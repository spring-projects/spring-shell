/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell.legacy;

import static org.springframework.util.StringUtils.collectionToDelimitedString;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.ConfigurableCommandRegistry;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.MethodTargetRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * A {@link MethodTargetRegistrar} that discovers methods annotated with {@link CliCommand} on beans
 * implementing the {@link CommandMarker} marker interface.
 * 
 * @author Eric Bottard
 * @author Florent Biville
 * @author Camilo Gonzalez
 */
@Component
public class LegacyMethodTargetRegistrar implements MethodTargetRegistrar {

	@Autowired
	private ApplicationContext applicationContext;

	private Map<String, MethodTarget> commands = new TreeMap<>();
	
	@Override
	public void register(ConfigurableCommandRegistry registry) {
		Map<String, CommandMarker> beans = applicationContext.getBeansOfType(CommandMarker.class);
		for (Object bean : beans.values()) {
			Class<?> clazz = bean.getClass();
			ReflectionUtils.doWithMethods(clazz, method -> {
				CliCommand cliCommand = method.getAnnotation(CliCommand.class);
				for (String key : cliCommand.value()) {
					MethodTarget target = new MethodTarget(method, bean, cliCommand.help());
					registry.register(key, target);
					commands.put(key, target);
				}
			}, method -> method.getAnnotation(CliCommand.class) != null);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " contributing "
			+ collectionToDelimitedString(commands.keySet(), ", ", "[", "]");
	}

}
