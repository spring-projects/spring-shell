/*
 * Copyright 2017-present the original author or authors.
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
package org.springframework.shell.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.jline.terminal.Terminal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.CommandRegistration;
import org.springframework.shell.core.completion.RegistrationOptionsCompletionResolver;
import org.springframework.shell.core.context.ShellContext;

import static org.assertj.core.api.Assertions.*;
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
class ShellTests {

	@Mock
	private InputProvider inputProvider;

	@Mock
	ResultHandlerService resultHandlerService;

	@Mock
	CommandRegistry commandRegistry;

	@Mock
	Terminal terminal;

	@Mock
	ShellContext shellContext;

	@InjectMocks
	private Shell shell;

	@BeforeEach
	void setUp() {
		shell.setCompletionResolvers(List.of(new RegistrationOptionsCompletionResolver()));
	}

	@Test
	void commandMatch() {
		when(inputProvider.readInput()).thenReturn(() -> "hello world how are you doing ?");
		doThrow(new Exit()).when(resultHandlerService).handle(any());

		CommandRegistration registration = CommandRegistration.builder()
			.command("hello world")
			.withTarget()
				.method(this, "helloWorld")
				.and()
			.build();
		Map<String, CommandRegistration> registrations = new HashMap<>();
		registrations.put("hello world", registration);
		when(commandRegistry.getRegistrations()).thenReturn(registrations);

		assertThatExceptionOfType(Exit.class).isThrownBy(() -> shell.run(inputProvider));
	}

	@Test
	void commandNotFound() {
		when(inputProvider.readInput()).thenReturn(() -> "hello world how are you doing ?");
		doThrow(new Exit()).when(resultHandlerService).handle(isA(CommandNotFound.class));

		CommandRegistration registration = CommandRegistration.builder()
			.command("bonjour")
			.withTarget()
				.method(this, "helloWorld")
				.and()
			.build();
		Map<String, CommandRegistration> registrations = new HashMap<>();
		registrations.put("hello world", registration);
		when(commandRegistry.getRegistrations()).thenReturn(registrations);

		assertThatExceptionOfType(Exit.class).isThrownBy(() -> shell.run(inputProvider));
	}

	@Test
	// See https://github.com/spring-projects/spring-shell/issues/142
	void commandNotFoundPrefix() {
		when(inputProvider.readInput()).thenReturn(() -> "helloworld how are you doing ?");
		doThrow(new Exit()).when(resultHandlerService).handle(isA(CommandNotFound.class));

		CommandRegistration registration = CommandRegistration.builder()
			.command("hello world")
			.withTarget()
				.method(this, "helloWorld")
				.and()
			.build();
		Map<String, CommandRegistration> registrations = new HashMap<>();
		registrations.put("hello world", registration);
		when(commandRegistry.getRegistrations()).thenReturn(registrations);

		assertThatExceptionOfType(Exit.class).isThrownBy(() -> shell.run(inputProvider));
	}

	@Test
	void noCommand() {
		when(inputProvider.readInput()).thenReturn(() -> "", () -> "hello world how are you doing ?", null);
		doThrow(new Exit()).when(resultHandlerService).handle(any());

		CommandRegistration registration = CommandRegistration.builder()
			.command("hello world")
			.withTarget()
				.method(this, "helloWorld")
				.and()
			.build();
		Map<String, CommandRegistration> registrations = new HashMap<>();
		registrations.put("hello world", registration);
		when(commandRegistry.getRegistrations()).thenReturn(registrations);

		assertThatExceptionOfType(Exit.class).isThrownBy(() -> shell.run(inputProvider));
	}

	@Test
	void commandThrowingAnException() {
		when(inputProvider.readInput()).thenReturn(() -> "fail");
		doThrow(new Exit()).when(resultHandlerService).handle(isA(SomeException.class));

		CommandRegistration registration = CommandRegistration.builder()
			.command("fail")
			.withTarget()
				.method(this, "failing")
				.and()
			.build();
		Map<String, CommandRegistration> registrations = new HashMap<>();
		registrations.put("fail", registration);
		when(commandRegistry.getRegistrations()).thenReturn(registrations);

		assertThatExceptionOfType(Exit.class).isThrownBy(() -> shell.run(inputProvider));
	}

	@Test
	void comments() throws Exception {
		when(inputProvider.readInput()).thenReturn(() -> "// This is a comment", (Input) null);

		shell.run(inputProvider);
	}

	@Test
	void commandNameCompletion() {
		CommandRegistration registration1 = CommandRegistration.builder()
			.command("hello world")
			.withTarget()
				.method(this, "helloWorld")
				.and()
			.build();
		CommandRegistration registration2 = CommandRegistration.builder()
			.command("another command")
			.withTarget()
				.method(this, "helloWorld")
				.and()
			.build();
		Map<String, CommandRegistration> registrations = new HashMap<>();
		registrations.put("hello world", registration1);
		registrations.put("another command", registration2);
		when(commandRegistry.getRegistrations()).thenReturn(registrations);

		// Invoke at very start
		List<String> proposals = shell.complete(new CompletionContext(List.of(""), 0, "".length(), null, null))
				.stream().map(CompletionProposal::value).toList();
		assertThat(proposals).containsExactlyInAnyOrder("another command", "hello world");

		// Invoke in middle of first word
		proposals = shell.complete(new CompletionContext(List.of("hel"), 0, "hel".length(), null, null))
				.stream().map(CompletionProposal::value).toList();
		assertThat(proposals).containsExactly("hello world");

		// Invoke at end of first word (no space after yet)
		proposals = shell.complete(new CompletionContext(List.of("hello"), 0, "hello".length(), null, null))
				.stream().map(CompletionProposal::value).toList();
		assertThat(proposals).containsExactly("hello world");

		// Invoke after first word / start of second word
		proposals = shell.complete(new CompletionContext(List.of("hello", ""), 1, "".length(), null, null))
				.stream().map(CompletionProposal::value).toList();
		assertThat(proposals).containsExactly("world");

		// Invoke in middle of second word
		proposals = shell.complete(new CompletionContext(List.of("hello", "wo"), 1, "wo".length(), null, null))
				.stream().map(CompletionProposal::value).toList();
		assertThat(proposals).containsExactly("world");

		// Invoke at end of whole command (no space after yet)
		proposals = shell.complete(new CompletionContext(List.of("hello", "world"), 1, "world".length(), null, null))
				.stream().map(CompletionProposal::value).toList();
		assertThat(proposals).containsExactly("world");

		// Invoke in middle of second word
		proposals = shell.complete(new CompletionContext(List.of("hello", "world", ""), 2, "".length(), null, null))
				.stream().map(CompletionProposal::value).toList();
		assertThat(proposals).isEmpty();
	}

	@SuppressWarnings("unused")
	private void helloWorld(String a) {
	}

	@SuppressWarnings("unused")
	private String failing() {
		throw new SomeException();
	}

	@Test
	void completionArgWithMethod() {
		CommandRegistration registration1 = CommandRegistration.builder()
			.command("hello world")
			.withTarget()
				.method(this, "helloWorld")
				.and()
			.withOption()
				.longNames("arg1")
				.description("arg1 desc")
				.and()
			.build();
		Map<String, CommandRegistration> registrations = new HashMap<>();
		registrations.put("hello world", registration1);
		when(commandRegistry.getRegistrations()).thenReturn(registrations);

		List<String> proposals = shell.complete(new CompletionContext(List.of("hello", "world", ""), 2, "".length(), null, null))
				.stream().map(CompletionProposal::value).toList();
		assertThat(proposals).containsExactlyInAnyOrder("--arg1");
	}

	@Test
	void completionArgWithFunction() {
		CommandRegistration registration1 = CommandRegistration.builder()
			.command("hello world")
			.withTarget()
				.function(ctx -> {
					return null;
				})
				.and()
			.withOption()
				.longNames("arg1")
				.description("arg1 desc")
				.and()
			.build();
		Map<String, CommandRegistration> registrations = new HashMap<>();
		registrations.put("hello world", registration1);
		when(commandRegistry.getRegistrations()).thenReturn(registrations);

		List<String> proposals = shell.complete(new CompletionContext(List.of("hello", "world", ""), 2, "".length(), null, null))
				.stream().map(CompletionProposal::value).toList();
		assertThat(proposals).containsExactlyInAnyOrder("--arg1");
	}

	@Test
	void shouldCompleteWithCorrectArgument() {
		CommandRegistration registration1 = CommandRegistration.builder()
			.command("hello world")
			.withTarget()
				.method(this, "helloWorld")
				.and()
			.withOption()
				.longNames("arg1")
				.completion(ctx -> Stream.of("arg1Comp1").map(CompletionProposal::new).toList())
				.and()
			.withOption()
				.longNames("arg2")
				.completion(ctx -> Stream.of("arg2Comp1").map(CompletionProposal::new).toList())
				.and()
			.build();
		Map<String, CommandRegistration> registrations = new HashMap<>();
		registrations.put("hello world", registration1);
		when(commandRegistry.getRegistrations()).thenReturn(registrations);

		List<String> proposals1 = shell.complete(new CompletionContext(List.of("hello", "world", "--arg1", ""), 3, "".length(), null, null))
				.stream().map(CompletionProposal::value).toList();
		assertThat(proposals1).containsExactlyInAnyOrder("--arg2", "arg1Comp1");

		List<String> proposals2 = shell.complete(new CompletionContext(List.of("hello", "world", "--arg1", "xxx", "--arg2", ""), 5, "".length(), null, null))
				.stream().map(CompletionProposal::value).toList();
		assertThat(proposals2).containsExactlyInAnyOrder("arg2Comp1");

		List<String> proposals3 = shell.complete(new CompletionContext(List.of("hello", "world", "--arg2", "xxx", "--arg1", ""), 5, "".length(), null, null))
				.stream().map(CompletionProposal::value).toList();
		assertThat(proposals3).containsExactlyInAnyOrder("arg1Comp1");
	}

	private static class Exit extends RuntimeException {
	}

	private static class SomeException extends RuntimeException {

	}
}
