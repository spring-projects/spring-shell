/*
 * Copyright 2023-2024 the original author or authors.
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
package org.springframework.shell.command.annotation.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.shell.Availability;
import org.springframework.shell.AvailabilityProvider;
import org.springframework.shell.Utils;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandRegistration.Builder;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.CommandRegistration.OptionSpec;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.ExceptionResolverMethodResolver;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.command.annotation.OptionValues;
import org.springframework.shell.command.invocation.InvocableShellMethod;
import org.springframework.shell.completion.CompletionProvider;
import org.springframework.shell.context.InteractionMode;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Factory bean used in {@link CommandRegistrationBeanRegistrar} to build
 * instance of {@link CommandRegistration}. Main logic of constructing
 * {@link CommandRegistration} out from annotated command target is
 * in this factory.
 *
 * This factory needs a name of a {@code commandBeanName} which is a name of bean
 * hosting command methods, {@code commandBeanType} which is type of a bean,
 * {@code commandMethodName} which is a name of {@link Method} in a bean,
 * {@code commandMethodParameters} for method parameter types and optionally
 * {@code BuilderSupplier} if context provides pre-configured builder.
 *
 * This is internal class and not meant for generic use.
 *
 * @author Janne Valkealahti
 */
class CommandRegistrationFactoryBean implements FactoryBean<CommandRegistration>, ApplicationContextAware, InitializingBean {

	private final Logger log = LoggerFactory.getLogger(CommandRegistrationFactoryBean.class);
	public static final String COMMAND_BEAN_TYPE = "commandBeanType";
	public static final String COMMAND_BEAN_NAME = "commandBeanName";
	public static final String COMMAND_METHOD_NAME = "commandMethodName";
	public static final String COMMAND_METHOD_PARAMETERS = "commandMethodParameters";

	private ObjectProvider<CommandRegistration.BuilderSupplier> supplier;
	private ApplicationContext applicationContext;
	private Object commandBean;
	private Class<?> commandBeanType;
	private String commandBeanName;
	private String commandMethodName;
	private Class<?>[] commandMethodParameters;

	@Override
	public CommandRegistration getObject() throws Exception {
		CommandRegistration registration = buildRegistration();
		return registration;
	}

	@Override
	public Class<?> getObjectType() {
		return CommandRegistration.class;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.commandBean = applicationContext.getBean(commandBeanName);
		this.supplier = applicationContext.getBeanProvider(CommandRegistration.BuilderSupplier.class);
	}

	public void setCommandBeanType(Class<?> commandBeanType) {
		this.commandBeanType = commandBeanType;
	}

	public void setCommandBeanName(String commandBeanName) {
		this.commandBeanName = commandBeanName;
	}

	public void setCommandMethodName(String commandMethodName) {
		this.commandMethodName = commandMethodName;
	}

	public void setCommandMethodParameters(Class<?>[] commandMethodParameters) {
		this.commandMethodParameters = commandMethodParameters;
	}

	private CommandRegistration.Builder getBuilder() {
		return supplier.getIfAvailable(() -> () -> CommandRegistration.builder()).get();
	}

	private CommandRegistration buildRegistration() {
		Method method = ReflectionUtils.findMethod(commandBeanType, commandMethodName, commandMethodParameters);
		MergedAnnotation<Command> classAnn = MergedAnnotations.from(commandBeanType, SearchStrategy.TYPE_HIERARCHY)
				.get(Command.class);
		MergedAnnotation<Command> methodAnn = MergedAnnotations.from(method, SearchStrategy.TYPE_HIERARCHY)
				.get(Command.class);

		Builder builder = getBuilder();

		// command
		String[] deduceCommand = CommandAnnotationUtils.deduceCommand(classAnn, methodAnn);
		if (deduceCommand.length == 0) {
			deduceCommand = new String[] { Utils.unCamelify(method.getName()) };
		}
		builder.command(deduceCommand);

		// group
		String deduceGroup = CommandAnnotationUtils.deduceGroup(classAnn, methodAnn);
		builder.group(deduceGroup);

		// hidden
		boolean deduceHidden = CommandAnnotationUtils.deduceHidden(classAnn, methodAnn);
		builder.hidden(deduceHidden);

		// description
		String deduceDescription = CommandAnnotationUtils.deduceDescription(classAnn, methodAnn);
		builder.description(deduceDescription);

		// interaction mode
		InteractionMode deduceInteractionMode = CommandAnnotationUtils.deduceInteractionMode(classAnn, methodAnn);
		builder.interactionMode(deduceInteractionMode);

		// availability
		MergedAnnotation<CommandAvailability> caAnn = MergedAnnotations.from(method, SearchStrategy.TYPE_HIERARCHY)
				.get(CommandAvailability.class);
		if (caAnn.isPresent()) {
			String[] refs = caAnn.getStringArray("provider");
			List<AvailabilityProvider> avails = Stream.of(refs)
				.map(r -> {
					return this.applicationContext.getBean(r, AvailabilityProvider.class);
				})
				.collect(Collectors.toList());
			if (!avails.isEmpty()) {
				builder.availability(() -> {
					for (AvailabilityProvider avail : avails) {
						Availability a = avail.get();
						if (!a.isAvailable()) {
							return a;
						}
					}
					return Availability.available();
				});
			}
		}

		// alias
		String[][] deduceAlias = CommandAnnotationUtils.deduceAlias(classAnn, methodAnn);
		for (String[] a : deduceAlias) {
			builder.withAlias().command(a);
		}

		// target
		builder.withTarget().method(commandBean, method);

		// options
		InvocableHandlerMethod ihm = new InvocableHandlerMethod(commandBean, method);
		for (MethodParameter mp : ihm.getMethodParameters()) {
			onCommandParameter(mp, builder);
		}

		// error handling
		ExceptionResolverMethodResolver exceptionResolverMethodResolver = new ExceptionResolverMethodResolver(commandBean.getClass());
		MethodCommandExceptionResolver methodCommandExceptionResolver = new MethodCommandExceptionResolver();
		methodCommandExceptionResolver.bean = commandBean;
		methodCommandExceptionResolver.exceptionResolverMethodResolver = exceptionResolverMethodResolver;
		builder.withErrorHandling().resolver(methodCommandExceptionResolver);

		CommandRegistration registration = builder.build();
		return registration;
	}

	private void onCommandParameter(MethodParameter mp, Builder builder) {

		MergedAnnotation<Option> optionAnn = MergedAnnotations.from(mp.getParameter(), SearchStrategy.TYPE_HIERARCHY)
				.get(Option.class);

		Option so = mp.getParameterAnnotation(Option.class);
		log.debug("Registering with mp='{}' so='{}'", mp, so);
		if (so != null) {
			List<String> longNames = new ArrayList<>();
			List<Character> shortNames = new ArrayList<>();
			for(int i = 0; i < so.shortNames().length; i++) {
				shortNames.add(so.shortNames()[i]);
			}
			if (!ObjectUtils.isEmpty(so.longNames())) {
				for(int i = 0; i < so.longNames().length; i++) {
					longNames.add(so.longNames()[i]);
				}
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
				OptionSpec optionSpec = builder.withOption();
				optionSpec.type(parameterType);
				optionSpec.longNames(longNames.toArray(new String[0]));
				optionSpec.shortNames(shortNames.toArray(new Character[0]));
				optionSpec.position(mp.getParameterIndex());
				optionSpec.description(so.description());
				if (StringUtils.hasText(so.label())) {
					optionSpec.label(so.label());
				}
				int arityMin = so.arityMin();
				int arityMax = so.arityMax();
				if (arityMin > -1) {
					if (arityMax < arityMin) {
						arityMax = arityMin;
					}
				}
				else if (arityMax > -1) {
					if (arityMin < 0) {
						arityMin = 0;
					}
				}
				if (arityMin > -1 && arityMax > -1) {
					optionSpec.arity(arityMin, arityMax);
				}
				else if (so.arity() != OptionArity.NONE) {
					optionSpec.arity(so.arity());
				}
				else {
					if (ClassUtils.isAssignable(boolean.class, parameterType)) {
						optionSpec.arity(OptionArity.ZERO_OR_ONE);
					}
					else if (ClassUtils.isAssignable(Boolean.class, parameterType)) {
						optionSpec.arity(OptionArity.ZERO_OR_ONE);
					}
				}

				if (StringUtils.hasText(so.defaultValue())) {
					optionSpec.defaultValue(so.defaultValue());
				}
				else if (ClassUtils.isAssignable(boolean.class, parameterType)){
					optionSpec.required(false);
					optionSpec.defaultValue("false");
				}
				else {
					if (optionAnn.isPresent()) {
						boolean requiredDeduce = optionAnn.getBoolean("required");
						optionSpec.required(requiredDeduce);
					}
				}

				OptionValues ovAnn = mp.getParameterAnnotation(OptionValues.class);
				if (ovAnn != null) {
					String[] providerBeanNames = ovAnn.provider();
					if (providerBeanNames.length > 0) {
						final List<CompletionProvider> resolvers = Arrays.stream(providerBeanNames)
							.map(beanName -> this.applicationContext.getBean(beanName, CompletionProvider.class))
							.collect(Collectors.toList());
						optionSpec.completion(ctx -> {
							return resolvers.stream()
								.flatMap(resolver -> resolver.apply(ctx).stream())
								.collect(Collectors.toList());
						});
					}
				}
				// if (ovAnn != null && StringUtils.hasText(ovAnn.ref())) {
				// 	CompletionProvider cr = this.applicationContext.getBean(ovAnn.ref(), CompletionProvider.class);
				// 	if (cr != null) {
				// 		optionSpec.completion(ctx -> cr.apply(ctx));
				// 	}
				// }

			}
		}
		else {
			mp.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
			String longName = mp.getParameterName();
			Class<?> parameterType = mp.getParameterType();
			// skip known types which are coming in via parameter resolvers
			if (ClassUtils.isAssignable(parameterType, CommandContext.class)) {
				return;
			}
			if (longName != null) {
				log.debug("Using mp='{}' longName='{}' parameterType='{}'", mp, longName, parameterType);
				OptionSpec optionSpec = builder.withOption();
				optionSpec.longNames(longName);
				optionSpec.type(parameterType);
				optionSpec.required();
				optionSpec.position(mp.getParameterIndex());
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

	private static class MethodCommandExceptionResolver implements CommandExceptionResolver {

		Object bean;
		ExceptionResolverMethodResolver exceptionResolverMethodResolver;

		@Override
		public CommandHandlingResult resolve(Exception ex) {
			Method exceptionHandlerMethod = exceptionResolverMethodResolver.resolveMethodByThrowable(ex);
			if (exceptionHandlerMethod == null) {
				return null;
			}
			InvocableShellMethod invocable = new InvocableShellMethod(bean, exceptionHandlerMethod);
			try {
				Object invoke = invocable.invoke(null, ex);
				if (invoke instanceof CommandHandlingResult) {
					return (CommandHandlingResult)invoke;
				}
			} catch (Exception e) {
			}
			return null;
		}

	}
}
