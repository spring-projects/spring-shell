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
