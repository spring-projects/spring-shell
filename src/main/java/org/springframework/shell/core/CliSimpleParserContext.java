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
 * Utility methods relating to shell simple parser contexts.
 */
public final class CliSimpleParserContext {

	// Class fields
	private static ThreadLocal<SimpleParser> simpleParserContextHolder = new ThreadLocal<SimpleParser>();

	public static Parser getSimpleParserContext() {
		return simpleParserContextHolder.get();
	}

	public static void setSimpleParserContext(final SimpleParser simpleParserContext) {
		simpleParserContextHolder.set(simpleParserContext);
	}

	public static void resetSimpleParserContext() {
		simpleParserContextHolder.remove();
	}

	/**
	 * Constructor is private to prevent instantiation
	 */
	private CliSimpleParserContext() {}
}
