/*
 * Copyright 2017 the original author or authors.
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
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.jline.utils.Signals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.ReflectionUtils;

/**
 * Main class implementing a shell loop.
 *
 * <p>
 * Given some textual input, locate the {@link MethodTarget} to invoke and
 * {@link ResultHandler#handleResult(Object) handle} the result.
 * </p>
 *
 * <p>
 * Also provides hooks for code completion
 * </p>
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 */
public class Shell {

	private final ResultHandlerService resultHandlerService;

	/**
	 * Marker object returned to signify that there was no input to turn into a command
	 * execution.
	 */
	public static final Object NO_INPUT = new Object();

	@Autowired
	protected ApplicationContext applicationContext;

	private final CommandRegistry commandRegistry;

	private Validator validator = Utils.defaultValidator();

	protected Map<String, MethodTarget> methodTargets = new HashMap<>();

	protected List<ParameterResolver> parameterResolvers;

	/**
	 * Marker object to distinguish unresolved arguments from {@code null}, which is a valid
	 * value.
	 */
	protected static final Object UNRESOLVED = new Object();

	public Shell(ResultHandlerService resultHandlerService, CommandRegistry commandRegistry) {
		this.resultHandlerService = resultHandlerService;
		this.commandRegistry = commandRegistry;
	}

	@Autowired(required = false)
	public void setValidatorFactory(ValidatorFactory validatorFactory) {
		this.validator = validatorFactory.getValidator();
	}

	@PostConstruct
	public void gatherMethodTargets() throws Exception {
		methodTargets = commandRegistry.listCommands();
		methodTargets.values()
				.forEach(this::validateParameterResolvers);
	}

	@Autowired
	public void setParameterResolvers(List<ParameterResolver> resolvers) {
		this.parameterResolvers = new ArrayList<>(resolvers);
		AnnotationAwareOrderComparator.sort(parameterResolvers);
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

		String line = String.join( " ", input.words() ).trim();
		String command = findLongestCommand(line);

		List<String> words = input.words();
		if (command != null) {
			MethodTarget methodTarget = methodTargets.get(command);
			Availability availability = methodTarget.getAvailability();
			if (availability.isAvailable()) {
				List<String> wordsForArgs = wordsForArguments(command, words);
				Method method = methodTarget.getMethod();

				Thread commandThread = Thread.currentThread();
				Object sh = Signals.register("INT", () -> commandThread.interrupt());
				try {
					Object[] args = resolveArgs(method, wordsForArgs);
					validateArgs(args, methodTarget);

					return ReflectionUtils.invokeMethod(method, methodTarget.getBean(), args);
				}
				catch (UndeclaredThrowableException e) {
					if (e.getCause() instanceof InterruptedException || e.getCause() instanceof ClosedByInterruptException) {
						Thread.interrupted(); // to reset interrupted flag
					}
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
				return new CommandNotCurrentlyAvailable(command, availability);
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

		List<CompletionProposal> candidates = new ArrayList<>( commandsStartingWith( prefix ) );

		String best = findLongestCommand(prefix);
		if (best != null) {
			CompletionContext argsContext = context.drop(best.split(" ").length);
			// Try to complete arguments
			MethodTarget methodTarget = methodTargets.get(best);
			Method method = methodTarget.getMethod();

			List<MethodParameter> parameters = Utils.createMethodParameters(method).collect(Collectors.toList());
			for (ParameterResolver resolver : parameterResolvers) {
				for ( MethodParameter parameter : parameters ) {
					if ( resolver.supports( parameter ) ) {
						candidates.addAll( resolver.complete( parameter, argsContext ) );
					}
				}
			}
		}
		return candidates;
	}

	private List<CompletionProposal> commandsStartingWith(String prefix) {
		// Workaround for https://github.com/spring-projects/spring-shell/issues/150
		// (sadly, this ties this class to JLine somehow)
		int lastWordStart = prefix.lastIndexOf(' ') + 1;
		return methodTargets.entrySet().stream()
				.filter(e -> e.getKey().startsWith(prefix))
				.map(e -> toCommandProposal(e.getKey().substring(lastWordStart), e.getValue()))
				.collect(Collectors.toList());
	}

	private CompletionProposal toCommandProposal(String command, MethodTarget methodTarget) {
		return new CompletionProposal(command)
				.dontQuote(true)
				.category("Available commands")
				.description(methodTarget.getHelp());
	}

	private void validateArgs(Object[] args, MethodTarget methodTarget) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] == UNRESOLVED) {
				MethodParameter methodParameter = Utils.createMethodParameter(methodTarget.getMethod(), i);
				throw new IllegalStateException("Could not resolve " + methodParameter);
			}
		}
		Set<ConstraintViolation<Object>> constraintViolations = validator.forExecutables().validateParameters(
				methodTarget.getBean(),
				methodTarget.getMethod(),
				args);
		if (constraintViolations.size() > 0) {
			throw new ParameterValidationException(constraintViolations, methodTarget);
		}
	}

	/**
	 * Use all known {@link ParameterResolver}s to try to compute a value for each parameter
	 * of the method to invoke.
	 * @param method the method for which parameters should be computed
	 * @param wordsForArgs the list of 'words' that should be converted to parameter values.
	 * May include markers for passing parameters 'by name'
	 * @return an array containing resolved parameter values, or {@link #UNRESOLVED} for
	 * parameters that could not be resolved
	 */
	private Object[] resolveArgs(Method method, List<String> wordsForArgs) {
		List<MethodParameter> parameters = Utils.createMethodParameters(method).collect(Collectors.toList());
		Object[] args = new Object[parameters.size()];
		Arrays.fill(args, UNRESOLVED);
		for (ParameterResolver resolver : parameterResolvers) {
			for (int argIndex = 0; argIndex < args.length; argIndex++) {
				MethodParameter parameter = parameters.get(argIndex);
				if (args[argIndex] == UNRESOLVED && resolver.supports(parameter)) {
					args[argIndex] = resolver.resolve(parameter, wordsForArgs).resolvedValue();
				}
			}
		}
		return args;
	}

	/**
	 * Verifies that we have at least one {@link ParameterResolver} that supports each of the
	 * {@link MethodParameter}s in the method.
	 */
	private void validateParameterResolvers(MethodTarget methodTarget) {
		Utils.createMethodParameters(methodTarget.getMethod())
				.forEach(parameter -> {
					parameterResolvers.stream()
							.filter(resolver -> resolver.supports(parameter))
							.findFirst()
							.orElseThrow(() -> new ParameterResolverMissingException(parameter));
				});
	}

	/**
	 * Returns the longest command that can be matched as first word(s) in the given buffer.
	 *
	 * @return a valid command name, or {@literal null} if none matched
	 */
	private String findLongestCommand(String prefix) {
		String result = methodTargets.keySet().stream()
				.filter(command -> prefix.equals(command) || prefix.startsWith(command + " "))
				.reduce("", (c1, c2) -> c1.length() > c2.length() ? c1 : c2);
		return "".equals(result) ? null : result;
	}

}
