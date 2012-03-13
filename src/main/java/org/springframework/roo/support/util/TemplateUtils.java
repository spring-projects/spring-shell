package org.springframework.roo.support.util;

import java.io.InputStream;

/**
 * Utilities for dealing with "templates", which are commonly used by ROO add-ons.
 *
 * @author Ben Alex
 * @since 1.0
 */
public final class TemplateUtils {

	/**
	 * Determines the path to the requested template.
	 *
	 * @param clazz which owns the template (required)
	 * @param templateFilename the filename of the template (required)
	 * @return the full classloader-specific path to the template (never null)
	 * @deprecated use {@link FileUtils#getPath(Class, String)} instead
	 */
	@Deprecated
	public static String getTemplatePath(final Class<?> loadingClass, final String relativeFilename) {
		return FileUtils.getPath(loadingClass, relativeFilename);
	}

	/**
	 * Acquires an {@link InputStream} to the requested classloader-derived template.
	 *
	 * @param clazz which owns the template (required)
	 * @param templateFilename the filename of the template (required)
	 * @return the input stream (never null; an exception is thrown if cannot be found)
	 * @deprecated use {@link FileUtils#getInputStream(Class, String)} instead
	 */
	@Deprecated
	public static InputStream getTemplate(final Class<?> clazz, final String templateFilename) {
		return clazz.getResourceAsStream(templateFilename);
	}
	
	/**
	 * Constructor is private to prevent instantiation
	 */
	private TemplateUtils() {}
}
