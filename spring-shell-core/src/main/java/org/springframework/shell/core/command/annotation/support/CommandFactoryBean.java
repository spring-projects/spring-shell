/*
 * Copyright 2023-present the original author or authors.
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
package org.springframework.shell.core.command.annotation.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jline.terminal.Terminal;
import org.jspecify.annotations.Nullable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.shell.core.InputProvider;
import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.commands.adapter.MethodInvokerCommandAdapter;
import org.springframework.util.Assert;
import org.springframework.util.MethodInvoker;
import org.springframework.util.StringUtils;

/**
 * {@link ShellRunner} that bootstraps the shell in interactive mode.
 * <p>
 * It requires an {@link InputProvider} to read user input, a {@link Terminal} to write
 * output, and a {@link CommandRegistry} to look up and execute commands.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public class CommandFactoryBean
		implements ApplicationContextAware, FactoryBean<org.springframework.shell.core.command.Command> {

	private final Log log = LogFactory.getLog(CommandFactoryBean.class);

	private final Method method;

	@SuppressWarnings("NullAway.Init")
	private ApplicationContext applicationContext;

	public CommandFactoryBean(Method method) {
		Assert.notNull(method, "'method' must not be null");
		this.method = method;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public org.springframework.shell.core.command.Command getObject() {
		List<String> parentCommandNames = getParentCommandNames();

		Command command = MergedAnnotations.from(method).get(Command.class).synthesize();

		String methodName = method.getName();
		String currentName = StringUtils.hasText(command.name()) ? command.name() : methodName;
		parentCommandNames.add(currentName);

		String name = String.join(" ", parentCommandNames);
		String description = command.description();
		String help = command.help();
		String group = command.group();
		// TODO handle options, aliases, etc
		MethodInvoker methodInvoker = getMethodInvoker();
		log.debug("Creating command for method : %s".formatted(methodName));

		return new MethodInvokerCommandAdapter(name, description, help, group, methodInvoker);
	}

	@Override
	public @Nullable Class<?> getObjectType() {
		return org.springframework.shell.core.command.Command.class;
	}

	private MethodInvoker getMethodInvoker() {
		Class<?> declaringClass = method.getDeclaringClass();
		Object bean = applicationContext.getBean(declaringClass);

		MethodInvoker methodInvoker = new MethodInvoker();
		methodInvoker.setTargetClass(declaringClass);
		methodInvoker.setTargetObject(bean);
		methodInvoker.setTargetMethod(method.getName());
		return methodInvoker;
	}

	private List<String> getParentCommandNames() {
		List<String> parentNames = new ArrayList<>();
		Class<?> current = method.getDeclaringClass();
		while (current != null) {
			Command classCommand = AnnotatedElementUtils.findMergedAnnotation(current, Command.class);
			if (classCommand != null) {
				parentNames.add(classCommand.name());
			}
			current = current.getEnclosingClass();
		}
		Collections.reverse(parentNames);
		return parentNames;
	}

}
