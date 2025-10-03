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

import java.util.Optional;

import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;

/**
 * Implementation of a {@link HandlerMethodArgumentResolver} resolving
 * {@link CommandContext}.
 *
 * @author Janne Valkealahti
 */
public class CommandContextMethodArgumentResolver implements HandlerMethodArgumentResolver {

	public static final String HEADER_COMMAND_CONTEXT = "springShellCommandContext";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		MethodParameter nestedParameter = parameter.nestedIfOptional();
		Class<?> paramType = nestedParameter.getNestedParameterType();
		return CommandContext.class.isAssignableFrom(paramType);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, Message<?> message) {
		CommandContext commandContext = message.getHeaders().get(HEADER_COMMAND_CONTEXT, CommandContext.class);
		return parameter.isOptional() ? Optional.ofNullable(commandContext) : commandContext;
	}

}
