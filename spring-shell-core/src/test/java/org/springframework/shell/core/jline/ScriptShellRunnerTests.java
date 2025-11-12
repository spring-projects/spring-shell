/*
 * Copyright 2024-present the original author or authors.
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

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScriptShellRunnerTests {

	// private final ScriptShellRunner runner = new ScriptShellRunner(null, shell);
	//
	// @Test
	// void shouldNotRunWhenNoArgs() throws Exception {
	// runner.run(ofArgs());
	// Mockito.verifyNoInteractions(shell);
	// }
	//
	// @Test
	// void shouldNotRunWhenInOptionValue() throws Exception {
	// runner.run(ofArgs("--foo", "@"));
	// Mockito.verifyNoInteractions(shell);
	// }
	//
	// @Test
	// void shouldNotRunWhenJustFirstArgWithoutFile() throws Exception {
	// runner.run(ofArgs("@"));
	// Mockito.verifyNoInteractions(shell);
	// }
	//
	// @Test
	// void shouldRunWhenFirstArgHavingFile(@TempDir Path workingDir) throws Exception {
	// Path path = workingDir.resolve("test");
	// Path file = Files.createFile(path);
	// String pathStr = file.toAbsolutePath().toString();
	// ScriptShellRunner shellRunner = new ScriptShellRunner(null, shell);
	// shellRunner.run(new String[] { "@" + pathStr });
	// }
	//
	// private static String[] ofArgs(String... args) {
	// String[] a = new String[args.length];
	// for (int i = 0; i < args.length; i++) {
	// a[i] = args[i];
	// }
	// return a;
	// }

}
