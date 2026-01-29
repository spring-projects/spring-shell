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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author David Pilar
 */
class CommandFactoryBeanTests {

	@Test
	void testOptionNames() {
		// given
		ApplicationContext context = mock(ApplicationContext.class);
		when(context.getBean(TestClass.class)).thenReturn(new TestClass());
		CommandFactoryBean commandFactoryBean = new CommandFactoryBean(TestClass.class.getDeclaredMethods()[0]);
		commandFactoryBean.setApplicationContext(context);

		// when
		org.springframework.shell.core.command.Command result = commandFactoryBean.getObject();

		// then
		assertEquals("hello", result.getName());
		List<CommandOption> options = result.getOptions();
		assertEquals(4, options.size());

		assertEquals("myOption1", options.get(0).longName());
		assertEquals(' ', options.get(0).shortName());

		assertEquals("myOption2", options.get(1).longName());
		assertEquals('m', options.get(1).shortName());

		assertEquals("longNameOption3", options.get(2).longName());
		assertEquals('l', options.get(2).shortName());

		assertEquals("longNameOption4", options.get(3).longName());
		assertEquals(' ', options.get(3).shortName());
	}

	static class TestClass {

		@Command(name = "hello")
		public void helloMethod(@Option String myOption1, @Option(shortName = 'm') String myOption2,
				@Option(longName = "longNameOption3", shortName = 'l') String myOption3,
				@Option(longName = "longNameOption4") String myOption4) {
			// no-op
		}

	}

}