/*
 * Copyright 2017-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.jline.terminal.Terminal;
import org.jline.utils.Signals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandExecution;
import org.springframework.shell.command.CommandExecution.CommandExecutionException;
import org.springframework.shell.command.CommandExecution.CommandExecutionHandlerMethodArgumentResolvers;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.completion.CompletionResolver;
import org.springframework.util.StringUtils;

/**
 * Main class implementing a shell loop.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 */
public class Shell {

	private final static Logger log = LoggerFactory.getLogger(Shell.class);
	private final ResultHandlerService resultHandlerService;

	/**
	 * Marker object returned to signify that there was no input to turn into a command
	 * execution.
	 */
	public static final Object NO_INPUT = new Object();

	private final Terminal terminal;
	private final CommandCatalog commandRegistry;
	protected List<CompletionResolver> completionResolvers = new ArrayList<>();
	private CommandExecutionHandlerMethodArgumentResolvers argumentResolvers;

	/**
	 * Marker object to distinguish unresolved arguments from {@code null}, which is a valid
	 * value.
	 */
	protected static final Object UNRESOLVED = new Object();

	private Validator validator = Utils.defaultValidator();

	public Shell(ResultHandlerService resultHandlerService, CommandCatalog commandRegistry, Terminal terminal) {
		this.resultHandlerService = resultHandlerService;
		this.commandRegistry = commandRegistry;
		this.terminal = terminal;
	}

	@Autowired
	public void setCompletionResolvers(List<CompletionResolver> resolvers) {
		this.completionResolvers = new ArrayList<>(resolvers);
		AnnotationAwareOrderComparator.sort(completionResolvers);
	}

	@Autowired
	public void setArgumentResolvers(CommandExecutionHandlerMethodArgumentResolvers argumentResolvers) {
		this.argumentResolvers = argumentResolvers;
	}

	@Autowired(required = false)
	public void setValidatorFactory(ValidatorFactory validatorFactory) {
		this.validator = validatorFactory.getValidator();
	}

	/**
	 * The main program loop: acquire input, try to match it to a command and evaluate. Repeat
	 * until a {@link ResultHandler} causes the process to exit or there is no input.
	 * <p>
	 * This method has public visibility so that it can be invoked by actual commands
	 * (<em>e.g.</em> a {@literal script} command).
	 * </p>
	 */
	public void run(InputProvider inputProvider) throws IOException {
		Object result = null;
		while (!(result instanceof ExitRequest)) { // Handles ExitRequest thrown from Quit command
			Input input;
			try {
				input = inputProvider.readInput();
			}
			catch (Exception e) {
				if (e instanceof ExitRequest) { // Handles ExitRequest thrown from hitting CTRL-C
					break;
				}
				resultHandlerService.handle(e);
				continue;
			}
			if (input == null) {
				break;
			}

			result = evaluate(input);
			if (result != NO_INPUT && !(result instanceof ExitRequest)) {
				resultHandlerService.handle(result);
			}
		}
	}

	/**
	 * Evaluate a single "line" of input from the user by trying to map words to a command and
	 * arguments.
	 *
	 * <p>
	 * This method does not throw exceptions, it catches them and returns them as a regular
	 * result
	 * </p>
	 */
	public Object evaluate(Input input) {
		if (noInput(input)) {
			return NO_INPUT;
		}

		String line = input.words().stream().collect(Collectors.joining(" ")).trim();
		String command = findLongestCommand(line);

		List<String> words = input.words();
		log.debug("Evaluate input with line=[{}], command=[{}]", line, command);
		if (command != null) {

			Optional<CommandRegistration> commandRegistration = commandRegistry.getRegistrations().values().stream()
				.filter(r -> {
					String c = StringUtils.arrayToDelimitedString(r.getCommands(), " ");
					return c.equals(command);
				})
				.findFirst();

			if (commandRegistration.isPresent()) {
				List<String> wordsForArgs = wordsForArguments(command, words);

				Thread commandThread = Thread.currentThread();
				Object sh = Signals.register("INT", () -> commandThread.interrupt());
				try {
					CommandExecution execution = CommandExecution
							.of(argumentResolvers != null ? argumentResolvers.getResolvers() : null, validator, terminal);
					return execution.evaluate(commandRegistration.get(), wordsForArgs.toArray(new String[0]));
				}
				catch (UndeclaredThrowableException e) {
					if (e.getCause() instanceof InterruptedException || e.getCause() instanceof ClosedByInterruptException) {
						Thread.interrupted(); // to reset interrupted flag
					}
					return e.getCause();
				}
				catch (CommandExecutionException e) {
					return e.getCause();
				}
				catch (Exception e) {
					return e;
				}
				finally {
					Signals.unregister("INT", sh);
				}
			}
			else {
				return new CommandNotFound(words);
			}
		}
		else {
			return new CommandNotFound(words);
		}
	}


	/**
	 * Return true if the parsed input ends up being empty (<em>e.g.</em> hitting ENTER on an
	 * empty line or blank space).
	 *
	 * <p>
	 * Also returns true (<em>i.e.</em> ask to ignore) when input starts with {@literal //},
	 * which is used for comments.
	 * </p>
	 */
	private boolean noInput(Input input) {
		return input.words().isEmpty()
				|| (input.words().size() == 1 && input.words().get(0).trim().isEmpty())
				|| (input.words().iterator().next().matches("\\s*//.*"));
	}

	/**
	 * Returns the list of words to be considered for argument resolving. Drops the first N
	 * words used for the command, as well as an optional empty word at the end of the list
	 * (which may be present if user added spaces before submitting the buffer)
	 */
	private List<String> wordsForArguments(String command, List<String> words) {
		int wordsUsedForCommandKey = command.split(" ").length;
		List<String> args = words.subList(wordsUsedForCommandKey, words.size());
		int last = args.size() - 1;
		if (last >= 0 && "".equals(args.get(last))) {
			args.remove(last);
		}
		return args;
	}

	/**
	 * Gather completion proposals given some (incomplete) input the user has already typed
	 * in. When and how this method is invoked is implementation specific and decided by the
	 * actual user interface.
	 */
	public List<CompletionProposal> complete(CompletionContext context) {

		String prefix = context.upToCursor();

		List<CompletionProposal> candidates = new ArrayList<>();
		candidates.addAll(commandsStartingWith(prefix));

		String best = findLongestCommand(prefix);
		if (best != null) {
			CompletionContext argsContext = context.drop(best.split(" ").length);
			// Try to complete arguments
			CommandRegistration registration = commandRegistry.getRegistrations().get(best);

			for (CompletionResolver resolver : completionResolvers) {
				List<CompletionProposal> resolved = resolver.resolve(registration, argsContext);
				candidates.addAll(resolved);
			}
		}
		return candidates;
	}

	private List<CompletionProposal> commandsStartingWith(String prefix) {
		// Workaround for https://github.com/spring-projects/spring-shell/issues/150
		// (sadly, this ties this class to JLine somehow)
		int lastWordStart = prefix.lastIndexOf(' ') + 1;
		return commandRegistry.getRegistrations().values().stream()
			.filter(r -> {
				String c = StringUtils.arrayToDelimitedString(r.getCommands(), " ");
				return c.startsWith(prefix);
			})
			.map(r -> {
				String c = StringUtils.arrayToDelimitedString(r.getCommands(), " ");
				c = c.substring(lastWordStart);
				return toCommandProposal(c, r);
			})
			.collect(Collectors.toList());
	}

	private CompletionProposal toCommandProposal(String command, CommandRegistration registration) {
		return new CompletionProposal(command)
				.dontQuote(true)
				.category("Available commands")
				.description(registration.getHelp());
	}

	/**
	 * Returns the longest command that can be matched as first word(s) in the given buffer.
	 *
	 * @return a valid command name, or {@literal null} if none matched
	 */
	private String findLongestCommand(String prefix) {
		String result = commandRegistry.getRegistrations().keySet().stream()
				.filter(command -> prefix.equals(command) || prefix.startsWith(command + " "))
				.reduce("", (c1, c2) -> c1.length() > c2.length() ? c1 : c2);
		return "".equals(result) ? null : result;
	}
}
