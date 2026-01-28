/*
 * Copyright 2026-present the original author or authors.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link CommandFactoryBean}
 *
 * @author Jay Choi
 */
class CommandFactoryBeanTests {

	private ApplicationContext context;

	@BeforeEach
	void setUp() {
		context = mock(ApplicationContext.class);
		TestCommands testCommands = new TestCommands();
		when(context.getBean(TestCommands.class)).thenReturn(testCommands);
	}

	@Test
	void optionalOptionWithoutDefaultValueShouldFail() throws Exception {
		// given
		Method method = TestCommands.class.getMethod("optionalWithoutDefault", String.class);
		CommandFactoryBean factoryBean = createFactoryBean(method);

		// when & then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, factoryBean::getObject);
		assertTrue(exception.getMessage().contains("must have a default value"));
	}

	@Test
	void optionalOptionWithDefaultValueShouldSucceed() throws Exception {
		// given
		Method method = TestCommands.class.getMethod("optionalWithDefault", String.class);
		CommandFactoryBean factoryBean = createFactoryBean(method);

		// when & then
		assertDoesNotThrow(factoryBean::getObject);
	}

	@Test
	void requiredOptionWithoutDefaultValueShouldSucceed() throws Exception {
		// given
		Method method = TestCommands.class.getMethod("requiredWithoutDefault", String.class);
		CommandFactoryBean factoryBean = createFactoryBean(method);

		// when & then
		assertDoesNotThrow(factoryBean::getObject);
	}

	private CommandFactoryBean createFactoryBean(Method method) {
		CommandFactoryBean factoryBean = new CommandFactoryBean(method);
		factoryBean.setApplicationContext(context);
		return factoryBean;
	}

	// Test command class
	static class TestCommands {

		@Command(name = "test1")
		public void optionalWithoutDefault(@Option(longName = "name") String name) {
		}

		@Command(name = "test2")
		public void optionalWithDefault(@Option(longName = "name", defaultValue = "World") String name) {
		}

		@Command(name = "test3")
		public void requiredWithoutDefault(@Option(longName = "name", required = true) String name) {
		}

	}

}
