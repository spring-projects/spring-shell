/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.support.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.springframework.shell.support.util.loader.Loader;
import org.springframework.util.FileCopyUtils;

/**
 * Unit test of {@link FileUtils}
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public class FileUtilsTest {

	private static final String MISSING_FILE = "no-such-file.txt";
	private static final String TEST_FILE = "sub" + File.separator + "file-utils-test.txt";

	@Test(expected = NullPointerException.class)
	public void testGetSystemDependentPathFromNullArray() {
		FileUtils.getSystemDependentPath((String[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetSystemDependentPathFromNoElements() {
		FileUtils.getSystemDependentPath();
	}

	@Test
	public void testGetSystemDependentPathFromOneElement() {
		assertEquals("foo", FileUtils.getSystemDependentPath("foo"));
	}

	@Test
	public void testGetSystemDependentPathFromMultipleElements() {
		final String expectedPath = "foo" + File.separator + "bar";
		assertEquals(expectedPath, FileUtils.getSystemDependentPath("foo", "bar"));
	}

	@Test
	public void testGetFileSeparatorAsRegex() throws Exception {
		// Set up
		final String regex = FileUtils.getFileSeparatorAsRegex();
		final String currentDirectory = new File(FileUtils.CURRENT_DIRECTORY).getCanonicalPath();

		// Invoke
		final String[] pathElements = currentDirectory.split(regex);

		// Check
		assertTrue(pathElements.length > 0);
	}

	@Test
	public void testRemoveTrailingSeparatorFromNullPath() {
		assertNull(FileUtils.removeTrailingSeparator(null));
	}

	@Test
	public void testRemoveTrailingSeparatorFromEmptyPath() {
		assertEquals("", FileUtils.removeTrailingSeparator(""));
	}

	@Test
	public void testRemoveTrailingSeparatorFromPathWithLeadingSeparator() {
		final String path = File.separator + "foo";
		assertEquals(path, FileUtils.removeTrailingSeparator(path));
	}

	@Test
	public void testRemoveTrailingSeparatorFromPathWithMultipleTrailingSeparators() {
		final String path = "foo" + File.separator + File.separator + File.separator;
		assertEquals("foo", FileUtils.removeTrailingSeparator(path));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEnsureTrailingSeparatorForNullPath() {
		FileUtils.ensureTrailingSeparator(null);
	}

	@Test
	public void testEnsureTrailingSeparatorForEmptyPath() {
		assertEquals(File.separator, FileUtils.ensureTrailingSeparator(""));
	}

	@Test
	public void testEnsureTrailingSeparatorForPathWithNoTrailingSeparator() {
		final String path = "foo";
		assertEquals(path + File.separator, FileUtils.ensureTrailingSeparator(path));
	}

	@Test
	public void testEnsureTrailingSeparatorForPathWithOneTrailingSeparator() {
		final String path = "foo" + File.separator;
		assertEquals(path, FileUtils.ensureTrailingSeparator(path));
	}

	@Test
	public void testEnsureTrailingSeparatorFromPathWithMultipleTrailingSeparators() {
		final String path = "foo" + File.separator + File.separator + File.separator;
		assertEquals("foo" + File.separator, FileUtils.ensureTrailingSeparator(path));
	}

	@Test
	public void testGetCanonicalPathForNullFile() {
		assertNull(FileUtils.getCanonicalPath(null));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetCanonicalPathForInvalidFile() throws Exception {
		// Set up
		final File invalidFile = mock(File.class);
		when(invalidFile.getCanonicalPath()).thenThrow(new IOException("dummy"));

		// Invoke
		FileUtils.getCanonicalPath(invalidFile);
	}

	@Test
	public void testGetCanonicalPathForValidFile() throws Exception {
		// Set up
		final File validFile = mock(File.class);
		final String canonicalPath = "the_path";
		when(validFile.getCanonicalPath()).thenReturn(canonicalPath);

		// Invoke
		final String actualPath = FileUtils.getCanonicalPath(validFile);

		// Check
		assertEquals(canonicalPath, actualPath);
	}

	@Test
	public void testRemoveLeadingAndTrailingSeparatorsFromNullPath() {
		assertNull(FileUtils.removeLeadingAndTrailingSeparators(null));
	}

	@Test
	public void testRemoveLeadingAndTrailingSeparatorsFromEmptyPath() {
		assertEquals("", FileUtils.removeLeadingAndTrailingSeparators(""));
	}

	@Test
	public void testRemoveLeadingAndTrailingSeparatorsFromPlainPath() {
		final String path = "foo";
		assertEquals(path, FileUtils.removeLeadingAndTrailingSeparators(path));
	}

	@Test
	public void testRemoveLeadingAndTrailingSeparatorsFromPathWithBoth() {
		// Set up
		final String separators = File.separator + File.separator + File.separator + File.separator;
		final String path = separators + "foo" + separators;

		// Invoke and check
		assertEquals("foo", FileUtils.removeLeadingAndTrailingSeparators(path));
	}

	@Test
	public void testGetFile() {
		assertTrue(FileUtils.getFile(Loader.class, TEST_FILE).isFile());
	}

	@Test
	public void testGetPath() {
		assertEquals("/org/springframework/shell/support/util/loader/sub/file-utils-test.txt", FileUtils.getPath(Loader.class, "sub/file-utils-test.txt"));
	}

	@Test
	public void testGetInputStreamOfFileInSubDirectory() throws Exception {
		// Invoke
		final InputStream inputStream = FileUtils.getInputStream(Loader.class, TEST_FILE);

		// Check
		final String contents = FileCopyUtils.copyToString(new InputStreamReader(inputStream));
		assertEquals("This file is required for FileUtilsTest.", contents);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetInputStreamOfInvalidFile() throws Exception {
		FileUtils.getInputStream(Loader.class, MISSING_FILE);
	}

	private void assertFirstDirectory(final String path, final String expectedFirstDirectory) {
		// Invoke
		final String firstDirectory = FileUtils.getFirstDirectory(path);

		// Check
		assertEquals(expectedFirstDirectory, firstDirectory);
	}

	@Test
	public void testGetFirstDirectoryOfExistingDirectory() {
		// Set up
		final String directory = FileUtils.getFile(Loader.class, TEST_FILE).getParent();

		// Invoke
		final String firstDirectory = FileUtils.getFirstDirectory(directory);

		// Check
		assertTrue(firstDirectory.endsWith("sub"));
	}

	@Test
	public void testGetFirstDirectoryOfExistingFile() {
		assertFirstDirectory(TEST_FILE, "sub");
	}

	@Test
	public void testBackOneDirectory() {
		assertEquals("foo" + File.separator + "bar", FileUtils.backOneDirectory("foo" + File.separator + "bar" + File.separator + "baz" + File.separator));
	}
}
