/*
 * Copyright 2025-present the original author or authors.
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

package org.springframework.shell.core.command;

/**
 * Record representing the exit status of a command.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public record ExitStatus(int code, String description) {

	public static ExitStatus OK = new ExitStatus(0, "OK");

	public static ExitStatus EXECUTION_ERROR = new ExitStatus(-1, "EXECUTION_ERROR");

	public static ExitStatus USAGE_ERROR = new ExitStatus(-2, "USAGE_ERROR");

}