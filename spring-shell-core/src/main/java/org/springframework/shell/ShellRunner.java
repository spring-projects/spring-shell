/*
 * Copyright 2021-2024 the original author or authors.
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
package org.springframework.shell;

import org.springframework.boot.ApplicationArguments;

/**
 * Interface for shell runners.
 *
 * @author Janne Valkealahti
 */
public interface ShellRunner {

	/**
	 * Checks if a particular shell runner can execute.
	 *
	 * For {@link #canRun(ApplicationArguments)} and
	 * {@link #run(ApplicationArguments)} prefer {@link #run(String[])}.
	 *
	 * @param args the application arguments
	 * @return true if shell runner can execute
	 */
	@Deprecated(since = "3.3.0", forRemoval = true)
	default boolean canRun(ApplicationArguments args) {
		return false;
	}

	/**
	 * Execute application.
	 *
	 * For {@link #canRun(ApplicationArguments)} and
	 * {@link #run(ApplicationArguments)} prefer {@link #run(String[])}.
	 *
	 * @param args the application argumets
	 * @throws Exception in errors
	 */
	@Deprecated(since = "3.3.0", forRemoval = true)
	default void run(ApplicationArguments args) throws Exception {
		throw new UnsupportedOperationException("Should get implemented together with canRun");
	}

	/**
	 * Execute {@code ShellRunner} with given args. Return value indicates if run
	 * operation happened and no further runners should be used.
	 *
	 * @param args the raw arguments
	 * @return true if run execution happened
	 * @throws Exception possible error during run
	 */
	default boolean run(String[] args) throws Exception {
		return false;
	}

}
