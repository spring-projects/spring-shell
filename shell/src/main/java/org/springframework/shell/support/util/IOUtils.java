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

import java.io.Closeable;
import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * Static helper methods relating to I/O. Inspired by the eponymous class in
 * Apache Commons I/O.
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public final class IOUtils {

	/**
	 * Quietly closes each of the given {@link Closeable}s, i.e. eats any
	 * {@link IOException}s arising.
	 *
	 * @param closeables the closeables to close (any of which can be
	 * <code>null</code> or already closed)
	 */
	public static void closeQuietly(final Closeable... closeables) {
		for (final Closeable closeable : closeables) {
			if (closeable != null) {
				try {
					closeable.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
	}

	/**
	 * Quietly closes each of the given {@link ZipFile}s, i.e. eats any
	 * {@link IOException}s arising.
	 *
	 * @param zipFiles the zipFiles to close (any of which can be
	 * <code>null</code> or already closed)
	 */
	public static void closeQuietly(final ZipFile... zipFiles) {
		for (final ZipFile zipFile : zipFiles) {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
	}

	/**
	 * Constructor is private to prevent instantiation
	 */
	private IOUtils() {}
}
