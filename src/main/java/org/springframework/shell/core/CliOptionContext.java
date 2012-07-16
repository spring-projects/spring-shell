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
package org.springframework.shell.core;

/**
 * Utility methods relating to shell option contexts
 */
public final class CliOptionContext {

	// Class fields
	private static ThreadLocal<String> optionContextHolder = new ThreadLocal<String>();

	/**
	 * Returns the option context for the current thread.
	 *
	 * @return <code>null</code> if none has been set
	 */
	public static String getOptionContext() {
		return optionContextHolder.get();
	}

	/**
	 * Stores the given option context for the current thread.
	 *
	 * @param optionContext the option context to store
	 */
	public static void setOptionContext(final String optionContext) {
		optionContextHolder.set(optionContext);
	}

	/**
	 * Resets the option context for the current thread.
	 */
	public static void resetOptionContext() {
		optionContextHolder.remove();
	}

	/**
	 * Constructor is private to prevent instantiation
	 */
	private CliOptionContext() {}
}
