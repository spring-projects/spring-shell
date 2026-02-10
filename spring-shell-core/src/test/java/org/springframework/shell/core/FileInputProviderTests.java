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
package org.springframework.shell.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author David Pilar
 */
class FileInputProviderTests {

	@TempDir(cleanup = CleanupMode.ALWAYS)
	private File tempDir;

	@Test
	void testReadInput() throws Exception {
		// given
		File inputFile = new File(tempDir, "input.txt");
		String inputContent = """
				echo Hello World
				// This is a comment
				echo Line 1\\
				Line 2

				echo Line 3""";
		Files.writeString(inputFile.toPath(), inputContent);

		// when & then
		try (FileInputProvider inputProvider = new FileInputProvider(inputFile)) {
			assertEquals("echo Hello World", inputProvider.readInput());
			assertEquals("echo Line 1 Line 2", inputProvider.readInput());
			assertEquals("echo Line 3", inputProvider.readInput());
			assertNull(inputProvider.readInput());
		}
	}

}