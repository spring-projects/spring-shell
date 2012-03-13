package org.springframework.roo.shell.converters;

import org.springframework.roo.shell.Converter;

/**
 * Interface for adding and removing classes that provide static fields which should
 * be made available via a {@link Converter}.
 *
 * @author Ben Alex
 * @since 1.0
 */
public interface StaticFieldConverter extends Converter<Object> {

	void add(Class<?> clazz);

	void remove(Class<?> clazz);
}