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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;


/**
 * Utilities for handling {@link File} instances.
 *
 * @author Ben Alex
 * @since 1.0
 */
public final class FileUtils {

	// Constants
	private static final String BACKSLASH = "\\";
	private static final String ESCAPED_BACKSLASH = "\\\\";

	/**
	 * The relative file path to the current directory. Should be valid on all
	 * platforms that Roo supports.
	 */
	public static final String CURRENT_DIRECTORY = ".";

	private static final String WINDOWS_DRIVE_PREFIX = "^[A-Za-z]:";

	// Doesn't check for backslash after the colon, since Java has no issues with paths like c:/Windows
	private static final Pattern WINDOWS_DRIVE_PATH = Pattern.compile(WINDOWS_DRIVE_PREFIX + ".*");

	private static final PathMatcher PATH_MATCHER;

	static {
		PATH_MATCHER = new AntPathMatcher();
		((AntPathMatcher) PATH_MATCHER).setPathSeparator(File.separator);
	}

	/**
	 * Deletes the specified {@link File}.
	 *
	 * <p>
	 * If the {@link File} refers to a directory, any contents of that directory (including other directories)
	 * are also deleted.
	 *
	 * <p>
	 * If the {@link File} does not already exist, this method immediately returns true.
	 *
	 * @param file to delete (required; the file may or may not exist)
	 * @return true if the file is fully deleted, or false if there was a failure when deleting
	 */
	public static boolean deleteRecursively(final File file) {
		Assert.notNull(file, "File to delete required");
		if (!file.exists()) {
			return true;
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				if (!deleteRecursively(f)) {
					return false;
				}
			}
		}
		file.delete();
		return true;
	}

	/**
	 * Copies the specified source directory to the destination.
	 *
	 * <p>
	 * Both the source must exist. If the destination does not already exist, it will be created. If the destination
	 * does exist, it must be a directory (not a file).
	 *
	 * @param source the already-existing source directory (required)
	 * @param destination the destination directory (required)
	 * @param deleteDestinationOnExit indicates whether to mark any created destinations for deletion on exit
	 * @return true if the copy was successful
	 */
	public static boolean copyRecursively(final File source, final File destination, final boolean deleteDestinationOnExit) {
		Assert.notNull(source, "Source directory required");
		Assert.notNull(destination, "Destination directory required");
		Assert.isTrue(source.exists(), "Source directory '" + source + "' must exist");
		Assert.isTrue(source.isDirectory(), "Source directory '" + source + "' must be a directory");
		if (destination.exists()) {
			Assert.isTrue(destination.isDirectory(), "Destination directory '" + destination + "' must be a directory");
		}
		else {
			destination.mkdirs();
			if (deleteDestinationOnExit) {
				destination.deleteOnExit();
			}
		}
		for (File s : source.listFiles()) {
			File d = new File(destination, s.getName());
			if (deleteDestinationOnExit) {
				d.deleteOnExit();
			}
			if (s.isFile()) {
				try {
					FileCopyUtils.copy(s, d);
				} catch (IOException ioe) {
					return false;
				}
			}
			else {
				// It's a sub-directory, so copy it
				d.mkdir();
				if (!copyRecursively(s, d, deleteDestinationOnExit)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the provided fileName denotes an absolute path on the file system.
	 * On Windows, this includes both paths with and without drive letters, where the latter have to start with '\'.
	 * No check is performed to see if the file actually exists!
	 *
	 * @param fileName name of a file, which could be an absolute path
	 * @return true if the fileName looks like an absolute path for the current OS
	 */
	public static boolean denotesAbsolutePath(final String fileName) {
		if (OsUtils.isWindows()) {
			// first check for drive letter
			if (WINDOWS_DRIVE_PATH.matcher(fileName).matches()) {
				return true;
			}
		}
		return fileName.startsWith(File.separator);
	}

	/**
	 * Returns the part of the given path that represents a directory, in other
	 * words the given path if it's already a directory, or the parent directory
	 * if it's a file.
	 *
	 * @param fileIdentifier the path to parse (required)
	 * @return see above
	 * @since 1.2.0
	 */
	public static String getFirstDirectory(String fileIdentifier) {
		fileIdentifier = removeTrailingSeparator(fileIdentifier);
		if (new File(fileIdentifier).isDirectory()) {
			return fileIdentifier;
		}
		return backOneDirectory(fileIdentifier);
	}

	/**
	 * Returns the given file system path minus its last element
	 *
	 * @param fileIdentifier
	 * @return
	 * @since 1.2.0
	 */
	public static String backOneDirectory(String fileIdentifier) {
		fileIdentifier = removeTrailingSeparator(fileIdentifier);
		fileIdentifier = fileIdentifier.substring(0, fileIdentifier.lastIndexOf(File.separator));
		return removeTrailingSeparator(fileIdentifier);
	}

	/**
	 * Removes any trailing {@link File#separator}s from the given path
	 *
	 * @param path the path to modify (can be <code>null</code>)
	 * @return the modified path
	 * @since 1.2.0
	 */
	public static String removeTrailingSeparator(String path) {
		while (path != null && path.endsWith(File.separator)) {
			path = path.substring(0, path.length() - File.separator.length());
		}
		return path;
	}

	/**
	 * Indicates whether the given canonical path matches the given Ant-style pattern
	 *
	 * @param antPattern the pattern to check against (can't be blank)
	 * @param canonicalPath the path to check (can't be blank)
	 * @return see above
	 * @since 1.2.0
	 */
	public static boolean matchesAntPath(final String antPattern, final String canonicalPath) {
		Assert.hasText(antPattern, "Ant pattern required");
		Assert.hasText(canonicalPath, "Canonical path required");
		return PATH_MATCHER.match(antPattern, canonicalPath);
	}

	/**
	 * Removes any leading or trailing {@link File#separator}s from the given path.
	 *
	 * @param path the path to modify (can be <code>null</code>)
	 * @return the path, modified as above, or <code>null</code> if <code>null</code> was given
	 * @since 1.2.0
	 */
	public static String removeLeadingAndTrailingSeparators(String path) {
		if (!StringUtils.hasText(path)) {
			return path;
		}
		while (path.endsWith(File.separator)) {
			path = path.substring(0, path.length() - File.separator.length());
		}
		while (path.startsWith(File.separator)) {
			path = path.substring(File.separator.length());
		}
		return path;
	}

	/**
	 * Ensures that the given path has exactly one trailing {@link File#separator}
	 *
	 * @param path the path to modify (can't be <code>null</code>)
	 * @return the normalised path
	 * @since 1.2.0
	 */
	public static String ensureTrailingSeparator(final String path) {
		Assert.notNull(path);
		return removeTrailingSeparator(path) + File.separatorChar;
	}

	/**
	 * Returns an operating-system-dependent path consisting of the given
	 * elements, separated by {@link File#separator}.
	 *
	 * @param pathElements the path elements from uppermost downwards (can't be empty)
	 * @return a non-blank string
	 * @since 1.2.0
	 */
	public static String getSystemDependentPath(final String... pathElements) {
		return getSystemDependentPath(Arrays.asList(pathElements));
	}

	/**
	 * Returns an operating-system-dependent path consisting of the given
	 * elements, separated by {@link File#separator}.
	 *
	 * @param pathElements the path elements from uppermost downwards (can't be empty)
	 * @return a non-blank string
	 * @since 1.2.0
	 */
	public static String getSystemDependentPath(final Collection<String> pathElements) {
		Assert.notEmpty(pathElements);
		return StringUtils.collectionToDelimitedString(pathElements, File.separator);
	}

	/**
	 * Returns the canonical path of the given {@link File}.
	 *
	 * @param file the file for which to find the canonical path (can be <code>null</code>)
	 * @return the canonical path, or <code>null</code> if a <code>null</code> file is given
	 * @since 1.2.0
	 */
	public static String getCanonicalPath(final File file) {
		if (file == null) {
			return null;
		}
		try {
			return file.getCanonicalPath();
		} catch (final IOException ioe) {
			throw new IllegalStateException("Cannot determine canonical path for '" + file + "'", ioe);
		}
	}

	/**
	 * Returns the platform-specific file separator as a regular expression.
	 *
	 * @return a non-blank regex
	 * @since 1.2.0
	 */
	public static String getFileSeparatorAsRegex() {
		final String fileSeparator = File.separator;
		if (fileSeparator.contains(BACKSLASH)) {
			// Escape the backslashes
			return fileSeparator.replace(BACKSLASH, ESCAPED_BACKSLASH);
		}
		return fileSeparator;
	}

	/**
	 * Determines the path to the requested file, relative to the given class.
	 *
	 * @param loadingClass the class to whose package the given file is relative (required)
	 * @param relativeFilename the name of the file relative to that package (required)
	 * @return the full classloader-specific path to the file (never <code>null</code>)
	 * @since 1.2.0
	 */
	public static String getPath(final Class<?> loadingClass, final String relativeFilename) {
		Assert.notNull(loadingClass, "Loading class required");
		Assert.hasText(relativeFilename, "Filename required");
		Assert.isTrue(!relativeFilename.startsWith("/"), "Filename shouldn't start with a slash");
		// Slashes instead of File.separatorChar is correct here, as these are classloader paths (not file system paths)
		return "/" + loadingClass.getPackage().getName().replace('.', '/') + "/" + relativeFilename;
	}

	/**
	 * Loads the given file from the classpath.
	 *
	 * @param loadingClass the class from whose package to load the file (required)
	 * @param filename the name of the file to load, relative to that package (required)
	 * @return the file's input stream (never <code>null</code>)
	 * @throws IllegalArgumentException if the given file cannot be found
	 */
	public static File getFile(final Class<?> loadingClass, final String filename) {
		final URL url = loadingClass.getResource(filename);
		Assert.notNull(url, "Could not locate '" + filename + "' in classpath of " + loadingClass.getName());
		try {
			return new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Loads the given file from the classpath.
	 *
	 * @param loadingClass the class from whose package to load the file (required)
	 * @param filename the name of the file to load, relative to that package (required)
	 * @return the file's input stream (never <code>null</code>)
	 * @throws IllegalArgumentException if the given file cannot be found
	 */
	public static InputStream getInputStream(final Class<?> loadingClass, final String filename) {
		final InputStream inputStream = loadingClass.getResourceAsStream(filename);
		Assert.notNull(inputStream, "Could not locate '" + filename + "' in classpath of " + loadingClass.getName());
		return inputStream;
	}

	/**
	 * Reads a banner from the given resource. Performs conversion of any line separator contained by the source to that of the running platform.
	 *
	 * @return platform-compatible banner as a String
	 */
	public static String readBanner(Reader reader) {
		try {
			String content = FileCopyUtils.copyToString(new BufferedReader(reader));
			return content.replaceAll("(\\r|\\n)+", OsUtils.LINE_SEPARATOR);
		} catch (Exception ex) {
			throw new IllegalStateException("Cannot read stream", ex);
		}
	}

	/**
	 * Reads a banner from the given resource. Performs conversion of any line separator contained by the source to that of the running platform.
	 *
	 * @return platform-compatible banner as a String
	 */
	public static String readBanner(final Class<?> loadingClass, String resourceName) {
		return readBanner(new InputStreamReader(getInputStream(loadingClass, resourceName)));
	}

	/**
	 * Returns the contents of the given File as a String.
	 *
	 * @param file the file to read from (must be an existing file)
	 * @return the contents
	 * @throws IllegalStateException in case of I/O errors
	 * @since 1.2.0
	 */
	public static String read(final File file) {
		try {
			return FileCopyUtils.copyToString(new FileReader(file));
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Constructor is private to prevent instantiation
	 *
	 * @since 1.2.0
	 */
	private FileUtils() {
	}
}
