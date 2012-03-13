package org.springframework.roo.support.util;

/**
 * Utilities for handling OS-specific behavior.
 *
 * @author Joris Kuipers
 * @since 1.1.1
 */
public class OsUtils {
	private static final boolean WINDOWS_OS = System.getProperty("os.name").toLowerCase().contains("windows");

	public static boolean isWindows() {
		return WINDOWS_OS;
	}
}
