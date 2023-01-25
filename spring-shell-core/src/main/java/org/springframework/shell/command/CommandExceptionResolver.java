/*
 * Copyright 2022-2023 the original author or authors.
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
package org.springframework.shell.command;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Interface to be implemented by objects that can resolve exceptions thrown
 * during command processing, in the typical case error response. Implementors
 * are typically registered as beans in the application context or directly
 * with command.
 *
 * @author Janne Valkealahti
 */
public interface CommandExceptionResolver {

	/**
	 * Default precedence related use of {@link Ordered} and {@link Order}.
	 */
	int DEFAULT_PRECEDENCE = -100;

	/**
	 * Try to resolve the given exception that got thrown during command processing.
	 *
	 * @param ex the exception
	 * @return a corresponding {@code HandlingResult} framework to handle, or
	 *         {@code null} for default processing in the resolution chain
	 */
	CommandHandlingResult resolve(Exception ex);
}
