package org.springframework.shell.command.execution;

import jakarta.validation.Validator;
import org.jline.terminal.Terminal;
import org.springframework.core.convert.ConversionService;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.Availability;
import org.springframework.shell.CommandNotCurrentlyAvailable;
import org.springframework.shell.command.*;
import org.springframework.shell.command.invocation.InvocableShellMethod;
import org.springframework.shell.command.invocation.ShellMethodArgumentResolverComposite;
import org.springframework.shell.command.parser.ParserConfig;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of a {@link CommandExecution}.
 */
public class DefaultCommandExecution implements CommandExecution {

    private List<? extends HandlerMethodArgumentResolver> resolvers;
    private Validator validator;
    private Terminal terminal;
    private ConversionService conversionService;
    private CommandCatalog commandCatalog;

    public DefaultCommandExecution(List<? extends HandlerMethodArgumentResolver> resolvers, Validator validator,
                                   Terminal terminal, ConversionService conversionService, CommandCatalog commandCatalog) {
        this.resolvers = resolvers;
        this.validator = validator;
        this.terminal = terminal;
        this.conversionService = conversionService;
        this.commandCatalog = commandCatalog;
    }

    public Object evaluate(String[] args) {
        CommandParser parser = CommandParser.of(conversionService, commandCatalog.getRegistrations(), new ParserConfig());
        CommandParser.CommandParserResults commandParserResults = parser.parse(args);
        CommandRegistration commandRegistration = commandParserResults.registration();

        // fast fail with availability before doing anything else
        Availability availability = commandRegistration.getAvailability();
        if (availability != null && !availability.isAvailable()) {
            return new CommandNotCurrentlyAvailable(commandRegistration.getCommand(), availability);
        }

        // check help options to short circuit
        boolean handleHelpOption = false;
        CommandRegistration.HelpOptionInfo helpOption = commandRegistration.getHelpOption();
        if (helpIsRequested(helpOption)) {
            handleHelpOption = commandParserResults.results().stream()
                    .anyMatch(commandParserResult -> longOrShortHelpOptionExist(helpOption, commandParserResult));
        }

        // if needed switch registration to help command if we're short circuiting
        CommandRegistration usedRegistration;
        if (handleHelpOption) {
            String command = commandRegistration.getCommand();
            CommandParser helpParser = CommandParser.of(conversionService, commandCatalog.getRegistrations(),
                    new ParserConfig());
            CommandRegistration helpCommandRegistration = commandCatalog.getRegistrations()
                    .get(commandRegistration.getHelpOption().getCommand());
            commandParserResults = helpParser.parse(new String[] { "help", "--command", command });
            usedRegistration = helpCommandRegistration;
        }
        else {
            usedRegistration = commandRegistration;
        }

        if (!commandParserResults.errors().isEmpty()) {
            throw new CommandParserExceptionsException("Command parser resulted errors", commandParserResults.errors());
        }

        CommandContext ctx = CommandContext.of(args, commandParserResults, terminal, usedRegistration);

        return executeAndGetResult(args, commandParserResults, usedRegistration, ctx);
    }

    private Object executeAndGetResult(String[] args, CommandParser.CommandParserResults results, CommandRegistration usedRegistration, CommandContext ctx) {
        Object res = null;

        CommandRegistration.TargetInfo targetInfo = usedRegistration.getTarget();

        // pick the target to execute
        if (targetInfo.getTargetType() == CommandRegistration.TargetInfo.TargetType.FUNCTION) {
            res = targetInfo.getFunction().apply(ctx);
        }
        else if (targetInfo.getTargetType() == CommandRegistration.TargetInfo.TargetType.CONSUMER) {
            targetInfo.getConsumer().accept(ctx);
        }
        else if (targetInfo.getTargetType() == CommandRegistration.TargetInfo.TargetType.METHOD) {
            try {
                MessageBuilder<String[]> messageBuilder = MessageBuilder.withPayload(args);
                Map<String, Object> paramValues = new HashMap<>();
                results.results().forEach(r -> {
                    if (r.option().getLongNames() != null) {
                        for (String n : r.option().getLongNames()) {
                            messageBuilder.setHeader(ArgumentHeaderMethodArgumentResolver.ARGUMENT_PREFIX + n, r.value());
                            paramValues.put(n, r.value());
                        }
                    }
                    if (r.option().getShortNames() != null) {
                        for (Character n : r.option().getShortNames()) {
                            messageBuilder.setHeader(ArgumentHeaderMethodArgumentResolver.ARGUMENT_PREFIX + n.toString(), r.value());
                        }
                    }
                });
                messageBuilder.setHeader(CommandContextMethodArgumentResolver.HEADER_COMMAND_CONTEXT, ctx);

                InvocableShellMethod invocableShellMethod = new InvocableShellMethod(targetInfo.getBean(), targetInfo.getMethod());
                invocableShellMethod.setConversionService(conversionService);
                invocableShellMethod.setValidator(validator);
                ShellMethodArgumentResolverComposite argumentResolvers = new ShellMethodArgumentResolverComposite();
                if (resolvers != null) {
                    argumentResolvers.addResolvers(resolvers);
                }
                if (!paramValues.isEmpty()) {
                    argumentResolvers.addResolver(new ParamNameHandlerMethodArgumentResolver(paramValues, conversionService));
                }
                invocableShellMethod.setMessageMethodArgumentResolvers(argumentResolvers);

                res = invocableShellMethod.invoke(messageBuilder.build(), (Object[])null);

            } catch (Exception e) {
                throw new CommandExecutionException(e);
            }
        }

        return res;
    }

    //TODO re-think the name
    private boolean longOrShortHelpOptionExist(CommandRegistration.HelpOptionInfo helpOption, CommandParser.CommandParserResult commandParserResult) {
        boolean present = false;
        if (helpOption.getLongNames() != null) {
            present = Arrays.stream(commandParserResult.option().getLongNames())
                    .anyMatch(longName -> ObjectUtils.containsElement(helpOption.getLongNames(), longName));
        }
        if (present) {
            return true;
        }

        if (helpOption.getShortNames() != null) {
            present = Arrays.stream(commandParserResult.option().getShortNames())
                    .anyMatch(shortName -> ObjectUtils.containsElement(helpOption.getShortNames(), shortName));
        }
        return present;
    }

    private boolean helpIsRequested(CommandRegistration.HelpOptionInfo helpOption) {
        return helpOption.isEnabled() && helpOption.getCommand() != null && (!ObjectUtils.isEmpty(helpOption.getLongNames()) || !ObjectUtils.isEmpty(helpOption.getShortNames()));
    }
}

