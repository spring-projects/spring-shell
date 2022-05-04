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
package org.springframework.shell.command;

import java.util.List;

/**
 * Interface to resolve currently existing commands. It is useful to have fully
 * dynamic set of commands which may exists only if some conditions in a running
 * shell are met. For example if shell is targeting arbitrary server environment
 * some commands may or may not exist depending on a runtime state.
 *
 * @author Janne Valkealahti
 */
@FunctionalInterface
public interface CommandResolver {

	/**
	 * Resolve command registrations.
	 *
	 * @return command registrations
	 */
	List<CommandRegistration> resolve();
}
