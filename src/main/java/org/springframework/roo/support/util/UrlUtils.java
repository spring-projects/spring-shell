package org.springframework.roo.support.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Utility methods relating to networking types such as URLs.
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public final class UrlUtils {

	/**
	 * Converts the given URI to a URL; equivalent to {@link URI#toURL()}
	 * except that it throws a runtime exception.
	 *
	 * @param uri
	 * @return a non-<code>null</code> URL
	 * @throws IllegalArgumentException if the conversion is not possible
	 */
	public static URL toURL(final URI uri) {
		try {
			return uri.toURL();
		} catch (final MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Constructor is private to prevent instantiation
	 */
	private UrlUtils() {}
}
