package org.springframework.roo.support.util;

/*
 * Copyright 2002-2008 the original author or authors.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Miscellaneous collection utility methods.
 * Mainly for internal use within the framework.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Andrew Swan
 * @since 1.1.3
 */
public final class CollectionUtils {

	/**
	 * Return <code>true</code> if the supplied Collection is <code>null</code>
	 * or empty. Otherwise, return <code>false</code>.
	 *
	 * @param collection the Collection to check
	 * @return whether the given Collection is empty
	 */
	public static boolean isEmpty(final Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}

	/**
	 * Return <code>true</code> if the supplied Map is <code>null</code>
	 * or empty. Otherwise, return <code>false</code>.
	 *
	 * @param map the Map to check
	 * @return whether the given Map is empty
	 */
	public static boolean isEmpty(final Map<?, ?> map) {
		return (map == null || map.isEmpty());
	}

	/**
	 * Convert the supplied array into a List. A primitive array gets
	 * converted into a List of the appropriate wrapper type.
	 * <p>A <code>null</code> source value will be converted to an
	 * empty List.
	 *
	 * @param source the (potentially primitive) array
	 * @return the converted List result
	 * @see ObjectUtils#toObjectArray(Object)
	 */
	public static List<?> arrayToList(final Object source) {
		return Arrays.asList(ObjectUtils.toObjectArray(source));
	}

	/**
	 * Merge the given array into the given Collection.
	 *
	 * @param array the array to merge (may be <code>null</code>)
	 * @param collection the target Collection to merge the array into
	 */
	public static void mergeArrayIntoCollection(final Object array, final Collection<Object> collection) {
		if (collection == null) {
			throw new IllegalArgumentException("Collection must not be null");
		}
		final Object[] arr = ObjectUtils.toObjectArray(array);
		for (final Object elem : arr) {
			collection.add(elem);
		}
	}

	/**
	 * Merge the given Properties instance into the given Map,
	 * copying all properties (key-value pairs) over.
	 * <p>Uses <code>Properties.propertyNames()</code> to even catch
	 * default properties linked into the original Properties instance.
	 *
	 * @param props the Properties instance to merge (may be <code>null</code>)
	 * @param map the target Map to merge the properties into
	 */
	public static void mergePropertiesIntoMap(final Properties props, final Map<String, String> map) {
		if (map == null) {
			throw new IllegalArgumentException("Map must not be null");
		}
		if (props != null) {
			for (final Enumeration<?> en = props.propertyNames(); en.hasMoreElements();) {
				final String key = (String) en.nextElement();
				map.put(key, props.getProperty(key));
			}
		}
	}

	/**
	 * Check whether the given Iterator contains the given element.
	 *
	 * @param iterator the Iterator to check
	 * @param element the element to look for
	 * @return <code>true</code> if found, <code>false</code> else
	 */
	public static boolean contains(final Iterator<?> iterator, final Object element) {
		if (iterator != null) {
			while (iterator.hasNext()) {
				final Object candidate = iterator.next();
				if (ObjectUtils.nullSafeEquals(candidate, element)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check whether the given Enumeration contains the given element.
	 *
	 * @param enumeration the Enumeration to check
	 * @param element the element to look for
	 * @return <code>true</code> if found, <code>false</code> else
	 */
	public static boolean contains(final Enumeration<?> enumeration, final Object element) {
		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				final Object candidate = enumeration.nextElement();
				if (ObjectUtils.nullSafeEquals(candidate, element)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check whether the given Collection contains the given element instance.
	 * <p>Enforces the given instance to be present, rather than returning
	 * <code>true</code> for an equal element as well.
	 *
	 * @param collection the Collection to check
	 * @param element the element to look for
	 * @return <code>true</code> if found, <code>false</code> else
	 */
	public static boolean containsInstance(final Collection<?> collection, final Object element) {
		if (collection != null) {
			for (final Object candidate : collection) {
				if (candidate == element) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return <code>true</code> if any element in '<code>candidates</code>' is
	 * contained in '<code>source</code>'; otherwise returns <code>false</code>.
	 *
	 * @param source the source Collection
	 * @param candidates the candidates to search for
	 * @return whether any of the candidates has been found
	 */
	public static boolean containsAny(final Collection<?> source, final Collection<?> candidates) {
		if (isEmpty(source) || isEmpty(candidates)) {
			return false;
		}
		for (final Object candidate : candidates) {
			if (source.contains(candidate)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the first element in '<code>candidates</code>' that is contained in
	 * '<code>source</code>'. If no element in '<code>candidates</code>' is present in
	 * '<code>source</code>' returns <code>null</code>. Iteration order is
	 * {@link Collection} implementation specific.
	 *
	 * @param source the source Collection
	 * @param candidates the candidates to search for
	 * @return the first present object, or <code>null</code> if not found
	 */
	public static Object findFirstMatch(final Collection<?> source, final Collection<?> candidates) {
		if (isEmpty(source) || isEmpty(candidates)) {
			return null;
		}
		for (final Object candidate : candidates) {
			if (source.contains(candidate)) {
				return candidate;
			}
		}
		return null;
	}

	/**
	 * Find a single value of the given type in the given Collection.
	 *
	 * @param collection the Collection to search
	 * @param type the type to look for
	 * @return a value of the given type found if there is a clear match,
	 * or <code>null</code> if none or more than one such value found
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findValueOfType(final Collection<?> collection, final Class<T> type) {
		if (isEmpty(collection)) {
			return null;
		}
		T value = null;
		for (final Object element : collection) {
			if (type == null || type.isInstance(element)) {
				if (value != null) {
					// More than one value found... no clear single value.
					return null;
				}
				value = (T) element;
			}
		}
		return value;
	}

	/**
	 * Find a single value of one of the given types in the given Collection:
	 * searching the Collection for a value of the first type, then
	 * searching for a value of the second type, etc.
	 *
	 * @param collection the collection to search
	 * @param types the types to look for, in prioritized order
	 * @return a value of one of the given types found if there is a clear match,
	 * or <code>null</code> if none or more than one such value found
	 */
	public static Object findValueOfType(final Collection<?> collection, final Class<?>... types) {
		if (isEmpty(collection) || ObjectUtils.isEmpty(types)) {
			return null;
		}
		for (final Class<?> type : types) {
			final Object value = findValueOfType(collection, type);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Determine whether the given Collection only contains a single unique object.
	 *
	 * @param collection the Collection to check
	 * @return <code>true</code> if the collection contains a single reference or
	 * multiple references to the same instance, <code>false</code> else
	 */
	public static boolean hasUniqueObject(final Collection<?> collection) {
		if (isEmpty(collection)) {
			return false;
		}
		boolean hasCandidate = false;
		Object candidate = null;
		for (final Iterator<?> it = collection.iterator(); it.hasNext();) {
			final Object elem = it.next();
			if (!hasCandidate) {
				hasCandidate = true;
				candidate = elem;
			}
			else if (candidate != elem) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Filters (removes elements from) the given {@link Iterable} using the
	 * given filter.
	 *
	 * @param <T> the type of object being filtered
	 * @param unfiltered the iterable to filter; can be <code>null</code>
	 * @param filter the filter to apply; can be <code>null</code> for none
	 * @return a non-<code>null</code> list
	 */
	public static <T> List<T> filter(final Iterable<? extends T> unfiltered, final Filter<T> filter) {
		final List<T> filtered = new ArrayList<T>();
		if (unfiltered != null) {
			for (final T element : unfiltered) {
				if (filter == null || filter.include(element)) {
					filtered.add(element);
				}
			}
		}
		return filtered;
	}

	/**
	 * Adds the given items to the given collection
	 *
	 * @param <T> the type of item in the collection being updated
	 * @param newItems the items being added; can be <code>null</code> for none
	 * @param existingItems the items being added to; must be modifiable
	 * @return <code>true</code> if the existing collection was modified
	 * @throws UnsupportedOperationException if there are items to add and the
	 * existing collection is not modifiable
	 * @since 1.2.0
	 */
	public static <T> boolean addAll(final Collection<? extends T> newItems, final Collection<T> existingItems) {
		if (existingItems != null && newItems != null) {
			return existingItems.addAll(newItems);
		}
		return false;
	}

	/**
	 * Populates the given collection by replacing any existing contents with
	 * the given elements, in a null-safe way.
	 *
	 * @param <T> the type of element in the collection
	 * @param collection the collection to populate (can be <code>null</code>)
	 * @param items the items with which to populate the collection (can be
	 * <code>null</code> or empty for none)
	 * @return the given collection (useful if it was anonymous)
	 */
	public static <T> Collection<T> populate(final Collection<T> collection, final Collection<? extends T> items) {
		if (collection != null) {
			collection.clear();
			if (items != null) {
				collection.addAll(items);
			}
		}
		return collection;
	}

	/**
	 * Returns the first element of the given collection
	 *
	 * @param <T>
	 * @param collection
	 * @return <code>null</code> if the first element is <code>null</code> or
	 * the collection is <code>null</code> or empty
	 */
	public static <T> T firstElementOf(final Collection<? extends T> collection) {
		if (isEmpty(collection)) {
			return null;
		}
		return collection.iterator().next();
	}

	/**
	 * Constructor is private to prevent instantiation
	 *
	 * @since 1.2.0
	 */
	private CollectionUtils() {}
}