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
package org.springframework.shell.test;

import java.util.List;

/**
 * Interface representing a shell screen.
 *
 * @author Janne Valkealahti
 */
public interface ShellScreen {

	/**
	 * Gets a visible lines in a screen.
	 *
	 * @return visible lines in a screen
	 */
    List<String> lines();

	/**
	 * Get {@code ShellScreen} out of lines.
	 *
	 * @param lines the lines
	 * @return instance of shell screen
	 */
	static ShellScreen of(List<String> lines) {
		return new DefaultShellScreen(lines);
	}

	class DefaultShellScreen implements ShellScreen {

    	List<String> lines;

    	DefaultShellScreen(List<String> lines) {
    		this.lines = lines;
    	}

    	@Override
    	public List<String> lines() {
    		return lines;
    	}
    }

}