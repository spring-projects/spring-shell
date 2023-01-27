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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.shell.command.annotation.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class CommandRegistrationBeanRegistrarTests {

	private final BeanDefinitionRegistry registry = new DefaultListableBeanFactory();
	private final CommandRegistrationBeanRegistrar registrar = new CommandRegistrationBeanRegistrar(registry);

	@Test
	void registerWhenNotAlreadyRegisteredAddBeanDefinition() {
		String beanName = BeanCommand.class.getName();
		this.registrar.register(BeanCommand.class);
		BeanDefinition definition = this.registry.getBeanDefinition(beanName);
		assertThat(definition).isNotNull();
		assertThat(definition.getBeanClassName()).isEqualTo(BeanCommand.class.getName());
	}

	@Test
	void registerWhenNoAnnotationThrowsException() {
		assertThatIllegalStateException()
				.isThrownBy(() -> this.registrar.register(NoAnnotationCommand.class))
				.withMessageContaining("No Command annotation found");
	}

	@Test
	void registerWhenNotAlreadyRegisteredAddMethodBeanDefinition() {
		String beanName = BeanWithMethodCommand.class.getName();
		this.registrar.register(BeanWithMethodCommand.class);
		BeanDefinition definition = this.registry.getBeanDefinition(beanName);
		assertThat(definition).isNotNull();
		definition = this.registry.getBeanDefinition(beanName + "/method");
		assertThat(definition).isNotNull();
		assertThat(definition.getBeanClassName()).isEqualTo(CommandRegistrationFactoryBean.class.getName());
	}

	@Command
	static class BeanCommand {
	}

	@Command
	static class BeanWithMethodCommand {

		@Command
		void method() {
		}
	}

	static class NoAnnotationCommand {
	}
}
