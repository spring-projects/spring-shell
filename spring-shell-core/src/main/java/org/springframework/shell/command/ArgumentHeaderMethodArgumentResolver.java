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
package org.springframework.shell.command;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.shell.support.AbstractArgumentMethodArgumentResolver;
import org.springframework.util.Assert;

/**
 * Resolver for {@link Header @Header} arguments.
 *
 * @author Janne Valkealahti
 */
public class ArgumentHeaderMethodArgumentResolver extends AbstractArgumentMethodArgumentResolver {

	public ArgumentHeaderMethodArgumentResolver(ConversionService conversionService,
			@Nullable ConfigurableBeanFactory beanFactory) {
		super(conversionService, beanFactory);
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Header.class);
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		Header annot = parameter.getParameterAnnotation(Header.class);
		Assert.state(annot != null, "No Header annotation");
		return new HeaderNamedValueInfo(annot);
	}

	@Override
	@Nullable
	protected Object resolveArgumentInternal(MethodParameter parameter, Message<?> message, List<String> names)
			throws Exception {
		if (names.size() == 1) {
			return message.getHeaders().get(ARGUMENT_PREFIX + names.get(0));
		}
		else {
			return null;
		}
	}

	@Override
	protected void handleMissingValue(List<String> headerName, MethodParameter parameter, Message<?> message) {
		throw new MessageHandlingException(message, "Missing header '" + headerName +
				"' for method parameter type [" + parameter.getParameterType() + "]");
	}

	private static final class HeaderNamedValueInfo extends NamedValueInfo {

		private HeaderNamedValueInfo(Header annotation) {
			super(Arrays.asList(annotation.name()), annotation.required(), annotation.defaultValue());
		}
	}
}
