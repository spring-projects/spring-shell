/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.test.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for shell test.
 *
 * @author Janne Valkealahti
 */
@ConfigurationProperties(prefix = "spring.shell.test")
public class SpringShellTestProperties {

	/**
	 * Width of an emulated terminal.
	 */
	private int terminalWidth = 80;

	/**
	 * Height of an emulated terminal.
	 */
	private int terminalHeight = 24;

	public int getTerminalWidth() {
		return terminalWidth;
	}

	public void setTerminalWidth(int terminalWidth) {
		this.terminalWidth = terminalWidth;
	}

	public int getTerminalHeight() {
		return terminalHeight;
	}

	public void setTerminalHeight(int terminalHeight) {
		this.terminalHeight = terminalHeight;
	}
}
