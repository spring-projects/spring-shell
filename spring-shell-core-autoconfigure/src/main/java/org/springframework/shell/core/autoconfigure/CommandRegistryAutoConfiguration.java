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
package org.springframework.shell.core.autoconfigure;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.annotation.support.CommandFactoryBean;
import org.springframework.shell.core.utils.Utils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@AutoConfiguration
public class CommandRegistryAutoConfiguration {

	private static final Log log = LogFactory.getLog(CommandRegistryAutoConfiguration.class);

	@Bean
	@ConditionalOnMissingBean
	public CommandRegistry commandRegistry(ApplicationContext applicationContext) {
		CommandRegistry commandRegistry = new CommandRegistry();
		registerProgrammaticCommands(applicationContext, commandRegistry);
		registerAnnotatedCommands(applicationContext, commandRegistry);
		commandRegistry.registerCommand(Utils.QUIT_COMMAND);
		return commandRegistry;
	}

	private void registerProgrammaticCommands(ApplicationContext applicationContext, CommandRegistry commandRegistry) {
		Map<String, Command> commandBeans = applicationContext.getBeansOfType(Command.class);
		commandBeans.values().forEach(commandRegistry::registerCommand);
	}

	private void registerAnnotatedCommands(ApplicationContext applicationContext, CommandRegistry commandRegistry) {
		Map<String, Object> components = applicationContext.getBeansWithAnnotation(Component.class);
		for (Object candidateComponent : components.values()) {
			Class<?> type = candidateComponent.getClass();
			String className = type.getName();
			if (className.startsWith("org.springframework.boot")) {
				continue;
			}
			log.debug("Registering commands from component: " + className);
			ReflectionUtils.MethodFilter filter = method -> AnnotatedElementUtils.hasAnnotation(method,
					org.springframework.shell.core.command.annotation.Command.class)
					&& isProfileActive(method, applicationContext.getEnvironment());
			Set<Method> methods = MethodIntrospector.selectMethods(type, filter);
			for (Method method : methods) {
				CommandFactoryBean factoryBean = new CommandFactoryBean(method);
				factoryBean.setApplicationContext(applicationContext);
				commandRegistry.registerCommand(factoryBean.getObject());
			}
		}
	}

	private boolean isProfileActive(Method method, Environment environment) {
		Profile profile = AnnotatedElementUtils.findMergedAnnotation(method, Profile.class);
		return profile == null || environment.acceptsProfiles(Profiles.of(profile.value()));
	}

}
