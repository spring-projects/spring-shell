/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell2;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.executable.ExecutableValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;

/**
 * Main class implementing a shell loop.
 *
 * <p>Given some textual input, locate the {@link MethodTarget} to invoke and {@link ResultHandler#handleResult(Object) handle}
 * the result.</p>
 *
 * <p>Also provides hooks for code completion</p>
 *
 * @author Eric Bottard
 */
public class Shell implements CommandRegistry {

	private final InputProvider inputProvider;

	private final ResultHandler resultHandler;

	@Autowired
	protected ApplicationContext applicationContext;

	protected Map<String, MethodTarget> methodTargets = new HashMap<>();

	@Autowired
	protected List<ParameterResolver> parameterResolvers = new ArrayList<>();

	/**
	 * Marker object to distinguish unresolved arguments from {@code null}, which is a valid value.
	 */
	protected static final Object UNRESOLVED = new Object();

	public Shell(InputProvider inputProvider, ResultHandler resultHandler) {
		this.inputProvider = inputProvider;
		this.resultHandler = resultHandler;
	}

	@Override
	public Map<String, MethodTarget> listCommands() {
		return methodTargets;
	}

	@PostConstruct
	public void gatherMethodTargets() throws Exception {
		for (MethodTargetResolver resolver : applicationContext.getBeansOfType(MethodTargetResolver.class).values()) {
			methodTargets.putAll(resolver.resolve());
		}
	}

	/**
	 * The main program loop: acquire input, try to match it to a command and evaluate. Repeat until a
	 * {@link ResultHandler} causes the process to exit.
	 */
	public void run() throws IOException {
		while (true) {
			Input input;
			try {
				input = inputProvider.readInput();
			}
			catch (Exception e) {
				resultHandler.handleResult(e);
				continue;
			}
			if (input.words().isEmpty()) {
				continue;
			}


			String line = input.words().stream().collect(Collectors.joining(" ")).trim();
			List<String> words = input.words();

			String command = findLongestCommand(line);

			Object result;
			if (command != null) {
				int wordsUsedForCommandKey = command.split(" ").length;
				MethodTarget methodTarget = methodTargets.get(command);
				List<String> wordsForArgs = words.subList(wordsUsedForCommandKey, words.size());
				Method method = methodTarget.getMethod();

				try {
					Object[] args = resolveArgs(method, wordsForArgs);
					validateArgs(args, methodTarget);
					result = ReflectionUtils.invokeMethod(method, methodTarget.getBean(), args);
				}
				catch (Exception e) {
					result = e;
				}
			}
			else {
				result = new CommandNotFound(words);
			}
			resultHandler.handleResult(result);
		}
	}

	/**
	 * Gather completion proposals given some (incomplete) input the user has already typed in.
	 * When and how this method is invoked is implementation specific and decided by the actual user interface.
	 */
	public List<CompletionProposal> complete(CompletionContext context) {

		String prefix = context.upToCursor();

		List<CompletionProposal> candidates = new ArrayList<>();
		// Find the longest match for a command name with words in the buffer
		String best = findLongestCommand(prefix);
		if (best == null) { // no command found
			candidates.addAll(commandsStartingWith(prefix));
			return candidates;
		} // if we're here, we're either trying to complete args for command <best> (will fall through)
		// or trying to complete command whose name starts with <best> (which also happens to be a command)
		else if (prefix.equals(best)) {
			candidates.addAll(commandsStartingWith(best));
		} // valid command (<best>) followed by a suffix (but not necessarily [<space> args*])
		else if (!prefix.startsWith(best + " ")) {
			// must be an invalid command, can't do anything
			return candidates;
		}

		// Try to complete arguments
		MethodTarget methodTarget = methodTargets.get(best);
		Method method = methodTarget.getMethod();
		return Arrays.stream(method.getParameters())
			.map(Utils::createMethodParameter)
			.flatMap(mp -> findResolver(mp).complete(mp, context).stream())
			.collect(Collectors.toList());
	}

	private List<CompletionProposal> commandsStartingWith(String prefix) {
		return methodTargets.entrySet().stream()
			.filter(e -> e.getKey().startsWith(prefix))
			.map(e -> toCompletionProposal(e.getKey(), e.getValue()))
			.collect(Collectors.toList());
	}

	private CompletionProposal toCompletionProposal(String command, MethodTarget methodTarget) {
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
		ExecutableValidator executableValidator = Validation
			.buildDefaultValidatorFactory().getValidator().forExecutables();
		Set<ConstraintViolation<Object>> constraintViolations = executableValidator.validateParameters(methodTarget.getBean(),
			methodTarget.getMethod(),
			args);
		if (constraintViolations.size() > 0) {
			System.out.println(constraintViolations);
		}
	}

	/**
	 * Use all known {@link ParameterResolver}s to try to compute a value for each parameter of the method to
	 * invoke.
	 * @param method       the method for which parameters should be computed
	 * @param wordsForArgs the list of 'words' that should be converted to parameter values.
	 *                     May include markers for passing parameters 'by name'
	 * @return an array containing resolved parameter values, or {@link #UNRESOLVED} for parameters that could not be
	 * resolved
	 */
	private Object[] resolveArgs(Method method, List<String> wordsForArgs) {
		Parameter[] parameters = method.getParameters();
		Object[] args = new Object[parameters.length];
		Arrays.fill(args, UNRESOLVED);
		for (int i = 0; i < parameters.length; i++) {
			MethodParameter methodParameter = Utils.createMethodParameter(method, i);
			args[i] = findResolver(methodParameter).resolve(methodParameter, wordsForArgs);
		}
		return args;
	}

	private ParameterResolver findResolver(MethodParameter parameter) {
		return parameterResolvers.stream()
			.filter(resolver -> resolver.supports(parameter))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("resolver not found"));
	}


	/**
	 * Returns the longest command that can be matched as first word(s) in the given buffer.
	 *
	 * @return a valid command name, or {@literal null} if none matched
	 */
	private String findLongestCommand(String prefix) {
		String result = methodTargets.keySet().stream()
			.filter(prefix::startsWith)
			.reduce("", (c1, c2) -> c1.length() > c2.length() ? c1 : c2);
		return "".equals(result) ? null : result;
	}

	public interface InputProvider {
		/**
		 * Return text entered by user to invoke commands.
		 */
		Input readInput();
	}


}
