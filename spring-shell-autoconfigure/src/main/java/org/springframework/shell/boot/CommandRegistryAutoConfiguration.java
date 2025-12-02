/*
 * Copyright 2021-present the original author or authors.
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
package org.springframework.shell.boot;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.shell.core.ShellConfigurationException;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.annotation.support.CommandFactoryBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

@AutoConfiguration
public class CommandRegistryAutoConfiguration {

	private static final Log log = LogFactory.getLog(SpringShellAutoConfiguration.class);

	@Bean
	@ConditionalOnMissingBean
	public CommandRegistry commandRegistry(ApplicationContext applicationContext) {
		CommandRegistry commandRegistry = new CommandRegistry();
		registerProgrammaticCommands(applicationContext, commandRegistry);
		registerAnnotatedCommands(applicationContext, commandRegistry);
		return commandRegistry;
	}

	private void registerProgrammaticCommands(ApplicationContext applicationContext, CommandRegistry commandRegistry) {
		Map<String, Command> commandBeans = applicationContext.getBeansOfType(Command.class);
		commandBeans.values().forEach(commandRegistry::registerCommand);
	}

	private void registerAnnotatedCommands(ApplicationContext applicationContext, CommandRegistry commandRegistry) {
		Map<String, Object> springBootApps = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
		Class<?> mainClass = AopUtils.getTargetClass(springBootApps.values().iterator().next());
		String mainPackage = ClassUtils.getPackageName(mainClass);
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
		Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(mainPackage);
		for (BeanDefinition candidateComponent : candidateComponents) {
			String className = candidateComponent.getBeanClassName();
			if (className == null) {
				log.warn(String.format("Skipping candidate component %s with null class name", candidateComponent));
				continue;
			}
			try {
				Class<?> cls = ClassUtils.forName(className, applicationContext.getClassLoader());
				ReflectionUtils.MethodFilter filter = method -> AnnotatedElementUtils.hasAnnotation(method,
						org.springframework.shell.core.command.annotation.Command.class);
				Set<Method> methods = MethodIntrospector.selectMethods(cls, filter);
				for (Method method : methods) {
					CommandFactoryBean factoryBean = new CommandFactoryBean(method);
					factoryBean.setApplicationContext(applicationContext);
					commandRegistry.registerCommand(factoryBean.getObject());
				}
			}
			catch (ClassNotFoundException e) {
				throw new ShellConfigurationException("Unable to configure commands from class " + className, e);
			}
		}
	}

}
