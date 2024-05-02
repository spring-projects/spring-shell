/*
 * Copyright 2024 the original author or authors.
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

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.shell.Shell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class ScriptShellRunnerTests {

	@Mock
	Shell shell;

	private ScriptShellRunner runner = new ScriptShellRunner(null, null);

	@Test
	void shouldNotRunWhenNoArgs() throws Exception {
		assertThat(runner.run(ofArgs())).isFalse();
	}

	@Test
	void shouldNotRunWhenInOptionValue() throws Exception {
		assertThat(runner.run(ofArgs("--foo", "@"))).isFalse();
	}

	@Test
	void shouldNotRunWhenJustFirstArgWithoutFile() throws Exception {
		assertThat(runner.run(ofArgs("@"))).isFalse();
	}

	@Test
	void shouldRunWhenFirstArgHavingFile(@TempDir Path workingDir) throws Exception {
		Path path = workingDir.resolve("test");
		Path file = Files.createFile(path);
		String pathStr = file.toAbsolutePath().toString();
		ScriptShellRunner runner = new ScriptShellRunner(null, shell);
		assertThat(runner.run(new String[]{"@" + pathStr})).isTrue();
	}

	@Test
	void oldApiCanRunReturnFalse() {
		assertThat(runner.canRun(ofApplicationArguments())).isFalse();
	}

	@Test
	void oldApiRunThrows() {
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
