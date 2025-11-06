package org.springframework.shell.core.command;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import org.springframework.core.ResolvableType;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.metadata.CommandAlias;
import org.springframework.shell.core.command.metadata.CommandExitCode;
import org.springframework.shell.core.command.metadata.CommandHelpOptionInfo;
import org.springframework.shell.core.command.metadata.CommandOption;
import org.springframework.shell.core.command.metadata.CommandTarget;
import org.springframework.shell.core.command.metadata.CommandTarget.TargetType;
import org.springframework.shell.core.command.support.CommandUtils;
import org.springframework.shell.core.completion.CompletionResolver;
import org.springframework.shell.core.context.InteractionMode;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import static java.util.stream.Collectors.*;

class DefaultCommand implements Command {

	private final String command;

	private final InteractionMode interactionMode;

	private final @Nullable String group;

	private final boolean hidden;

	private final @Nullable String description;

	private final Supplier<Availability> availability;

	private final List<CommandOption> options;

	private final DefaultTargetSpec targetSpec;

	private final List<DefaultAliasSpec> aliasSpecs;

	private final @Nullable DefaultExitCodeSpec exitCodeSpec;

	private final @Nullable DefaultErrorHandlingSpec errorHandlingSpec;

	private final @Nullable DefaultHelpOptionsSpec helpOptionsSpec;

	private final @Nullable Function<String, String> defaultOptionNameModifier;

	public DefaultCommand(String command, InteractionMode interactionMode, @Nullable String group, boolean hidden,
			@Nullable String description, Supplier<Availability> availability, List<DefaultOptionSpec> optionSpecs,
			DefaultTargetSpec targetSpec, List<DefaultAliasSpec> aliasSpecs, @Nullable DefaultExitCodeSpec exitCodeSpec,
			@Nullable DefaultErrorHandlingSpec errorHandlingSpec, @Nullable DefaultHelpOptionsSpec helpOptionsSpec,
			@Nullable Function<String, String> defaultOptionNameModifier) {
		this.command = command;
		this.interactionMode = interactionMode;
		this.group = group;
		this.hidden = hidden;
		this.description = description;
		this.availability = availability;
		this.targetSpec = targetSpec;
		this.aliasSpecs = aliasSpecs;
		this.exitCodeSpec = exitCodeSpec;
		this.errorHandlingSpec = errorHandlingSpec;
		this.helpOptionsSpec = helpOptionsSpec;
		this.defaultOptionNameModifier = defaultOptionNameModifier;
		this.options = initOptions(optionSpecs);
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public InteractionMode getInteractionMode() {
		return interactionMode;
	}

	@Override
	public @Nullable String getGroup() {
		return group;
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public @Nullable String getDescription() {
		return description;
	}

	@Override
	public Availability getAvailability() {
		return availability.get();
	}

	@Override
	public List<CommandOption> getOptions() {
		return options;
	}

	@Override
	public CommandTarget getTarget() {
		TargetType targetType = null;
		if (targetSpec.bean != null) {
			targetType = TargetType.METHOD;
		}
		if (targetSpec.function != null) {
			targetType = TargetType.FUNCTION;
		}
		if (targetSpec.consumer != null) {
			targetType = TargetType.CONSUMER;
		}

		if (targetType == null) {
			throw new IllegalArgumentException("No bean, function or consumer defined");
		}

		return new CommandTarget(targetType, targetSpec.bean, targetSpec.method, targetSpec.function,
				targetSpec.consumer);
	}

	@Override
	public List<CommandAlias> getAliases() {
		return aliasSpecs.stream()
			.filter(spec -> StringUtils.hasText(spec.commands))
			.map(spec -> new CommandAlias(spec.commands, spec.group))
			.toList();
	}

	@Override
	public CommandExitCode getExitCode() {
		if (this.exitCodeSpec == null) {
			return CommandExitCode.empty();
		}
		else {
			return new CommandExitCode(exitCodeSpec.functions);
		}
	}

	@Override
	public List<CommandExceptionResolver> getExceptionResolvers() {
		if (this.errorHandlingSpec == null) {
			return Collections.emptyList();
		}
		else {
			return this.errorHandlingSpec.resolvers;
		}
	}

	@Override
	public CommandHelpOptionInfo getHelpOption() {
		if (this.helpOptionsSpec == null) {
			return CommandHelpOptionInfo.empty();
		}
		else {
			return new CommandHelpOptionInfo(helpOptionsSpec.enabled, helpOptionsSpec.longNames,
					helpOptionsSpec.shortNames, helpOptionsSpec.command);
		}
	}

	private List<CommandOption> initOptions(List<DefaultOptionSpec> optionSpecs) {
		List<CommandOption> commandOptions = optionSpecs.stream().map(o -> {
			String[] longNames = o.longNames;
			String[] longNamesModified = new String[0];
			Function<String, String> modifier = o.nameModifier != null ? o.nameModifier : defaultOptionNameModifier;
			if (modifier != null) {
				longNamesModified = Arrays.copyOf(longNames, longNames.length);
				longNames = Arrays.stream(longNames).map(modifier).toArray(String[]::new);
			}
			return new CommandOption(longNames, longNamesModified, o.shortNames, o.description, o.type, o.required,
					o.defaultValue, o.position, o.arityMin, o.arityMax, o.label, o.completion);
		}).collect(toList());

		if (helpOptionsSpec != null) {
			String[] longNames = helpOptionsSpec.longNames != null ? helpOptionsSpec.longNames : new String[0];
			Character[] shortNames = helpOptionsSpec.shortNames != null ? helpOptionsSpec.shortNames : new Character[0];
			String desc = "help for %s".formatted(command);
			CommandOption commandOption = CommandOption.of(longNames, shortNames, desc,
					ResolvableType.forType(void.class));
			commandOptions.add(commandOption);
		}

		return commandOptions;
	}

	static class DefaultOptionSpec implements OptionSpec {

		private String[] longNames = new String[0];

		private Character[] shortNames = new Character[0];

		private @Nullable ResolvableType type;

		private @Nullable String description;

		private boolean required;

		private @Nullable String defaultValue;

		private int position = -1;

		private int arityMin = -1;

		private int arityMax = -1;

		private @Nullable String label;

		private @Nullable CompletionResolver completion;

		private @Nullable Function<String, String> nameModifier;

		@Override
		public OptionSpec longNames(String... names) {
			this.longNames = names;
			return this;
		}

		@Override
		public OptionSpec shortNames(Character... names) {
			this.shortNames = names;
			return this;
		}

		@Override
		public OptionSpec type(Type type) {
			return type(ResolvableType.forType(type));
		}

		@Override
		public OptionSpec type(ResolvableType type) {
			this.type = type;
			return this;
		}

		@Override
		public OptionSpec description(String description) {
			this.description = description;
			return this;
		}

		@Override
		public OptionSpec required(boolean required) {
			this.required = required;
			return this;
		}

		@Override
		public OptionSpec required() {
			return required(true);
		}

		@Override
		public OptionSpec defaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		@Override
		public OptionSpec position(int position) {
			this.position = position;
			return this;
		}

		@Override
		public OptionSpec arity(int min, int max) {
			Assert.isTrue(min > -1, "arity min must be 0 or more");
			Assert.isTrue(max >= min, "arity max must be equal more than min");
			this.arityMin = min;
			this.arityMax = max;
			return this;
		}

		@Override
		public OptionSpec arity(OptionArity arity) {
			return switch (arity) {
				case ZERO -> arity(0, 0);
				case ZERO_OR_ONE -> arity(0, 1);
				case EXACTLY_ONE -> arity(1, 1);
				case ZERO_OR_MORE -> arity(0, Integer.MAX_VALUE);
				case ONE_OR_MORE -> arity(1, Integer.MAX_VALUE);
				case NONE -> this;
			};
		}

		@Override
		public OptionSpec label(String label) {
			this.label = label;
			return this;
		}

		@Override
		public OptionSpec completion(CompletionResolver completion) {
			this.completion = completion;
			return this;
		}

		@Override
		public OptionSpec nameModifier(Function<String, String> nameModifier) {
			this.nameModifier = nameModifier;
			return this;
		}

	}

	static class DefaultAliasSpec implements AliasSpec {

		private @Nullable String commands;

		private @Nullable String group;

		@Override
		public AliasSpec command(String... commands) {
			this.commands = CommandUtils.toCommand(commands);
			return this;
		}

		@Override
		public AliasSpec group(String group) {
			this.group = group;
			return this;
		}

	}

	static class DefaultHelpOptionsSpec implements HelpOptionsSpec {

		private boolean enabled = true;

		private @Nullable String command;

		private String @Nullable [] longNames;

		private Character @Nullable [] shortNames;

		@Override
		public HelpOptionsSpec enabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		@Override
		public HelpOptionsSpec command(String command) {
			this.command = command;
			return this;
		}

		@Override
		public HelpOptionsSpec longNames(String... longNames) {
			this.longNames = longNames;
			return this;
		}

		@Override
		public HelpOptionsSpec shortNames(Character... shortNames) {
			this.shortNames = shortNames;
			return this;
		}

	}

	static class DefaultTargetSpec implements TargetSpec {

		private @Nullable Object bean;

		private @Nullable Method method;

		private @Nullable Function<CommandContext, ?> function;

		private @Nullable Consumer<CommandContext> consumer;

		@Override
		public TargetSpec method(Object bean, Method method) {
			checkTarget(function);
			this.bean = bean;
			this.method = method;
			return this;
		}

		@Override
		public TargetSpec method(Object bean, String method, @Nullable Class<?>... paramTypes) {
			checkTarget(function);
			this.bean = bean;
			this.method = ReflectionUtils.findMethod(bean.getClass(), method,
					ObjectUtils.isEmpty(paramTypes) ? null : paramTypes);
			return this;
		}

		@Override
		public TargetSpec function(Function<CommandContext, ?> function) {
			checkTarget(bean);
			this.function = function;
			return this;
		}

		@Override
		public TargetSpec consumer(Consumer<CommandContext> consumer) {
			this.consumer = consumer;
			return this;
		}

		private void checkTarget(@Nullable Object target) {
			Assert.state(target == null, "only one target can exist");
		}

	}

	static class DefaultExitCodeSpec implements ExitCodeSpec {

		private final List<Function<Throwable, Integer>> functions = new ArrayList<>();

		@Override
		public ExitCodeSpec map(Class<? extends Throwable> e, int code) {
			Function<Throwable, Integer> f = t -> {
				if (ObjectUtils.nullSafeEquals(t.getClass(), e)) {
					return code;
				}
				return 0;
			};
			this.functions.add(f);
			return this;
		}

		@Override
		public ExitCodeSpec map(Function<Throwable, Integer> function) {
			this.functions.add(function);
			return this;
		}

	}

	static class DefaultErrorHandlingSpec implements ErrorHandlingSpec {

		private final List<CommandExceptionResolver> resolvers = new ArrayList<>();

		@Override
		public ErrorHandlingSpec resolver(CommandExceptionResolver resolver) {
			this.resolvers.add(resolver);
			return this;
		}

	}

}
