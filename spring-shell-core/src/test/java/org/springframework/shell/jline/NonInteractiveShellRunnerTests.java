/*
 * Copyright 2022-2024 the original author or authors.
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
package org.springframework.shell.jline;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;
import org.springframework.shell.context.DefaultShellContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class NonInteractiveShellRunnerTests {

	@Spy
	@InjectMocks
	private Shell shell;

	@Test
	public void testEmptyArgsDontRun() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(null, null);
		assertThat(runner.run(new String[0])).isFalse();
	}

	@Test
	public void testNonEmptyArgsRun() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, new DefaultShellContext());
		ArgumentCaptor<InputProvider> valueCapture = ArgumentCaptor.forClass(InputProvider.class);
		Mockito.doNothing().when(shell).run(valueCapture.capture());
		assertThat(runner.run(ofArgs("hi"))).isTrue();
	}

	@Test
	public void shouldQuoteWithWhitespace() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, new DefaultShellContext());
		ArgumentCaptor<InputProvider> valueCapture = ArgumentCaptor.forClass(InputProvider.class);
		Mockito.doNothing().when(shell).run(valueCapture.capture());
		assertThat(runner.run(ofArgs("foo bar"))).isTrue();
		InputProvider value = valueCapture.getValue();
		assertThat(value.readInput().rawText()).isEqualTo("\"foo bar\"");
	}

	@Test
	public void shouldNotQuoteIfQuoted() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, new DefaultShellContext());
		ArgumentCaptor<InputProvider> valueCapture = ArgumentCaptor.forClass(InputProvider.class);
		Mockito.doNothing().when(shell).run(valueCapture.capture());
		assertThat(runner.run(ofArgs("'foo bar'"))).isTrue();
		InputProvider value = valueCapture.getValue();
		assertThat(value.readInput().rawText()).isEqualTo("'foo bar'");
	}

	@Test
	public void shouldNotQuoteWithoutWhitespace() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, new DefaultShellContext());
		ArgumentCaptor<InputProvider> valueCapture = ArgumentCaptor.forClass(InputProvider.class);
		Mockito.doNothing().when(shell).run(valueCapture.capture());
		assertThat(runner.run(ofArgs("foobar"))).isTrue();
		InputProvider value = valueCapture.getValue();
		assertThat(value.readInput().rawText()).isEqualTo("foobar");
	}

	@Test
	void oldApiCanRunReturnFalse() {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, null);
		assertThat(runner.canRun(ofApplicationArguments())).isFalse();
	}

	@Test
	void oldApiRunThrows() {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, null);
		assertThatThrownBy(() -> {
			runner.run(ofApplicationArguments());
		});
	}

	private static ApplicationArguments ofApplicationArguments(String... args) {
		return new DefaultApplicationArguments(args);
	}

	private static String[] ofArgs(String... args) {
		String[] a = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			a[i] = args[i];
		}
		return a;
	}

}
