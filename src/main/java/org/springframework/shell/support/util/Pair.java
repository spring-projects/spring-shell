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

/**
 * A pair with a key of type "K" and a value of type "V". Instances are immutable.
 *
 * @author Andrew Swan
 * @since 1.2.0
 * @param <K> the key type
 * @param <V> the value type
 */
public class Pair<K, V> {

	// Fields
	private final K key;
	private final V value;

	/**
	 * Constructor
	 *
	 * @param key can be <code>null</code>
	 * @param value can be <code>null</code>
	 */
	public Pair(final K key, final V value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Returns the key
	 *
	 * @return <code>null</code> if it is
	 */
	public K getKey() {
		return key;
	}

	/**
	 * Returns the value
	 *
	 * @return <code>null</code> if it is
	 */
	public V getValue() {
		return value;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Pair)) {
			return false;
		}
		final Pair<?, ?> otherPair = (Pair<?, ?>) obj;
		return ObjectUtils.nullSafeEquals(key, otherPair.getKey()) && ObjectUtils.nullSafeEquals(value, otherPair.getValue());
	}

	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(new Object[] {getKey(), getValue()});
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("key: ").append(key);
		sb.append(", value: ").append(value);
		return sb.toString();
	}
}
