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

import java.util.List;

import org.springframework.shell.event.ParseResult;

/**
 * Interface for {@link SimpleParser}.
 *
 * @author Ben Alex
 * @author Alan Stewart
 * @since 1.0
 */
public interface Parser {

	ParseResult parse(String buffer);

	/**
	 * Populates a list of completion candidates. This method is required for backward compatibility for STS versions up to 2.8.0.
	 * 
	 * @param buffer
	 * @param cursor
	 * @param candidates
	 * @return
	 */
	int complete(String buffer, int cursor, List<String> candidates);

	/**
	 * Populates a list of completion candidates. 
	 * 
	 * @param buffer
	 * @param cursor
	 * @param candidates
	 * @return
	 */
	int completeAdvanced(String buffer, int cursor, List<Completion> candidates);
}