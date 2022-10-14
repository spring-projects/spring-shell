/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.component.context;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * Base implementation of a {@link ComponentContext}.
 *
 * @author Janne Valkealahti
 */
@SuppressWarnings("unchecked")
public class BaseComponentContext<C extends ComponentContext<C>> extends LinkedHashMap<Object, Object>
		implements ComponentContext<C> {

	private Integer terminalWidth;

	@Override
	public Object get(Object key) {
		Object o = super.get(key);
		if (o != null) {
			return o;
		}
		throw new NoSuchElementException("Context does not contain key: " + key);
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		Object value = get(key);
		if (!type.isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException("Incorrect type specified for key '" +
					key + "'. Expected [" + type + "] but actual type is [" + value.getClass() + "]");
		}
		return (T) value;
	}

	@Override
	public ComponentContext<C> put(Object key, Object value) {
		super.put(key, value);
		return this;
	}

	@Override
	public Stream<java.util.Map.Entry<Object, Object>> stream() {
		return entrySet().stream();
	}

	@Override
	public Integer getTerminalWidth() {
		return terminalWidth;
	}

	@Override
	public void setTerminalWidth(Integer terminalWidth) {
		this.terminalWidth = terminalWidth;
	}

	@Override
	public Map<String, Object> toTemplateModel() {
		Map<String, Object> attributes = new HashMap<>();
		// hardcoding enclosed map values into 'rawValues'
		// as it may contain anything
		attributes.put("rawValues", this);
		attributes.put("terminalWidth", terminalWidth);
		return attributes;
	}

	@Override
	public String toString() {
		return "BaseComponentContext [terminalWidth=" + terminalWidth + "]";
	}
}
