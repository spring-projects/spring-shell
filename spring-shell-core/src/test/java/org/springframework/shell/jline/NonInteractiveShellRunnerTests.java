/*
 * Copyright 2022 the original author or authors.
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

import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;
import org.springframework.shell.context.DefaultShellContext;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class NonInteractiveShellRunnerTests {

	@Spy
	@InjectMocks
	private Shell shell;

	@Test
	public void testEmptyArgsDontRun() {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(null, null);
		DefaultApplicationArguments args = new DefaultApplicationArguments();
		assertThat(runner.canRun(args)).isFalse();
	}

	@Test
	public void testNonEmptyArgsRun() {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(null, null);
		DefaultApplicationArguments args = new DefaultApplicationArguments("hi");
		assertThat(runner.canRun(args)).isTrue();
	}

	@Test
	public void shouldQuoteWithWhitespace() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, new DefaultShellContext());
		DefaultApplicationArguments args = new DefaultApplicationArguments("foo bar");
		ArgumentCaptor<InputProvider> valueCapture = ArgumentCaptor.forClass(InputProvider.class);
		Mockito.doNothing().when(shell).run(valueCapture.capture());
		runner.run(args);
		InputProvider value = valueCapture.getValue();
		assertThat(value.readInput().rawText()).isEqualTo("\"foo bar\"");
	}

	@Test
	public void shouldNotQuoteIfQuoted() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, new DefaultShellContext());
		DefaultApplicationArguments args = new DefaultApplicationArguments("'foo bar'");
		ArgumentCaptor<InputProvider> valueCapture = ArgumentCaptor.forClass(InputProvider.class);
		Mockito.doNothing().when(shell).run(valueCapture.capture());
		runner.run(args);
		InputProvider value = valueCapture.getValue();
		assertThat(value.readInput().rawText()).isEqualTo("'foo bar'");
	}

	@Test
	public void shouldNotQuoteWithoutWhitespace() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, new DefaultShellContext());
		DefaultApplicationArguments args = new DefaultApplicationArguments("foobar");
		ArgumentCaptor<InputProvider> valueCapture = ArgumentCaptor.forClass(InputProvider.class);
		Mockito.doNothing().when(shell).run(valueCapture.capture());
		runner.run(args);
		InputProvider value = valueCapture.getValue();
		assertThat(value.readInput().rawText()).isEqualTo("foobar");
	}
}
