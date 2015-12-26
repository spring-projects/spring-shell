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

import java.io.Closeable;
import java.io.IOException;
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
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.shell2.result.ResultHandlers;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * Created by ericbottard on 26/11/15.
 */
@Component
public class JLineShell implements Shell {

	@Autowired
	private ResultHandlers resultHandlers;

	@Autowired
	private ApplicationContext applicationContext;

	private Map<String, MethodTarget> methodTargets = new HashMap<>();

	private LineReader lineReader;

	@Autowired
	private Terminal terminal;

	@Autowired
	private List<ParameterResolver> parameterResolvers = new ArrayList<>();

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
				.completer(new Completer() {

					@Override
					public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
						candidates.add(new Candidate("value", "displayed value", "v", "the description", null, null, false));
						candidates.add(new Candidate("value1", "displayed value1", "v", "the description of v1", null, null, false));
					}
				})
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
			lineReader.readLine("shell:>");
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
				Parameter[] parameters = methodTarget.getMethod().getParameters();
				Object[] rawArgs = new Object[parameters.length];
				Object unresolved = new Object();
				Arrays.fill(rawArgs, unresolved);
				for (int i = 0; i < parameters.length; i++) {
					MethodParameter methodParameter = new MethodParameter(methodTarget.getMethod(), i);
					methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
					for (ParameterResolver resolver : parameterResolvers) {
						if (resolver.supports(methodParameter)) {
							rawArgs[i] = resolver.resolve(methodParameter, words.subList(wordsUsedForCommandKey, words.size()));
							break;
						}
					}
					if (rawArgs[i] == unresolved) {
						throw new IllegalStateException("Could not resolve " + methodParameter);
					}
				}

				ExecutableValidator executableValidator = Validation
						.buildDefaultValidatorFactory().getValidator().forExecutables();
				Set<ConstraintViolation<Object>> constraintViolations = executableValidator.validateParameters(methodTarget.getBean(),
						methodTarget.getMethod(),
						rawArgs);
				if (constraintViolations.size() > 0) {
					System.out.println(constraintViolations);
				}

				Object result = null;
				try {
					result = ReflectionUtils.invokeMethod(methodTarget.getMethod(), methodTarget.getBean(), rawArgs);
				}
				catch (ExitRequest e) {
					if (applicationContext instanceof Closeable) {
						((Closeable)applicationContext).close();
						System.exit(e.status());
					}
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

}
