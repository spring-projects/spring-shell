/*
 * Copyright 2015 the original author or authors.
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

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * Main component implementing a REPL using JLine.
 *
 * <p>Discovers {@link MethodTarget}s at startup and hands off execution of commands according
 * to the parsed JLine buffer.</p>
 *
 * @author Eric Bottard
 * @author Florent Biville
 */
@Component
public class JLineShell implements Shell {

	@Autowired
	ResultHandlers resultHandlers = new ResultHandlers();

	@Autowired
	private ApplicationContext applicationContext;

	private Map<String, MethodTarget> methodTargets = new HashMap<>();

	LineReader lineReader;

	@Autowired
	private Terminal terminal;

	@Autowired
	private List<ParameterResolver> parameterResolvers = new ArrayList<>();

	/**
	 * Marker object to distinguish unresolved arguments from {@code null}, which is a valid value.
	 */
	private static final Object UNRESOLVED = new Object();

	@Override
	public Map<String, MethodTarget> listCommands() {
		return methodTargets;
	}

	@PostConstruct
	public void init() throws Exception {
		for (MethodTargetResolver resolver : applicationContext.getBeansOfType(MethodTargetResolver.class).values()) {
			methodTargets.putAll(resolver.resolve(applicationContext));
		}

		LineReaderBuilder lineReaderBuilder = LineReaderBuilder.builder()
				.terminal(terminal)
				.appName("Foo")
				.completer(new CompleterAdapter())
				.highlighter(new Highlighter() {

					@Override
					public AttributedString highlight(LineReader reader, String buffer) {
						int l = 0;
						String best = null;
						for (String command : methodTargets.keySet()) {
							if (buffer.startsWith(command) && command.length() > l) {
								l = command.length();
								best = command;
							}
						}
						if (best != null) {
							return new AttributedStringBuilder(buffer.length()).append(best, AttributedStyle.BOLD).append(buffer.substring(l)).toAttributedString();
						}
						else {
							return new AttributedString(buffer, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
						}
					}
				})
				.parser(new DefaultParser());

		lineReader = lineReaderBuilder.build();

	}


	public void run() throws IOException {
		while (true) {
			try {
				lineReader.readLine(new AttributedString("shell:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)).toAnsi(terminal));
			}
			catch (UserInterruptException e) {
				if (e.getPartialLine().isEmpty()) {
					resultHandlers.handleResult(new ExitRequest(1));
				} else {
					continue;
				}
			}
			String separator = "";
			StringBuilder candidateCommand = new StringBuilder();
			MethodTarget methodTarget = null;
			int c = 0;
			int wordsUsedForCommandKey = 0;
			for (String word : lineReader.getParsedLine().words()) {
				c++;
				candidateCommand.append(separator).append(word);
				MethodTarget t = methodTargets.get(candidateCommand.toString());
				if (t != null) {
					methodTarget = t;
					wordsUsedForCommandKey = c;
				}
				separator = " ";
			}

			List<String> words = lineReader.getParsedLine().words();
			// TODO investigate trailing empty string in e.g. "help 'WTF 2'"
			words = words.stream().filter(w -> w.length() > 0).collect(Collectors.toList());
			if (methodTarget != null) {
				List<String> wordsForArgs = words.subList(wordsUsedForCommandKey, words.size());
				Method method = methodTarget.getMethod();

				Object result = null;
				try {
					Object[] args = resolveArgs(method, wordsForArgs);
					validateArgs(args, methodTarget);
					result = ReflectionUtils.invokeMethod(method, methodTarget.getBean(), args);
				}
				catch (Exception e) {
					result = e;
				}

				resultHandlers.handleResult(result);

			}
			else {
				System.out.println("No command found for " + words);
			}
		}
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
	 * A bridge between JLine's {@link Completer} contract and our own.
	 * @author Eric Bottard
	 */
	private class CompleterAdapter implements Completer {

		@Override
		public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
			String prefix = reader.getBuffer().upToCursor();

			String best = methodTargets.keySet().stream()
					.filter(c -> prefix.startsWith(c))
					.reduce("", (c1, c2) -> c1.length() > c2.length() ? c1 : c2);
			if (best.equals("")) { // no command found
				List<Candidate> result = commandsStartingWith(prefix);
				candidates.addAll(result);
				return;
			} // trying to complete args for command <best>,
			// or trying to complete command whose name starts with <best> (which also happens to be a command)
			else if (prefix.equals(best)) {
				candidates.addAll(commandsStartingWith(best));
				return;
			} // valid command (<best>) followed by a suffix (but not necessarily [<space> args*])
			else if (!prefix.startsWith(best + " ")) {
				// must be an invalid command, can't do anything
				return;
			}

			// Try to complete arguments
			MethodTarget methodTarget = methodTargets.get(best);
			List<String> words = line.words();
			int noOfWordsInCommand = best.split(" ").length;
			List<String> rest = words.subList(noOfWordsInCommand, words.size())
					.stream()
					.filter(w -> !w.isEmpty())
					.collect(Collectors.toList());
			CompletionContext context = new CompletionContext(rest, line.wordIndex() - noOfWordsInCommand, line.wordCursor());
			Method method = methodTarget.getMethod();
			for (int i = 0; i < method.getParameterCount(); i++) {
				MethodParameter methodParameter = Utils.createMethodParameter(method, i);
				ParameterResolver resolver = findResolver(methodParameter);
				resolver.complete(methodParameter, context)
						.stream()
						.map(completion -> new Candidate(
										completion.value(),
										completion.displayText(),
										"Comp for parameter " + resolver.describe(methodParameter).toString(),
										resolver.describe(methodParameter).help(),
										null, null, true)
						)
						.forEach(candidates::add);
			}
		}

		private List<Candidate> commandsStartingWith(String prefix) {
			return methodTargets.entrySet().stream()
					.filter(e -> e.getKey().startsWith(prefix)) // find commands that start with our buffer prefix
					.map(e -> toCandidate(e.getKey(), e.getValue()))
					.collect(Collectors.toList());
		}

		private Candidate toCandidate(String command, MethodTarget methodTarget) {
			return new Candidate(command, command, "Available commands", methodTarget.getHelp(), null, null, true);
		}
	}
}
