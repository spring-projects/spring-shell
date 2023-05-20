/*
 * Copyright 2023 the original author or authors.
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

import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.Availability;
import org.springframework.shell.AvailabilityProvider;
import org.springframework.shell.command.CommandOption;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandRegistration.OptionArity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.command.annotation.OptionValues;
import org.springframework.shell.completion.CompletionProvider;

import static org.assertj.core.api.Assertions.assertThat;

class CommandRegistrationFactoryBeanTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();
	private final static String BEAN = "commandBean";
	private final static String FACTORYBEAN = "commandRegistrationFactoryBean";
	private final static String FACTORYBEANREF = "&" + FACTORYBEAN;

	@Test
	void hiddenOnClassLevel() {
		configCommon(HiddenOnClassBean.class, new HiddenOnClassBean())
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.isHidden()).isTrue();
				});
	}

	@Command(hidden = true)
	private static class HiddenOnClassBean {

		@Command
		void command(){
		}
	}

	@Test
	void commandCommonThings() {
		configCommon(OnBothClassAndMethod.class, new OnBothClassAndMethod())
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getCommand()).isEqualTo("one two");
					assertThat(registration.getAliases()).hasSize(1);
					assertThat(registration.getAliases().get(0).getCommand()).isEqualTo("three four");
					assertThat(registration.getGroup()).isEqualTo("group2");
				});
	}

	@Command(command = "one", alias = "three", group = "group1")
	private static class OnBothClassAndMethod {

		@Command(command = "two", alias = "four", group = "group2")
		void command(){
		}
	}

	@Test
	void setsRequiredOption() {
		configCommon(RequiredOption.class, new RequiredOption(), "command1", new Class[] { String.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions()).hasSize(1);
					assertThat(registration.getOptions().get(0).isRequired()).isTrue();
		});
		configCommon(RequiredOption.class, new RequiredOption(), "command2", new Class[] { String.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions()).hasSize(1);
					assertThat(registration.getOptions().get(0).isRequired()).isFalse();
		});
	}

	@Command
	private static class RequiredOption {

		@Command
		void command1(@Option(required = true) String arg) {
		}

		@Command
		void command2(@Option(required = false) String arg) {
		}
	}

	@Test
	void setsAvailabilitySupplier() {
		configCommon(AvailabilityIndicator.class, new AvailabilityIndicator(), "command1", new Class[] { })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getAvailability()).isNotNull();
					assertThat(registration.getAvailability().getReason()).isEqualTo("fakereason");
		});
	}

	@Command
	private static class AvailabilityIndicator {

		@Command
		@CommandAvailability(provider = "testAvailability")
		void command1() {
		}

		@Bean
		public AvailabilityProvider testAvailability() {
			return () -> Availability.unavailable("fakereason");
		}
	}


	@Test
	void setsOptionValuesWithBoolean() {
		configCommon(OptionValuesWithBoolean.class, new OptionValuesWithBoolean(), "command1", new Class[] { boolean.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions().get(0).getArityMin()).isEqualTo(0);
					assertThat(registration.getOptions().get(0).getArityMax()).isEqualTo(1);
		});
		configCommon(OptionValuesWithBoolean.class, new OptionValuesWithBoolean(), "command2", new Class[] { Boolean.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions().get(0).getArityMin()).isEqualTo(0);
					assertThat(registration.getOptions().get(0).getArityMax()).isEqualTo(1);
		});
		configCommon(OptionValuesWithBoolean.class, new OptionValuesWithBoolean(), "command3", new Class[] { Boolean.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions().get(0).getArityMin()).isEqualTo(0);
					assertThat(registration.getOptions().get(0).getArityMax()).isEqualTo(1);
		});
		configCommon(OptionValuesWithBoolean.class, new OptionValuesWithBoolean(), "command4", new Class[] { Boolean.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions().get(0).getArityMin()).isEqualTo(0);
					assertThat(registration.getOptions().get(0).getArityMax()).isEqualTo(1);
		});
		configCommon(OptionValuesWithBoolean.class, new OptionValuesWithBoolean(), "command5", new Class[] { boolean.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions().get(0).getArityMin()).isEqualTo(0);
					assertThat(registration.getOptions().get(0).getArityMax()).isEqualTo(1);
					assertThat(registration.getOptions().get(0).isRequired()).isFalse();
					assertThat(registration.getOptions().get(0).getDefaultValue()).isEqualTo("false");
		});
	}

	@Command
	private static class OptionValuesWithBoolean {

		@Command
		void command1(@Option(defaultValue = "false") boolean arg) {
		}

		@Command
		void command2(@Option(defaultValue = "false") Boolean arg) {
		}

		@Command
		void command3(@Option Boolean arg) {
		}

		@Command
		void command4(Boolean arg) {
		}

		@Command
		void command5(boolean arg) {
		}
	}

	@Test
	void setsOptionWithCompletion() {
		configCommon(OptionWithCompletion.class, new OptionWithCompletion(), "command1", new Class[] { String.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions().get(0).getCompletion()).isNotNull();
		});
	}

	@Command
	private static class OptionWithCompletion {

		@Command
		void command1(@Option(longNames = "arg") @OptionValues(provider = "completionProvider") String arg) {
		}

		@Bean
		CompletionProvider completionProvider() {
			return ctx -> {
				return Collections.emptyList();
			};
		}

	}

	@Test
	void setsOptionWithArity() {
		configCommon(OptionWithArity.class, new OptionWithArity(), "command1", new Class[] { String.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions().get(0).getArityMin()).isEqualTo(1);
					assertThat(registration.getOptions().get(0).getArityMax()).isEqualTo(1);
		});
		configCommon(OptionWithArity.class, new OptionWithArity(), "command2", new Class[] { String.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions().get(0).getArityMin()).isEqualTo(1);
					assertThat(registration.getOptions().get(0).getArityMax()).isEqualTo(1);
		});
		configCommon(OptionWithArity.class, new OptionWithArity(), "command3", new Class[] { String.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions().get(0).getArityMin()).isEqualTo(0);
					assertThat(registration.getOptions().get(0).getArityMax()).isEqualTo(2);
		});
		configCommon(OptionWithArity.class, new OptionWithArity(), "command4", new Class[] { String.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions().get(0).getArityMin()).isEqualTo(0);
					assertThat(registration.getOptions().get(0).getArityMax()).isEqualTo(2);
		});
	}

	@Command
	private static class OptionWithArity {

		@Command
		void command1(@Option(longNames = "arg", arity = OptionArity.EXACTLY_ONE) String arg) {
		}

		@Command
		void command2(@Option(longNames = "arg", arityMin = 1) String arg) {
		}

		@Command
		void command3(@Option(longNames = "arg", arityMax = 2) String arg) {
		}

		@Command
		void command4(@Option(longNames = "arg", arityMax = 2, arity = OptionArity.EXACTLY_ONE) String arg) {
		}
	}

	@Test
	void setsOptionWithLabel() {
		configCommon(OptionWithLabel.class, new OptionWithLabel(), "command1", new Class[] { String.class })
				.run((context) -> {
					CommandRegistrationFactoryBean fb = context.getBean(FACTORYBEANREF,
							CommandRegistrationFactoryBean.class);
					assertThat(fb).isNotNull();
					CommandRegistration registration = fb.getObject();
					assertThat(registration).isNotNull();
					assertThat(registration.getOptions().get(0).getLabel()).isEqualTo("label");
		});
	}

	@Command
	private static class OptionWithLabel {

		@Command
		void command1(@Option(longNames = "arg", label = "label") String arg) {
		}
	}

	private <T> ApplicationContextRunner configCommon(Class<T> type, T bean) {
		return configCommon(type, bean, "command", new Class[0]);
	}

	private <T> ApplicationContextRunner configCommon(Class<T> type, T bean, String method, Class<?>[] parameters) {
		return this.contextRunner
				.withBean(BEAN, type, () -> bean)
				.withBean(FACTORYBEAN, CommandRegistrationFactoryBean.class, () -> new CommandRegistrationFactoryBean(), bd -> {
					bd.getPropertyValues().add(CommandRegistrationFactoryBean.COMMAND_BEAN_TYPE, type);
					bd.getPropertyValues().add(CommandRegistrationFactoryBean.COMMAND_BEAN_NAME, BEAN);
					bd.getPropertyValues().add(CommandRegistrationFactoryBean.COMMAND_METHOD_NAME, method);
					bd.getPropertyValues().add(CommandRegistrationFactoryBean.COMMAND_METHOD_PARAMETERS, parameters);
				});
	}
}
