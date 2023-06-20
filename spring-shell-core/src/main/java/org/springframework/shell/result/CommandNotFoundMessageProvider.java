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
package org.springframework.shell.result;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.result.CommandNotFoundMessageProvider.ProviderContext;

/**
 * Provider for a message used within {@link CommandNotFoundResultHandler}.
 *
 * @author Janne Valkealahti
 */
@FunctionalInterface
public interface CommandNotFoundMessageProvider extends Function<ProviderContext, String> {

	static ProviderContext contextOf(Throwable error, List<String> commands, Map<String, CommandRegistration> registrations, String text) {
		return new ProviderContext() {

			@Override
			public Throwable error() {
				return error;
			}

			@Override
			public List<String> commands() {
				return commands;
			}

			@Override
			public Map<String, CommandRegistration> registrations() {
				return registrations;
			}

			@Override
			public String text() {
				return text;
			}
		};
	}

	/**
	 * Context for {@link CommandNotFoundResultHandler}.
	 */
	interface ProviderContext {

		/**
		 * Gets an actual error.
		 *
		 * @return actual error
		 */
		Throwable error();

		/**
		 * Gets a list of commands parsed.
		 *
		 * @return list of commands parsed
		 */
		List<String> commands();

		/**
		 * Gets a command registrations.
		 *
		 * @return a command registrations
		 */
		Map<String, CommandRegistration> registrations();

		/**
		 * Gets a raw input text.
		 *
		 * @return a raw input text
		 */
		String text();
	}

}
