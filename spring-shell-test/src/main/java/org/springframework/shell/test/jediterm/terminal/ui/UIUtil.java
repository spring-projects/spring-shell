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
package org.springframework.shell.test.jediterm.terminal.ui;

/**
 * @author jediterm authors
 */
public class UIUtil {
	public static final String OS_NAME = System.getProperty("os.name");
	public static final String OS_VERSION = System.getProperty("os.version").toLowerCase();

	protected static final String _OS_NAME = OS_NAME.toLowerCase();
	public static final boolean isWindows = _OS_NAME.startsWith("windows");
	public static final boolean isOS2 = _OS_NAME.startsWith("os/2") || _OS_NAME.startsWith("os2");
	public static final boolean isMac = _OS_NAME.startsWith("mac");
	public static final boolean isLinux = _OS_NAME.startsWith("linux");
	public static final boolean isUnix = !isWindows && !isOS2;

	public static final String JAVA_RUNTIME_VERSION = System.getProperty("java.runtime.version");
}
