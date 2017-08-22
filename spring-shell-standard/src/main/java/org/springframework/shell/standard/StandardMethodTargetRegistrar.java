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

package org.springframework.shell.standard;

import static org.springframework.util.StringUtils.collectionToDelimitedString;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.*;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * The standard implementation of {@link MethodTargetRegistrar} for new shell
 * applications, resolves methods annotated with {@link ShellMethod} on
 * {@link ShellComponent} beans.
 *
 * @author Eric Bottard
 * @author Florent Biville
 * @author Camilo Gonzalez
 */
public class StandardMethodTargetRegistrar implements MethodTargetRegistrar {

	private ApplicationContext applicationContext;

	private Map<String, MethodTarget> commands = new HashMap<>();

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void register(ConfigurableCommandRegistry registry) {
		Map<String, Object> commandBeans = applicationContext.getBeansWithAnnotation(ShellComponent.class);
		for (Object bean : commandBeans.values()) {
			Class<?> clazz = bean.getClass();
			ReflectionUtils.doWithMethods(clazz, method -> {
				ShellMethod shellMapping = method.getAnnotation(ShellMethod.class);
				String[] keys = shellMapping.key();
				if (keys.length == 0) {
					keys = new String[] { Utils.unCamelify(method.getName()) };
				}
				for (String key : keys) {
					Supplier<Availability> availabilityIndicator = findAvailabilityIndicator(keys, bean, method);
					MethodTarget target = new MethodTarget(method, bean, shellMapping.value(), availabilityIndicator);
					registry.register(key, target);
					commands.put(key, target);
				}
			}, method -> method.getAnnotation(ShellMethod.class) != null);
		}
	}

	/**
	 * Tries to locate an availability indicator (a no-arg method that returns
	 * {@link Availability}) for the given command method. The following are tried in order
	 * for method {@literal m}:
	 * <ol>
	 * <li>If {@literal m} bears the {@literal @}{@link ShellMethodAvailability} annotation,
	 * its value should be the method name to look up</li>
	 * <li>a method named {@literal "<m>Availability"} is looked up.</li>
	 * <li>otherwise, if some method {@literal ai} that returns {@link Availability} and takes
	 * no argument exists, that is annotated with {@literal @}{@link ShellMethodAvailability}
	 * and whose annotation value contains one of the {@literal commandKeys}, then it is
	 * selected</li>
	 * </ol>
	 */
	private Supplier<Availability> findAvailabilityIndicator(String[] commandKeys, Object bean, Method method) {
		ShellMethodAvailability explicit = method.getAnnotation(ShellMethodAvailability.class);
		final Method indicator;
		if (explicit != null) {
			Assert.isTrue(explicit.value().length == 1, "When set on a @" +
					ShellMethod.class.getSimpleName() + " method, the value of the @"
					+ ShellMethodAvailability.class.getSimpleName() +
					" should be a single element, the name of a method that returns "
					+ Availability.class.getSimpleName() +
					". Found " + Arrays.asList(explicit.value()) + " for " + method);
			indicator = ReflectionUtils.findMethod(bean.getClass(), explicit.value()[0]);
		} // Try "<method>Availability"
		else {
			Method implicit = ReflectionUtils.findMethod(bean.getClass(), method.getName() + "Availability");
			if (implicit != null) {
				indicator = implicit;
			} else {
				Map<Method, Collection<String>> candidates = new HashMap<>();
				ReflectionUtils.doWithMethods(bean.getClass(), candidate -> {
					List<String> matchKeys = new ArrayList<>(Arrays.asList(candidate.getAnnotation(ShellMethodAvailability.class).value()));
					if (matchKeys.contains("*")) {
						Assert.isTrue(matchKeys.size() == 1, "When using '*' as a wildcard for " +
								ShellMethodAvailability.class.getSimpleName() + ", this can be the only value. Found " +
								matchKeys + " on method " + candidate);
						candidates.put(candidate, matchKeys);
					} else {
						matchKeys.retainAll(Arrays.asList(commandKeys));
						if (!matchKeys.isEmpty()) {
							candidates.put(candidate, matchKeys);
						}
					}
				}, m -> m.getAnnotation(ShellMethodAvailability.class) != null && m.getAnnotation(ShellMethod.class) == null);

				// Make sure wildcard approach has less precedence than explicit name
				Set<Method> notUsingWildcard = candidates.entrySet().stream()
						.filter(e -> !e.getValue().contains("*"))
						.map(Map.Entry::getKey)
						.collect(Collectors.toSet());

				Assert.isTrue(notUsingWildcard.size() <= 1,
						"Found several @" + ShellMethodAvailability.class.getSimpleName() +
								" annotated methods that could apply for " + method + ". Offending candidates are "
								+ notUsingWildcard);

				if (notUsingWildcard.size() == 1) {
					indicator = notUsingWildcard.iterator().next();
				} // Wildcard was available
				else if (candidates.size() == 1) {
					indicator = candidates.keySet().iterator().next();
				} else {
					indicator = null;
				}
			}
		}

		if (indicator != null) {
			Assert.isTrue(indicator.getReturnType().equals(Availability.class),
					"Method " + indicator + " should return " + Availability.class.getSimpleName());
			Assert.isTrue(indicator.getParameterCount() == 0, "Method " + indicator + " should be a no-arg method");
			ReflectionUtils.makeAccessible(indicator);
			return () -> (Availability) ReflectionUtils.invokeMethod(indicator, bean);
		}
		else {
			return null;
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " contributing "
				+ collectionToDelimitedString(commands.keySet(), ", ", "[", "]");
	}
}
