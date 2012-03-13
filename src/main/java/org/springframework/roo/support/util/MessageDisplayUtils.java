package org.springframework.roo.support.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.roo.support.logging.HandlerUtils;

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
