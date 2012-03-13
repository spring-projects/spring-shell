package org.springframework.roo.shell;

/**
 * Utility methods relating to shell option contexts
 */
public final class CliOptionContext {

	// Class fields
	private static ThreadLocal<String> optionContextHolder = new ThreadLocal<String>();

	/**
	 * Returns the option context for the current thread.
	 *
	 * @return <code>null</code> if none has been set
	 */
	public static String getOptionContext() {
		return optionContextHolder.get();
	}

	/**
	 * Stores the given option context for the current thread.
	 *
	 * @param optionContext the option context to store
	 */
	public static void setOptionContext(final String optionContext) {
		optionContextHolder.set(optionContext);
	}

	/**
	 * Resets the option context for the current thread.
	 */
	public static void resetOptionContext() {
		optionContextHolder.remove();
	}

	/**
	 * Constructor is private to prevent instantiation
	 */
	private CliOptionContext() {}
}
