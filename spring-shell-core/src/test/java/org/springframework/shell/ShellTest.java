/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link Shell}.
 *
 * @author Eric Bottard
 */
@ExtendWith(MockitoExtension.class)
public class ShellTest {

	@Mock
	private InputProvider inputProvider;

	@Mock
	ResultHandlerService resultHandlerService;

	@Mock
	CommandRegistry commandRegistry;

	@Mock
	private ParameterResolver parameterResolver;

	private ValueResult valueResult;

	@InjectMocks
	private Shell shell;

	private boolean invoked;

	@BeforeEach
	public void setUp() {
		shell.parameterResolvers = Arrays.asList(parameterResolver);
	}

	@Test
	public void commandMatch() throws IOException {
		when(parameterResolver.supports(any())).thenReturn(true);
		when(inputProvider.readInput()).thenReturn(() -> "hello world how are you doing ?");
		valueResult = new ValueResult(null, "test");
		when(parameterResolver.resolve(any(), any())).thenReturn(valueResult);
		doThrow(new Exit()).when(resultHandlerService).handle(any());

		when(commandRegistry.listCommands()).thenReturn(Collections.singletonMap("hello world",
				MethodTarget.of("helloWorld", this, new Command.Help("Say hello"))));

		try {
			shell.run(inputProvider);
			fail("Exit expected");
		}
		catch (Exit expected) {

		}

		assertThat(invoked).isTrue();
	}

	@Test
	public void commandNotFound() throws IOException {
		when(inputProvider.readInput()).thenReturn(() -> "hello world how are you doing ?");
		doThrow(new Exit()).when(resultHandlerService).handle(isA(CommandNotFound.class));

		when(commandRegistry.listCommands()).thenReturn(Collections.singletonMap("bonjour",
				MethodTarget.of("helloWorld", this, new Command.Help("Say hello"))));

		try {
			shell.run(inputProvider);
			fail("Exit expected");
		}
		catch (Exit expected) {

		}
	}

	@Test
	// See https://github.com/spring-projects/spring-shell/issues/142
	public void commandNotFoundPrefix() throws IOException {
		when(inputProvider.readInput()).thenReturn(() -> "helloworld how are you doing ?");
		doThrow(new Exit()).when(resultHandlerService).handle(isA(CommandNotFound.class));

		when(commandRegistry.listCommands()).thenReturn(
				Collections.singletonMap("hello", MethodTarget.of("helloWorld", this, new Command.Help("Say hello"))));

		try {
			shell.run(inputProvider);
			fail("Exit expected");
		}
		catch (Exit expected) {

		}
	}

	@Test
	public void noCommand() throws IOException {
		when(parameterResolver.supports(any())).thenReturn(true);
		when(inputProvider.readInput()).thenReturn(() -> "", () -> "hello world how are you doing ?", null);
		valueResult = new ValueResult(null, "test");
		when(parameterResolver.resolve(any(), any())).thenReturn(valueResult);
		doThrow(new Exit()).when(resultHandlerService).handle(any());

		when(commandRegistry.listCommands()).thenReturn(Collections.singletonMap("hello world",
				MethodTarget.of("helloWorld", this, new Command.Help("Say hello"))));

		try {
			shell.run(inputProvider);
			fail("Exit expected");
		}
		catch (Exit expected) {

		}

		assertThat(invoked).isTrue();
	}

	@Test
	public void commandThrowingAnException() throws IOException {
		when(inputProvider.readInput()).thenReturn(() -> "fail");
		doThrow(new Exit()).when(resultHandlerService).handle(isA(SomeException.class));

		when(commandRegistry.listCommands()).thenReturn(Collections.singletonMap("fail",
				MethodTarget.of("failing", this, new Command.Help("Will throw an exception"))));

		try {
			shell.run(inputProvider);
			fail("Exit expected");
		}
		catch (Exit expected) {

		}

		assertThat(invoked).isTrue();
	}

	@Test
	public void comments() throws IOException {
		when(inputProvider.readInput()).thenReturn(() -> "// This is a comment", (Input) null);

		shell.run(inputProvider);
	}

	@Test
	public void commandNameCompletion() throws Exception {
		Map<String, MethodTarget> methodTargets = new HashMap<>();
		methodTargets.put("hello world", MethodTarget.of("helloWorld", this, new Command.Help("hellow world")));
		methodTargets.put("another command", MethodTarget.of("helloWorld", this, new Command.Help("another command")));
		when(parameterResolver.supports(any())).thenReturn(true);
		when(commandRegistry.listCommands()).thenReturn(methodTargets);

		// Invoke at very start
		List<String> proposals = shell.complete(new CompletionContext(Arrays.asList(""), 0, "".length()))
				.stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(proposals).containsExactlyInAnyOrder("another command", "hello world");
		// assertThat(proposals).containsExactly("another command", "hello world");

		// Invoke in middle of first word
		proposals = shell.complete(new CompletionContext(Arrays.asList("hel"), 0, "hel".length()))
				.stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(proposals).containsExactly("hello world");

		// Invoke at end of first word (no space after yet)
		proposals = shell.complete(new CompletionContext(Arrays.asList("hello"), 0, "hello".length()))
				.stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(proposals).containsExactly("hello world");

		// Invoke after first word / start of second word
		proposals = shell.complete(new CompletionContext(Arrays.asList("hello", ""), 1, "".length()))
				.stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(proposals).containsExactly("world");

		// Invoke in middle of second word
		proposals = shell.complete(new CompletionContext(Arrays.asList("hello", "wo"), 1, "wo".length()))
				.stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(proposals).containsExactly("world");

		// Invoke at end of whole command (no space after yet)
		proposals = shell.complete(new CompletionContext(Arrays.asList("hello", "world"), 1, "world".length()))
				.stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(proposals).containsExactly("world");

		// Invoke in middle of second word
		proposals = shell.complete(new CompletionContext(Arrays.asList("hello", "world", ""), 2, "".length()))
				.stream().map(CompletionProposal::value).collect(Collectors.toList());
		assertThat(proposals).isEmpty();
	}

	@SuppressWarnings("unused")
	private void helloWorld(String a) {
		invoked = true;
	}

	@SuppressWarnings("unused")
	private String failing() {
		invoked = true;
		throw new SomeException();
	}


	private static class Exit extends RuntimeException {
	}

	private static class SomeException extends RuntimeException {

	}


}
