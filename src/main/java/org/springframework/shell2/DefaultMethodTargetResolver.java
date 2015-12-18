/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.shell2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * Created by ericbottard on 09/12/15.
 */
@Component
public class DefaultMethodTargetResolver implements MethodTargetResolver {

	@Override
	public Map<String, MethodTarget> resolve(ApplicationContext applicationContext) {
		Map<String, MethodTarget> methodTargets = new HashMap<>();
		Map<String, Object> commandBeans = applicationContext.getBeansWithAnnotation(ShellComponent.class);
		for (Object bean : commandBeans.values()) {
			Class<?> clazz = bean.getClass();
			ReflectionUtils.doWithMethods(clazz, method -> {
				ShellMethod shellMapping = method.getAnnotation(ShellMethod.class);
				String[] keys = shellMapping.value();
				if (keys.length == 0) {
					keys = new String[]{method.getName()};
				}
				for (String key : keys) {
					methodTargets.put(key, new MethodTarget(method, bean, shellMapping.help()));
				}
			}, method -> method.getAnnotation(ShellMethod.class) != null);
		}
		return methodTargets;
	}
}
