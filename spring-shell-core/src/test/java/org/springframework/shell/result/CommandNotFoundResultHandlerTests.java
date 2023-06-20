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
package org.springframework.shell.result;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jline.terminal.Terminal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.shell.CommandNotFound;
import org.springframework.shell.command.CommandRegistration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommandNotFoundResultHandlerTests {

	@Mock
	ObjectProvider<CommandNotFoundMessageProvider> provider;

	@Mock
	Terminal terminal;

	@Test
	void defaultProviderPrintsBasicMessage() {
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		given(provider.getIfAvailable()).willReturn(null);
		given(provider.getIfAvailable(any())).willCallRealMethod();
		given(terminal.writer()).willReturn(writer);
		CommandNotFoundResultHandler handler = new CommandNotFoundResultHandler(terminal, provider);
		CommandNotFound e = new CommandNotFound(Arrays.asList("one", "two"), Collections.emptyMap(), "one two --xxx");
		handler.handleResult(e);
		String string = out.toString();
		assertThat(string).contains("No command found for 'one two'");
	}

	@Test
	void customProviderPrintsBasicMessage() {
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		given(provider.getIfAvailable()).willReturn(ctx -> "hi");
		given(provider.getIfAvailable(any())).willCallRealMethod();
		given(terminal.writer()).willReturn(writer);
		CommandNotFoundResultHandler handler = new CommandNotFoundResultHandler(terminal, provider);
		CommandNotFound e = new CommandNotFound(Arrays.asList("one", "two"), Collections.emptyMap(), "one two --xxx");
		handler.handleResult(e);
		String string = out.toString();
		assertThat(string).contains("hi");
	}

	@Test
	void customProviderGetsContext() {
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		List<String> commands = Arrays.asList("one", "two");
		Map<String, CommandRegistration> registrations = Collections.emptyMap();
		CommandNotFound e = new CommandNotFound(commands, registrations, "text");
		given(provider.getIfAvailable()).willReturn(ctx -> {
			return String.format("%s%s%s%s%s", "hi", ctx.error() == e ? "true" : "false",
					ctx.commands().stream().collect(Collectors.joining()),
					ctx.registrations() == registrations ? "true" : "false", ctx.text());
		});
		given(provider.getIfAvailable(any())).willCallRealMethod();
		given(terminal.writer()).willReturn(writer);
		CommandNotFoundResultHandler handler = new CommandNotFoundResultHandler(terminal, provider);
		handler.handleResult(e);
		String string = out.toString();
		assertThat(string).contains("hitrueonetwotruetext");
	}
}
