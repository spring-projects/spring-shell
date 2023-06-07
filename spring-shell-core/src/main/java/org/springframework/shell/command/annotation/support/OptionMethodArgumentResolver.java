/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.command.annotation.support;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.support.AbstractArgumentMethodArgumentResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Resolver for {@link Option @Option} arguments.
 *
 * @author Janne Valkealahti
 */
public class OptionMethodArgumentResolver extends AbstractArgumentMethodArgumentResolver {

	public OptionMethodArgumentResolver(ConversionService conversionService,
			@Nullable ConfigurableBeanFactory beanFactory) {
		super(conversionService, beanFactory);
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Option.class);
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		Option annot = parameter.getParameterAnnotation(Option.class);
		Assert.state(annot != null, "No Option annotation");
		List<String> names = Arrays.stream(annot != null ? annot.longNames() : new String[0])
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

		private HeaderNamedValueInfo(Option annotation, List<String> names) {
			super(names, false, null);
		}
	}
}
