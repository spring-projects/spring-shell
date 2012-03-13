package org.springframework.roo.support.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link List} of {@link Pair}s. Unlike a {@link java.util.Map}, it can have
 * duplicate and/or <code>null</code> keys.
 *
 * @author Andrew Swan
 * @since 1.2.0
 * @param <K> the type of key
 * @param <V> the type of value
 */
public class PairList<K, V> extends ArrayList<Pair<K, V>> {

	// For serialisation
	private static final long serialVersionUID = 5990417235907246300L;

	/**
	 * Returns the given array of pairs as a modifiable list
	 *
	 * @param <K> the type of key
	 * @param <V> the type of value
	 * @param pairs the pairs to put in a list
	 * @return a non-<code>null</code> list
	 */
	public PairList(final Pair<K, V>... pairs) {
		addAll(Arrays.asList(pairs));
	}

	/**
	 * Constructor for building a list of the given key-value pairs
	 *
	 * @param keys the keys (can be null)
	 * @param values the values (must be null if the keys are null, otherwise
	 * must be non-null and of the same size as the keys)
	 */
	public PairList(final List<? extends K> keys, final List<? extends V> values) {
		Assert.isTrue(!(keys == null ^ values == null), "Parameter types and names must either both be null or both be not null");
		if (keys == null) {
			Assert.isTrue(values == null, "Parameter names must be null if types are null");
		}
		else {
			Assert.isTrue(values != null, "Parameter names are required if types are provided");
			Assert.isTrue(keys.size() == values.size(), "Expected " + keys.size() + " values but found " + values.size());
			for (int i = 0; i < keys.size(); i++) {
				add(keys.get(i), values.get(i));
			}
		}
	}

	/**
	 * Constructor for an empty list of pairs
	 */
	public PairList() {
		// Empty
	}

	/**
	 * Returns the keys of each {@link Pair} in this list
	 *
	 * @return a non-<code>null</code> list
	 */
	public List<K> getKeys() {
		final List<K> keys = new ArrayList<K>();
		for (final Pair<K, ?> pair : this) {
			keys.add(pair.getKey());
		}
		return keys;
	}

	/**
	 * Returns the values of each {@link Pair} in this list
	 *
	 * @return a non-<code>null</code> modifiable copy of this list
	 */
	public List<V> getValues() {
		final List<V> values = new ArrayList<V>();
		for (final Pair<?, V> pair : this) {
			values.add(pair.getValue());
		}
		return values;
	}

	/**
	 * Adds a new pair to this list with the given key and value
	 *
	 * @param key the key to add; can be <code>null</code>
	 * @param value the value to add; can be <code>null</code>
	 * @return true (as specified by Collection.add(E))
	 */
	public boolean add(final K key, final V value) {
		return add(new Pair<K, V>(key, value));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<K, V>[] toArray() {
		return super.toArray(new Pair[size()]);
	}
}
