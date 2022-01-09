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
package org.springframework.shell.context;

/**
 * Enumeration for modes shell is operating.
 *
 * @author Janne Valkealahti
 */
public enum InteractionMode {

	/**
	 * All possible modes.
	 */
	ALL,

	/**
	 * Non-interactive mode which is expected to exit and doesn't have any kind of
	 * running mode to keep shell alive.
	 */
	NONINTERACTIVE,

	/**
	 * Interactive mode which is expected to not exit and do have a running mode to
	 * keep shell alive.
	 */
	INTERACTIVE
}
