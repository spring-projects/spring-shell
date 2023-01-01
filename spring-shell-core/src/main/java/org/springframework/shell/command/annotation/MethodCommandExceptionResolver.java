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
package org.springframework.shell.command.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.shell.command.invocation.InvocableShellMethod;
import org.springframework.shell.command.invocation.ShellMethodArgumentResolverComposite;
import org.springframework.util.Assert;

public class MethodCommandExceptionResolver implements CommandExceptionResolver {

	private final static Logger log = LoggerFactory.getLogger(MethodCommandExceptionResolver.class);
	private final Object bean;
	private final Terminal terminal;

	public MethodCommandExceptionResolver(Object bean) {
		this(bean, null);
	}

	public MethodCommandExceptionResolver(Object bean, Terminal terminal) {
		Assert.notNull(bean, "Target bean must be set");
		this.bean = bean;
		this.terminal = terminal;
	}

	@Override
	public CommandHandlingResult resolve(Exception ex) {
		try {
			ExceptionResolverMethodResolver resolver = new ExceptionResolverMethodResolver(bean.getClass());
			Method exceptionResolverMethod = resolver.resolveMethodByThrowable(ex);
			if (exceptionResolverMethod == null) {
				return null;
			}
			InvocableShellMethod invocable = new InvocableShellMethod(bean, exceptionResolverMethod);

			ShellMethodArgumentResolverComposite argumentResolvers = new ShellMethodArgumentResolverComposite();
			argumentResolvers.addResolver(new TerminalResolver());
			invocable.setMessageMethodArgumentResolvers(argumentResolvers);

			ArrayList<Throwable> exceptions = new ArrayList<>();
			Throwable exToExpose = ex;
			while (exToExpose != null) {
				exceptions.add(exToExpose);
				Throwable cause = exToExpose.getCause();
				exToExpose = (cause != exToExpose ? cause : null);
			}
			Object[] arguments = new Object[exceptions.size() + 1];
			exceptions.toArray(arguments);

			MessageBuilder<Object[]> messageBuilder = MessageBuilder.withPayload(arguments);
			messageBuilder.setHeader("terminal", terminal);

			MethodParameter returnType = invocable.getReturnType();
			Class<?> parameterType = returnType.getParameterType();
			boolean isVoid = void.class.isAssignableFrom(parameterType);
			Object invoke = invocable.invoke(messageBuilder.build(), arguments);

			Integer ecFromAnn = null;
			ExitCode ecAnn = AnnotationUtils.findAnnotation(exceptionResolverMethod, ExitCode.class);
			if (ecAnn != null && ecAnn.code() > 0) {
				ecFromAnn = ecAnn.code();
			}

			if (isVoid) {
				if (ecFromAnn != null) {
					return CommandHandlingResult.of(null, ecFromAnn);
				}
				return CommandHandlingResult.empty();
			}
			else if (invoke instanceof CommandHandlingResult result) {
				if (ecFromAnn != null) {
					return CommandHandlingResult.of(result.message(), ecFromAnn);
				}
				return (CommandHandlingResult)invoke;
			}
			else if (invoke instanceof String msg) {
				if (ecFromAnn != null) {
					return CommandHandlingResult.of(msg, ecFromAnn);
				}
				return CommandHandlingResult.of(msg, 1);
			}
		}
		catch (Exception e) {
			// TODO: should think how to report this to user without logging
			log.warn("Failure in @ExceptionResolver", e);
		}
		return null;
	}

	private static class TerminalResolver implements HandlerMethodArgumentResolver {

		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return Terminal.class.isAssignableFrom(parameter.getParameterType());
		}

		@Override
		public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
			Terminal terminal = message.getHeaders().get("terminal", Terminal.class);
			return terminal;
		}

	}
}
