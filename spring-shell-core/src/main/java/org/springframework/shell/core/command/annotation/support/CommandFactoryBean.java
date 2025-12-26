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
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandCreationException;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.adapter.MethodInvokerCommandAdapter;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.core.command.availability.AvailabilityProvider;
import org.springframework.shell.core.command.exit.ExitStatusExceptionMapper;
import org.springframework.shell.core.command.completion.CompletionProvider;
import org.springframework.shell.core.utils.Utils;
import org.springframework.util.Assert;

/**
 * Factory bean to build instances of {@link Command}.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public class CommandFactoryBean implements ApplicationContextAware, FactoryBean<Command> {

	private final Log log = LogFactory.getLog(CommandFactoryBean.class);

	private final Method method;

	@SuppressWarnings("NullAway.Init")
	private ApplicationContext applicationContext;

	public CommandFactoryBean(Method method) {
		Assert.notNull(method, "'method' must not be null");
		this.method = method;
	}

	@Override
	public Command getObject() {
		org.springframework.shell.core.command.annotation.Command command = MergedAnnotations.from(this.method)
			.get(org.springframework.shell.core.command.annotation.Command.class)
			.synthesize();
		String name = String.join(" ", command.name());
		name = name.isEmpty() ? this.method.getName() : name;
		String description = command.description();
		description = description.isEmpty() ? "N/A" : description;
		String help = command.help();
		String group = command.group();
		group = group.isEmpty() ? this.method.getDeclaringClass().getSimpleName() + " Commands" : group;
		boolean hidden = command.hidden();
		String[] aliases = command.alias();
		String availabilityProvider = command.availabilityProvider();
		String exitStatusExceptionMapper = command.exitStatusExceptionMapper();
		String completionProviderBeanName = command.completionProvider();
		log.debug("Creating command bean for method '" + this.method + "' with name '" + name + "'");
		Class<?> declaringClass = this.method.getDeclaringClass();
		Object targetObject;
		try {
			targetObject = this.applicationContext.getBean(declaringClass);
		}
		catch (NoSuchBeanDefinitionException e) {
			String errorMessage = """
					Unable to create command for method '%s' because no bean of type '%s' is defined in the application context.
					Ensure that the declaring class is annotated with a Spring stereotype annotation (e.g., @Component) or
					is otherwise registered as a bean in the application context.
					"""
				.formatted(this.method.getName(), declaringClass.getName());
			throw new CommandCreationException(errorMessage, e);
		}
		ConfigurableConversionService configurableConversionService = new DefaultConversionService();
		try {
			configurableConversionService = this.applicationContext.getBean(ConfigurableConversionService.class);
		}
		catch (BeansException e) {
			log.debug("No ConfigurableConversionService bean found, using a default conversion service.");
		}
		AvailabilityProvider availabilityProviderBean = AvailabilityProvider.alwaysAvailable();
		if (!availabilityProvider.isEmpty()) {
			try {
				availabilityProviderBean = this.applicationContext.getBean(availabilityProvider,
						AvailabilityProvider.class);
			}
			catch (BeansException e) {
				log.debug("No AvailabilityProvider bean found with name '" + availabilityProvider
						+ "', using always available provider.");
			}
		}
		ExitStatusExceptionMapper exitStatusExceptionMapperBean = null;
		if (!exitStatusExceptionMapper.isEmpty()) {
			try {
				exitStatusExceptionMapperBean = this.applicationContext.getBean(exitStatusExceptionMapper,
						ExitStatusExceptionMapper.class);
			}
			catch (BeansException e) {
				log.debug("No ExitStatusExceptionMapper bean found with name '" + exitStatusExceptionMapper
						+ "', using default exception mapping strategy.");
			}
		}
		Validator validator = Utils.defaultValidator();
		try {
			validator = this.applicationContext.getBean(Validator.class);
		}
		catch (BeansException e) {
			log.debug("No Validator bean found, using default validator.");
		}
		CompletionProvider completionProvider = CompletionContext -> Collections.emptyList();
		if (!completionProviderBeanName.isEmpty()) {
			try {
				completionProvider = this.applicationContext.getBean(completionProviderBeanName,
						CompletionProvider.class);
			}
			catch (BeansException e) {
				log.debug("No CompletionProvider bean found with name '" + completionProviderBeanName
						+ "', using default completion provider.");
			}
		}

		MethodInvokerCommandAdapter methodInvokerCommandAdapter = new MethodInvokerCommandAdapter(name, description,
				group, help, hidden, this.method, targetObject, configurableConversionService, validator);
		methodInvokerCommandAdapter.setAliases(Arrays.stream(aliases).toList());
		methodInvokerCommandAdapter.setAvailabilityProvider(availabilityProviderBean);
		methodInvokerCommandAdapter.setCompletionProvider(completionProvider);
		if (exitStatusExceptionMapperBean != null) {
			methodInvokerCommandAdapter.setExitStatusExceptionMapper(exitStatusExceptionMapperBean);
		}
		// introspect command options
		List<CommandOption> commandOptions = new ArrayList<>();
		for (Parameter parameter : this.method.getParameters()) {
			Option optionAnnotation = parameter.getAnnotation(Option.class);
			if (optionAnnotation != null) {
				char shortName = optionAnnotation.shortName();
				String longName = optionAnnotation.longName();
				String optionDescription = optionAnnotation.description();
				boolean required = optionAnnotation.required();
				String defaultValue = optionAnnotation.defaultValue();
				if (shortName == ' ' && longName.isEmpty()) {
					throw new IllegalArgumentException(
							"Either shortName or longName (or both) must be provided for option on parameter '"
									+ parameter.getName() + "'");
				}
				CommandOption commandOption = new CommandOption(shortName, longName, optionDescription, required,
						defaultValue, null);
				commandOptions.add(commandOption);
			}
		}
		methodInvokerCommandAdapter.setOptions(commandOptions);
		return methodInvokerCommandAdapter;
	}

	@Override
	public Class<?> getObjectType() {
		return Command.class;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
