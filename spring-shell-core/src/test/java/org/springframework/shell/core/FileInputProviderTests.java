/*
 * Copyright 2025-present the original author or authors.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileInputProviderTests {

	@TempDir
	Path tempDir;

	@Test
	void shouldReadSingleLine() throws Exception {
		// given
		File file = createTempFile("hello world");

		// when / then
		try (FileInputProvider provider = new FileInputProvider(file)) {
			String input = provider.readInput();
			assertEquals("hello world", input);
		}
	}

	@Test
	void shouldReturnNullOnEmptyFile() throws Exception {
		// given
		File file = createTempFile("");

		// when / then
		try (FileInputProvider provider = new FileInputProvider(file)) {
			String input = provider.readInput();
			assertNull(input);
		}
	}

	@Test
	void shouldHandleLineContinuation() throws Exception {
		// given
		File file = createTempFile("hello \\\nworld");

		// when / then
		try (FileInputProvider provider = new FileInputProvider(file)) {
			String input = provider.readInput();
			assertEquals("hello  world", input);
		}
	}

	@Test
	void shouldReturnNullForCommentLine() throws Exception {
		// given
		File file = createTempFile("// this is a comment");

		// when / then
		try (FileInputProvider provider = new FileInputProvider(file)) {
			String input = provider.readInput();
			assertNull(input);
		}
	}

	@Test
	void shouldThrowForNonExistentFile() {
		// given
		File nonExistent = new File(tempDir.toFile(), "nonexistent.txt");

		// when / then
		assertThrows(FileNotFoundException.class, () -> new FileInputProvider(nonExistent));
	}

	private File createTempFile(String content) throws IOException {
		Path file = tempDir.resolve("test-input.txt");
		Files.writeString(file, content);
		return file.toFile();
	}

}
