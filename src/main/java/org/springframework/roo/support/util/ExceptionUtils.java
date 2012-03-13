package org.springframework.roo.support.util;

/**
 * Methods for working with exceptions.
 *
 * @author Ben Alex
 * @since 1.0
 */
public abstract class ExceptionUtils {

	/**
	 * Obtains the root cause of an exception, if available.
	 *
	 * @param ex to extract the root cause from (required)
	 * @return the root cause, or original exception is unavailable (guaranteed to never be null)
	 */
	public final static Throwable extractRootCause(final Throwable ex) {
		Assert.notNull(ex, "An exception is required");
		Throwable root = ex;
		if (ex.getCause() != null) {
			root = ex.getCause();
		}
		return root;
	}
}
