package org.springframework.roo.support.util;

/**
 * Allows filtering of objects of type T.
 *
 * @author Andrew Swan
 * @since 1.2.0
 * @param <T> the type of object to be filtered
 */
public interface Filter<T> {

	/**
	 * Indicates whether to include the given instance in the filtered results
	 *
	 * @param type the type to evaluate; can be <code>null</code>
	 * @return <code>false</code> to exclude the given type
	 */
	boolean include(T instance);
}