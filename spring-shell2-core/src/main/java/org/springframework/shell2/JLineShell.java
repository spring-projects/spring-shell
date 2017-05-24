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
import java.util.function.Function;
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
import org.springframework.beans.factory.annotation.Qualifier;
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
	@Qualifier("main")
	ResultHandler resultHandler;

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
			methodTargets.putAll(resolver.resolve());
		}

		ExtendedDefaultParser parser = new ExtendedDefaultParser();
		parser.setEofOnUnclosedQuote(true);
		parser.setEofOnEscapedNewLine(true);

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
				.parser(parser);

		lineReader = lineReaderBuilder.build();

	}


	public void run() throws IOException {
		while (true) {
			try {
				lineReader.readLine(new AttributedString("shell:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)).toAnsi(terminal));
			}
			catch (UserInterruptException e) {
				if (e.getPartialLine().isEmpty()) {
					resultHandler.handleResult(new ExitRequest(1));
				} else {
					continue;
				}
			}

			String line = lineReader.getParsedLine().line();
			String command = findLongestCommand(line);

			List<String> words = lineReader.getParsedLine().words();
			if (command != null) {
				int wordsUsedForCommandKey = command.split(" ").length;
				MethodTarget methodTarget = methodTargets.get(command);
				List<String> wordsForArgs = sanitizeInput(words.subList(wordsUsedForCommandKey, words.size()));
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

				resultHandler.handleResult(result);

			}
			else {
				System.out.println("No command found for " + sanitizeInput(words));
			}
		}
	}

	/**
	 * Sanitize the buffer input given the customizations applied to the JLine parser (<em>e.g.</em> support for
	 * line continuations, <em>etc.</em>)
	 */
	private List<String> sanitizeInput(List<String> words) {
		words = words.stream()
			.map(s -> s.replaceAll("^\\n+|\\n+$", "")) // CR at beginning/end of line introduced by backslash continuation
			.map(s -> s.replaceAll("\\n+", " ")) // CR in middle of word introduced by return inside a quoted string
			.filter(w -> w.length() > 0) // Empty word introduced when using quotes, no idea why...
			.collect(Collectors.toList());
		return words;
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

			// Find the longest match for a command name with words in the buffer
			String best = findLongestCommand(prefix);
			if (best == null) { // no command found
				candidates.addAll(commandsStartingWith(prefix));
				return;
			} // if we're here, we're either trying to complete args for command <best> (will fall through)
			// or trying to complete command whose name starts with <best> (which also happens to be a command)
			else if (prefix.equals(best)) {
				candidates.addAll(commandsStartingWith(best));
			} // valid command (<best>) followed by a suffix (but not necessarily [<space> args*])
			else if (!prefix.startsWith(best + " ")) {
				// must be an invalid command, can't do anything
				return;
			}

			CompletingParsedLine cpl = (line instanceof CompletingParsedLine) ? ((CompletingParsedLine) line) : t -> t;

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
										cpl.emit(completion.value()).toString(),
										completion.displayText(),
										"Value for parameter " + resolver.describe(methodParameter).toString(),
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
}
