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