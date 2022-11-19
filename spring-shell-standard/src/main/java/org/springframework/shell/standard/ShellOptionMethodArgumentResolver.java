/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.standard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.shell.support.AbstractArgumentMethodArgumentResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Resolver for {@link ShellOption @ShellOption} arguments.
 *
 * @author Janne Valkealahti
 */
public class ShellOptionMethodArgumentResolver extends AbstractArgumentMethodArgumentResolver {

	public ShellOptionMethodArgumentResolver(ConversionService conversionService,
			@Nullable ConfigurableBeanFactory beanFactory) {
		super(conversionService, beanFactory);
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(ShellOption.class);
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		ShellOption annot = parameter.getParameterAnnotation(ShellOption.class);
		Assert.state(annot != null, "No ShellOption annotation");
		List<String> names = Arrays.stream(annot != null ? annot.value() : new String[0])
			.map(v -> StringUtils.trimLeadingCharacter(v, '-'))
			.collect(Collectors.toList());
		return new HeaderNamedValueInfo(annot, names);
	}

	@Override
	@Nullable
	protected Object resolveArgumentInternal(MethodParameter parameter, Message<?> message, List<String> names)
			throws Exception {
		for (String name : names) {
			if (message.getHeaders().containsKey(ARGUMENT_PREFIX + name)) {
				return message.getHeaders().get(ARGUMENT_PREFIX + name);
			}
		}
		return null;
	}

	@Override
	protected void handleMissingValue(List<String> headerName, MethodParameter parameter, Message<?> message) {
		throw new MessageHandlingException(message,
				"Missing headers '" + StringUtils.collectionToCommaDelimitedString(headerName)
						+ "' for method parameter type [" + parameter.getParameterType() + "]");
	}

	private static final class HeaderNamedValueInfo extends NamedValueInfo {

		private HeaderNamedValueInfo(ShellOption annotation, List<String> names) {
			super(names, false, null);
		}
	}
}
