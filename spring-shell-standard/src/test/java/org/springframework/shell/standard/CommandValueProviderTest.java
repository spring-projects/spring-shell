/*
 * Copyright 2017 the original author or authors.
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


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.CommandRegistry;
import org.springframework.shell.Utils;
import org.springframework.util.ReflectionUtils;

/**
 * Unit tests for {@link CommandValueProvider}.
 *
 * @author Eric Bottard
 */
public class CommandValueProviderTest {

	@Mock
	private CommandRegistry shell;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testValues() {
		CommandValueProvider valueProvider = new CommandValueProvider(shell);

		Method help = ReflectionUtils.findMethod(Command.class, "help", String.class);
		MethodParameter methodParameter = Utils.createMethodParameter(help, 0);
		CompletionContext completionContext = new CompletionContext(Arrays.asList("help", "m"), 0, 0);
		boolean supports = valueProvider.supports(methodParameter, completionContext);

		assertThat(supports).isEqualTo(true);

		Map<String, MethodTarget> commands = new HashMap<>();
		commands.put("me", null);
		commands.put("meow", null);
		commands.put("yourself", null);
		when(shell.listCommands()).thenReturn(commands);
		List<CompletionProposal> proposals = valueProvider.complete(methodParameter, completionContext, new String[0]);

		assertThat(proposals).extracting("value", String.class)
			.contains("me", "meow", "yourself");
	}


	public static class Command {

		public void help(@ShellOption(valueProvider = CommandValueProvider.class) String command) {

		}
	}

}
