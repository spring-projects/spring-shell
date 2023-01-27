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

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

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
