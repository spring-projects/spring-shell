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