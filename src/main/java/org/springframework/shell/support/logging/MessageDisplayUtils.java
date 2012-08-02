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
package org.springframework.shell.support.logging;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.shell.support.util.IOUtils;
import org.springframework.util.FileCopyUtils;

/**
 * Retrieves text files from the classloader and displays them on-screen.
 *
 * <p>
 * Respects normal Roo conventions such as all resources should appear under the same
 * package as the bundle itself etc.
 *
 * @author Ben Alex
 * @since 1.1.1
 */
public abstract class MessageDisplayUtils {

	// Constants
	private static Logger LOGGER = HandlerUtils.getLogger(MessageDisplayUtils.class);

	/**
	 * Displays the requested file via the LOGGER API.
	 *
	 * <p>
	 * Each file must available from the classloader of the "owner". It must also be in the same
	 * package as the class of the "owner". So if the owner is com.foo.Bar, and the file is called
	 * "hello.txt", the file must appear in the same bundle as com.foo.Bar and be available from
	 * the resource path "/com/foo/Hello.txt".
	 *
	 * @param fileName the simple filename (required)
	 * @param owner the class which owns the file (required)
	 * @param important if true, it will display with a higher importance color where possible
	 */
	public static void displayFile(final String fileName, final Class<?> owner, final boolean important) {
		Level level = important ? Level.SEVERE : Level.FINE;
		String owningPackage = owner.getPackage().getName().replace('.', '/');
		String fullResourceName = "/" + owningPackage + "/" + fileName;
		InputStream inputStream = owner.getClassLoader().getResourceAsStream(fullResourceName);
		if (inputStream == null) {
			throw new IllegalStateException("Could not locate '" + fileName + "'");
		}
		try {
			String message = FileCopyUtils.copyToString(new InputStreamReader(new BufferedInputStream(inputStream)));
			LOGGER.log(level, message);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	/**
	 * Same as {@link #displayFile(String, Class, boolean)} except it passes false as the
	 * final argument.
	 *
	 * @param fileName the simple filename (required)
	 * @param owner the class which owns the file (required)
	 */
	public static void displayFile(final String fileName, final Class<?> owner) {
		displayFile(fileName, owner, false);
	}
}
