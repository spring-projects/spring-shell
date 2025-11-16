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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.shell.core.ShellConfigurationException;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.EnableCommand;
import org.springframework.shell.core.commands.Clear;
import org.springframework.shell.core.commands.Help;
import org.springframework.shell.core.commands.Version;
import org.springframework.shell.core.jline.InteractiveShellRunner;
import org.springframework.shell.core.jline.JLineInputProvider;
import org.springframework.util.ReflectionUtils;

/**
 * {@link ImportBeanDefinitionRegistrar} for {@link EnableCommand @EnableCommands}.
 *
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 * @author Piotr Olaszewski
 */
public final class EnableCommandRegistrar implements ImportBeanDefinitionRegistrar {

	private static final String COMMAND_REGISTRY_BEAN_NAME = "commandRegistry";

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		EnableCommand enableCommand = metadata.getAnnotations().get(EnableCommand.class).synthesize();

		registerBuiltInCommands(registry);
		registerUserCommands(registry, enableCommand);
		registerCommandRegistryIfNeeded(registry);
		registerShellRunner(registry);
	}

	private static void registerBuiltInCommands(BeanDefinitionRegistry registry) {
		registry.registerBeanDefinition("help", new RootBeanDefinition(Help.class));
		registry.registerBeanDefinition("clear", new RootBeanDefinition(Clear.class));
		registry.registerBeanDefinition("version", new RootBeanDefinition(Version.class));
	}

	private void registerUserCommands(BeanDefinitionRegistry registry, EnableCommand enableCommand) {
		Class<?>[] candidateClasses = enableCommand.value();
		for (Class<?> candidateClass : candidateClasses) {
			registerCommands(candidateClass, registry);
			registerAnnotatedMethods(candidateClass, registry);
		}
	}

	private static void registerCommandRegistryIfNeeded(BeanDefinitionRegistry registry) {
		if (!registry.containsBeanDefinition(COMMAND_REGISTRY_BEAN_NAME)) {
			RootBeanDefinition beanDefinition = new RootBeanDefinition(CommandRegistry.class);
			registry.registerBeanDefinition(COMMAND_REGISTRY_BEAN_NAME, beanDefinition);
		}
	}

	private static void registerShellRunner(BeanDefinitionRegistry registry) {
		// register a shell runner (default to interactive if none is defined)
		if (!registry.containsBeanDefinition("shellRunner")) {
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(InteractiveShellRunner.class);
			try {
				// configure input provider
				if (registry.containsBeanDefinition("inputProvider")) {
					beanDefinitionBuilder.addConstructorArgReference("inputProvider");
				}
				else {
					LineReader reader = LineReaderBuilder.builder().build();
					JLineInputProvider inputProvider = new JLineInputProvider(reader);
					beanDefinitionBuilder.addConstructorArgValue(inputProvider);
				}

				// configure terminal
				if (registry.containsBeanDefinition("terminal")) {
					beanDefinitionBuilder.addConstructorArgReference("terminal");
				}
				else {
					Terminal terminal = TerminalBuilder.builder().system(true).build();
					beanDefinitionBuilder.addConstructorArgValue(terminal);
				}

				// autowire command registry
				beanDefinitionBuilder.addConstructorArgReference(COMMAND_REGISTRY_BEAN_NAME);

				registry.registerBeanDefinition("shellRunner", beanDefinitionBuilder.getBeanDefinition());
			}
			catch (IOException e) {
				throw new ShellConfigurationException("Unable to configure shell runner", e);
			}

		}
	}

	private void registerCommands(Class<?> candidateClass, BeanDefinitionRegistry registry) {
		Arrays.stream(candidateClass.getDeclaredMethods())
			.filter(method -> method.getReturnType().equals(org.springframework.shell.core.command.Command.class))
			.forEach(method -> {
				RootBeanDefinition beanDefinition = new RootBeanDefinition(
						org.springframework.shell.core.command.Command.class);
				registry.registerBeanDefinition(method.getName(), beanDefinition);
			});
	}

	private void registerAnnotatedMethods(Class<?> candidateClass, BeanDefinitionRegistry registry) {
		ReflectionUtils.MethodFilter filter = method -> AnnotatedElementUtils.hasAnnotation(method, Command.class);
		Set<Method> methods = MethodIntrospector.selectMethods(candidateClass, filter);
		for (Method method : methods) {
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(CommandFactoryBean.class);
			beanDefinitionBuilder.addConstructorArgValue(method);
			registry.registerBeanDefinition(method.getName(), beanDefinitionBuilder.getBeanDefinition());
		}
	}

}
