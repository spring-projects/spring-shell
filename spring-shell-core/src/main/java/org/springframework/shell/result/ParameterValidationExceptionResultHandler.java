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

package org.springframework.shell.result;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.ElementKind;
import javax.validation.Path;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.shell.ParameterDescription;
import org.springframework.shell.ParameterResolver;
import org.springframework.shell.ParameterValidationException;
import org.springframework.shell.Utils;

/**
 * Displays validation errors on the terminal.
 *
 * @author Eric Bottard
 */
public class ParameterValidationExceptionResultHandler
		extends TerminalAwareResultHandler<ParameterValidationException> {

	public ParameterValidationExceptionResultHandler(Terminal terminal) {
		super(terminal);
	}

	@Autowired
	private List<ParameterResolver> parameterResolvers;

	@Override
	protected void doHandleResult(ParameterValidationException result) {
		terminal.writer().println(new AttributedString("The following constraints were not met:",
				AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi());
		result.getConstraintViolations().stream()
				.forEach(v -> {
					Optional<Integer> parameterIndex = StreamSupport.stream(v.getPropertyPath().spliterator(), false)
							.filter(n -> n.getKind() == ElementKind.PARAMETER)
							.map(n -> ((Path.ParameterNode) n).getParameterIndex())
							.findFirst();

					MethodParameter methodParameter = Utils.createMethodParameter(result.getMethodTarget().getMethod(),
							parameterIndex.get());
					List<ParameterDescription> descriptions = findParameterResolver(methodParameter)
							.describe(methodParameter).collect(Collectors.toList());
					if (descriptions.size() == 1) {
						ParameterDescription description = descriptions.get(0);
						AttributedStringBuilder ansi = new AttributedStringBuilder(100);
						ansi.append("\t").append(description.keys().get(0), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED).bold());
						ansi.append(" ").append(description.formal(), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED).underline());
						String msg = String.format(" : %s (You passed '%s')",
								v.getMessage(),
								String.valueOf(v.getInvalidValue())
						);
						ansi.append(msg, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));

						terminal.writer().println(ansi.toAnsi(terminal));
					}
					// Several formals for one method param, must be framework like JCommander, etc
					else {
						// Output toString() for now...
						terminal.writer().println(new AttributedString(v.toString(),
								AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi(terminal));
					}

				});
	}

	private ParameterResolver findParameterResolver(MethodParameter methodParameter) {
		return parameterResolvers.stream().filter(pr -> pr.supports(methodParameter)).findFirst().get();
	}
}
