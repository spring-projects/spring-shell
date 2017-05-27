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

package org.springframework.shell2.standard;

import static org.springframework.util.StringUtils.collectionToCommaDelimitedString;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell2.MethodTarget;
import org.springframework.shell2.MethodTargetResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * The standard implementation of {@link MethodTargetResolver} for new shell applications,
 * resolves methods annotated with {@link ShellMethod} on {@link ShellComponent} beans.
 *
 * @author Eric Bottard
 * @author Florent Biville
 * @author Camilo Gonzalez
 */
@Component
public class StandardMethodTargetResolver implements MethodTargetResolver {

	@Autowired
	private ApplicationContext applicationContext;
	
	@Override
	public Map<String, MethodTarget> resolve() {
		Map<String, MethodTarget> methodTargets = new HashMap<>();
		Map<String, Object> commandBeans = applicationContext.getBeansWithAnnotation(ShellComponent.class);
		for (Object bean : commandBeans.values()) {
			Class<?> clazz = bean.getClass();
			ReflectionUtils.doWithMethods(clazz, method -> {
				ShellMethod shellMapping = method.getAnnotation(ShellMethod.class);
				String[] keys = shellMapping.value();
				if (keys.length == 0) {
					keys = new String[] {method.getName()};
				}
				for (String key : keys) {
					methodTargets.put(key, new MethodTarget(method, bean, shellMapping.help()));
				}
			}, method -> method.getAnnotation(ShellMethod.class) != null);
		}
		return methodTargets;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " contributing "
			+ collectionToDelimitedString(resolve().keySet(), ", ", "[", "]");
	}
}
