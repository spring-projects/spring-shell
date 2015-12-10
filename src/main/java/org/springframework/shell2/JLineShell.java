package org.springframework.shell2;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * Created by ericbottard on 26/11/15.
 */
@Component
public class JLineShell {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ConversionService conversionService;

	private Map<String, Object> commandBeans;

	private Map<String, MethodTarget> methodTargets = new HashMap<>();

	private LineReader lineReader;

	@Autowired
	private List<ParameterResolver> parameterResolvers = new ArrayList<>();

	@PostConstruct
	public void init() throws Exception {
		for (MethodTargetResolver resolver : applicationContext.getBeansOfType(MethodTargetResolver.class).values()) {
			methodTargets.putAll(resolver.resolve(applicationContext));
		}

		Terminal terminal = TerminalBuilder.builder().build();
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
						if (buffer.length() < 4) {
							return new AttributedString(buffer, AttributedStyle.DEFAULT.blink().foreground(AttributedStyle.RED));
						}
						else {
							return new AttributedString(buffer);
						}
					}
				})
				.parser(new DefaultParser());

		lineReader = lineReaderBuilder.build();

	}


	public void run() {
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
			if (methodTarget != null) {
				Parameter[] parameters = methodTarget.getMethod().getParameters();
				Object[] rawArgs = new Object[parameters.length];
				Object unresolved = new Object();
				Arrays.fill(rawArgs, unresolved);
				for (int i = 0; i < parameters.length; i++) {
					MethodParameter methodParameter = new MethodParameter(methodTarget.getMethod(), i);
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
				System.out.println(constraintViolations);

				Object result = null;
				try {
					result = ReflectionUtils.invokeMethod(methodTarget.getMethod(), methodTarget.getBean(), rawArgs);
				}
				catch (Exception e) {
					result = e;
				}

				System.out.println(String.valueOf(result));

			}
			else {
				System.out.println("No command found for " + words);
			}
		}
	}
}
