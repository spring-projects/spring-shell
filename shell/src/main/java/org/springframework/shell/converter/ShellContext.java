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
package org.springframework.shell.converter;

import java.util.Map;


/**
 * Shell context.
 *
 * @author David Winterfeldt
 */
public class ShellContext {

	private final Map<String, String> options;
	private final String optionContext;
	
	public ShellContext(Map<String, String> options, String optionContext) {
		this.options = options;
		this.optionContext = optionContext;
	}
	
	/**
	 * Gets options.
	 */
	public Map<String, String> getOptions() {
		return options;
	}

	/**
	 * Gets option context (current option).
	 */
	public String getOptionContext() {
		return optionContext;
	}
	
}
