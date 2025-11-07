/*
 * Copyright 2022-present the original author or authors.
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
package org.springframework.shell.core.jline;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.shell.core.InputProvider;
import org.springframework.shell.core.Shell;
import org.springframework.shell.core.context.DefaultShellContext;
import org.springframework.shell.core.jline.NonInteractiveShellRunner;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NonInteractiveShellRunnerTests {

	@Spy
	@InjectMocks
	private Shell shell;

	@Test
	void testEmptyArgsDontRun() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, null);
		runner.run(new String[0]);
		Mockito.verifyNoInteractions(shell);
	}

	@Test
	void testNonEmptyArgsRun() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, new DefaultShellContext());
		ArgumentCaptor<InputProvider> valueCapture = ArgumentCaptor.forClass(InputProvider.class);
		Mockito.doNothing().when(shell).run(valueCapture.capture());
		runner.run(ofArgs("hi"));
		Mockito.verify(shell).run(Mockito.any(InputProvider.class));
	}

	@Test
	void shouldQuoteWithWhitespace() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, new DefaultShellContext());
		ArgumentCaptor<InputProvider> valueCapture = ArgumentCaptor.forClass(InputProvider.class);
		Mockito.doNothing().when(shell).run(valueCapture.capture());
		runner.run(ofArgs("foo bar"));
		InputProvider value = valueCapture.getValue();
		assertThat(value.readInput().rawText()).isEqualTo("\"foo bar\"");
	}

	@Test
	void shouldNotQuoteIfQuoted() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, new DefaultShellContext());
		ArgumentCaptor<InputProvider> valueCapture = ArgumentCaptor.forClass(InputProvider.class);
		Mockito.doNothing().when(shell).run(valueCapture.capture());
		runner.run(ofArgs("'foo bar'"));
		InputProvider value = valueCapture.getValue();
		assertThat(value.readInput().rawText()).isEqualTo("'foo bar'");
	}

	@Test
	void shouldNotQuoteWithoutWhitespace() throws Exception {
		NonInteractiveShellRunner runner = new NonInteractiveShellRunner(shell, new DefaultShellContext());
		ArgumentCaptor<InputProvider> valueCapture = ArgumentCaptor.forClass(InputProvider.class);
		Mockito.doNothing().when(shell).run(valueCapture.capture());
		runner.run(ofArgs("foobar"));
		InputProvider value = valueCapture.getValue();
		assertThat(value.readInput().rawText()).isEqualTo("foobar");
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
