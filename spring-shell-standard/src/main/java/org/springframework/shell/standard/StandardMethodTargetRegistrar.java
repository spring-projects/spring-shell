/*
 * Copyright 2015-2023 the original author or authors.
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
package org.springframework.shell.standard;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.shell.Availability;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.MethodTargetRegistrar;
import org.springframework.shell.Utils;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandRegistration.Builder;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.CommandRegistration.OptionSpec;
import org.springframework.shell.command.annotation.MethodCommandExceptionResolver;
import org.springframework.shell.completion.CompletionResolver;
import org.springframework.shell.standard.ShellOption.NoValueProvider;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * The standard implementation of {@link MethodTargetRegistrar} for new shell
 * applications, resolves methods annotated with {@link ShellMethod} on
 * {@link ShellComponent} beans.
 *
 * @author Eric Bottard
 * @author Florent Biville
 * @author Camilo Gonzalez
 * @author Janne Valkealahti
 */
public class StandardMethodTargetRegistrar implements MethodTargetRegistrar {

	private final Logger log = LoggerFactory.getLogger(StandardMethodTargetRegistrar.class);
	private ApplicationContext applicationContext;
	private CommandRegistration.BuilderSupplier commandRegistrationBuilderSupplier;

	public StandardMethodTargetRegistrar(ApplicationContext applicationContext,
	CommandRegistration.BuilderSupplier commandRegistrationBuilderSupplier) {
		this.applicationContext = applicationContext;
		this.commandRegistrationBuilderSupplier = commandRegistrationBuilderSupplier;
	}

	@Override
	public void register(CommandCatalog registry) {
		Map<String, Object> commandBeans = applicationContext.getBeansWithAnnotation(ShellComponent.class);
		log.debug("Found commandBeans to register {}", commandBeans);
		for (Object bean : commandBeans.values()) {
			Class<?> clazz = bean.getClass();
			ReflectionUtils.doWithMethods(clazz, method -> {
				ShellMethod shellMapping = method.getAnnotation(ShellMethod.class);
				String[] keys = shellMapping.key();
				if (keys.length == 0) {
					keys = new String[] { Utils.unCamelify(method.getName()) };
				}
				String group = getOrInferGroup(method);

				String key = keys[0];
				log.debug("Registering with keys='{}' key='{}'", keys, key);
				Supplier<Availability> availabilityIndicator = findAvailabilityIndicator(keys, bean, method);

				Builder builder = commandRegistrationBuilderSupplier.get()
					.command(key)
					.group(group)
					.description(shellMapping.value())
					.interactionMode(shellMapping.interactionMode())
					.availability(availabilityIndicator);

				for (int i = 1; i < keys.length; i++) {
					builder.withAlias().command(keys[i]).group(group);
				}

				InvocableHandlerMethod ihm = new InvocableHandlerMethod(bean, method);
				for (MethodParameter mp : ihm.getMethodParameters()) {

					ShellOption so = mp.getParameterAnnotation(ShellOption.class);
					log.debug("Registering with mp='{}' so='{}'", mp, so);
					if (so != null) {
						List<String> longNames = new ArrayList<>();
						List<Character> shortNames = new ArrayList<>();
						if (!ObjectUtils.isEmpty(so.value())) {
							Arrays.asList(so.value()).stream().forEach(o -> {
								String stripped = StringUtils.trimLeadingCharacter(o, '-');
								log.debug("Registering o='{}' stripped='{}'", o, stripped);
								if (o.length() == stripped.length() + 2) {
									longNames.add(stripped);
								}
								else if (o.length() == stripped.length() + 1 && stripped.length() == 1) {
									shortNames.add(stripped.charAt(0));
								}
								else if (o.length() == stripped.length()) {
									if ("--".equals(shellMapping.prefix())) {
										longNames.add(stripped);
									}
									else if ("-".equals(shellMapping.prefix()) && stripped.length() == 1) {
										shortNames.add(stripped.charAt(0));
									}
								}
							});
						}
						else {
							// ShellOption value not defined
							mp.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
							String longName = mp.getParameterName();
							Class<?> parameterType = mp.getParameterType();
							if (longName != null) {
								log.debug("Using mp='{}' longName='{}' parameterType='{}'", mp, longName, parameterType);
								longNames.add(longName);
							}
						}
						if (!longNames.isEmpty() || !shortNames.isEmpty()) {
							log.debug("Registering longNames='{}' shortNames='{}'", longNames, shortNames);
							Class<?> parameterType = mp.getParameterType();
							Type genericParameterType = mp.getGenericParameterType();
							OptionSpec optionSpec = builder.withOption()
								.type(genericParameterType)
								.longNames(longNames.toArray(new String[0]))
								.shortNames(shortNames.toArray(new Character[0]))
								.position(mp.getParameterIndex())
								.description(so.help());
							if (so.arity() > -1) {
								optionSpec.arity(0, so.arity());
							}
							else {
								if (ClassUtils.isAssignable(boolean.class, parameterType)) {
									optionSpec.arity(OptionArity.ZERO_OR_ONE);
								}
								else if (ClassUtils.isAssignable(Boolean.class, parameterType)) {
									optionSpec.arity(OptionArity.ZERO_OR_ONE);
								}
							}
							if (!ObjectUtils.nullSafeEquals(so.defaultValue(), ShellOption.NONE)
									&& !ObjectUtils.nullSafeEquals(so.defaultValue(), ShellOption.NULL)) {
								optionSpec.defaultValue(so.defaultValue());
							}
							if (ObjectUtils.nullSafeEquals(so.defaultValue(), ShellOption.NONE)) {
								if (ClassUtils.isAssignable(boolean.class, parameterType)) {
									optionSpec.required(false);
									optionSpec.defaultValue("false");
								}
								else {
									optionSpec.required();
								}
							}
							if (!ClassUtils.isAssignable(NoValueProvider.class, so.valueProvider())) {
								CompletionResolver completionResolver = ctx -> {
									ValueProvider valueProviderBean = this.applicationContext.getBean(so.valueProvider());
									List<CompletionProposal> complete = valueProviderBean.complete(ctx);
									return complete;
								};
								optionSpec.completion(completionResolver);
							}
						}
					}
					else {
						mp.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
						String longName = mp.getParameterName();
						Class<?> parameterType = mp.getParameterType();
						if (longName != null) {
							log.debug("Using mp='{}' longName='{}' parameterType='{}'", mp, longName, parameterType);
							OptionSpec optionSpec = builder.withOption()
								.longNames(longName)
								.type(parameterType)
								.required()
								.position(mp.getParameterIndex());
							if (ClassUtils.isAssignable(boolean.class, parameterType)) {
								optionSpec.arity(OptionArity.ZERO_OR_ONE);
								optionSpec.required(false);
								optionSpec.defaultValue("false");
							}
							else if (ClassUtils.isAssignable(Boolean.class, parameterType)) {
								optionSpec.arity(OptionArity.ZERO_OR_ONE);
							}
							else {
								optionSpec.arity(OptionArity.EXACTLY_ONE);
							}
						}
					}
				}

				builder.withTarget().method(bean, method);

				ObjectProvider<Terminal> terminal = this.applicationContext.getBeanProvider(Terminal.class);
				// TODO: feels a bit fishy to return null terminal but for now it's mostly to pass tests as it should not fail
				MethodCommandExceptionResolver resolver = new MethodCommandExceptionResolver(bean, terminal.getIfAvailable(() -> null));
				builder.withErrorHandling().resolver(resolver);

				CommandRegistration registration = builder.build();
				registry.register(registration);
			}, method -> method.getAnnotation(ShellMethod.class) != null);
		}
	}

	/**
	 * Gets the group from the following places, in order:<ul>
	 *     <li>explicit annotation at the method level</li>
	 *     <li>explicit annotation at the class level</li>
	 *     <li>explicit annotation at the package level</li>
	 *     <li>implicit from the class name</li>
	 * </ul>
	 */
	private String getOrInferGroup(Method method) {
		ShellMethod methodAnn = AnnotationUtils.getAnnotation(method, ShellMethod.class);
		if (!methodAnn.group().equals(ShellMethod.INHERITED)) {
			return methodAnn.group();
		}
		Class<?> clazz = method.getDeclaringClass();
		ShellCommandGroup classAnn = AnnotationUtils.getAnnotation(clazz, ShellCommandGroup.class);
		if (classAnn != null && !classAnn.value().equals(ShellCommandGroup.INHERIT_AND_INFER)) {
			return classAnn.value();
		}
		ShellCommandGroup packageAnn = AnnotationUtils.getAnnotation(clazz.getPackage(), ShellCommandGroup.class);
		if (packageAnn != null && !packageAnn.value().equals(ShellCommandGroup.INHERIT_AND_INFER)) {
			return packageAnn.value();
		}
		// Shameful copy/paste from https://stackoverflow.com/questions/7593969/regex-to-split-camelcase-or-titlecase-advanced
		return StringUtils.arrayToDelimitedString(clazz.getSimpleName().split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"), " ");
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
}
