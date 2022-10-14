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

import java.util.Map;
import java.util.stream.Stream;

/**
 * Contract for base component context having access to basic key/value pairs.
 * This is a base context which components can extend to provide their own
 * component spesific contexts.
 *
 * @author Janne Valkealahti
 */
public interface ComponentContext<C extends ComponentContext<C>> {

	/**
	 * Gets an empty context.
	 *
	 * @param <C> the type of context
	 * @return empty context
	 */
	static <C extends ComponentContext<C>> ComponentContext<C> empty() {
		return new BaseComponentContext<>();
	}

	/**
	 * Gets a value from a context.
	 *
	 * @param <T> the type of context
	 * @param key the key
	 * @return a value
	 */
	<T> T get(Object key);

	/**
	 * Gets a value from a context with a given type to get cast to.
	 *
	 * @param <T> the type of context
	 * @param key the key
	 * @param type the class type
	 * @return a value
	 */
	<T> T get(Object key, Class<T> type);

	/**
	 * Check if a context contains a key.
	 *
	 * @param key the key
	 * @return true if context contains key
	 */
	boolean containsKey(Object key);

	/**
	 * Put an entry into a context.
	 *
	 * @param key the entry key
	 * @param value the entry value
	 * @return a context
	 */
	ComponentContext<C> put(Object key, Object value);

	/**
	 * Stream key/value pairs from this {@link ComponentContext}
	 *
	 * @return a {@link Stream} of key/value pairs held by this context
	 */
	Stream<Map.Entry<Object, Object>> stream();

	/**
	 * Get terminal width.
	 *
	 * @return a terminal width
	 */
	Integer getTerminalWidth();

	/**
	 * Set terminal width.
	 *
	 * @param terminalWidth the width
	 */
	void setTerminalWidth(Integer terminalWidth);

	/**
	 * Gets context values as a map. Every context implementation can
	 * do their own model as essentially what matter is a one coming
	 * out from a last child which is one most likely to feed into
	 * a template engine.
	 *
	 * @return map of context values
	 */
	Map<String, Object> toTemplateModel();
}
