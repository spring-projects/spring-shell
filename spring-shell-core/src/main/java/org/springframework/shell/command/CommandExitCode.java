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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Interface representing an exit code in a command.
 *
 * @author Janne Valkealahti
 */
public interface CommandExitCode {

	/**
	 * Gets a function mappings from exceptions to exit codes.
	 *
	 * @return function mappings
	 */
	List<Function<Throwable, Integer>> getMappingFunctions();

	/**
	 * Gets an instance of a default {@link CommandExitCode}.
	 *
	 * @return a command exit code
	 */
	public static CommandExitCode of() {
		return of(new ArrayList<>());
	}

	/**
	 * Gets an instance of a default {@link CommandExitCode}.
	 *
	 * @param functions the function mappings
	 * @return a command exit code
	 */
	public static CommandExitCode of(List<Function<Throwable, Integer>> functions) {
		return new DefaultCommandExitCode(functions);
	}

	static class DefaultCommandExitCode implements CommandExitCode {

		private final List<Function<Throwable, Integer>> functions;

		DefaultCommandExitCode(	List<Function<Throwable, Integer>> functions) {
			this.functions = functions;
		}

		@Override
		public List<Function<Throwable, Integer>> getMappingFunctions() {
			return functions;
		}
	}
}
