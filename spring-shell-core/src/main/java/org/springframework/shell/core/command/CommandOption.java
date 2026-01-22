/*
 * Copyright 2022-present the original author or authors.
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

import org.jspecify.annotations.Nullable;

/**
 * Record representing the definition as well as the runtime information about a command
 * option.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public record CommandOption(char shortName, @Nullable String longName, @Nullable String description,
		@Nullable Boolean required, @Nullable String defaultValue, @Nullable String value, Class<?> type) {

	public static Builder with() {
		return new Builder();
	}

	public static class Builder {

		private char shortName = ' ';

		private @Nullable String longName;

		private @Nullable String description;

		private @Nullable Boolean required;

		private @Nullable String defaultValue;

		private @Nullable String value;

		private Class<?> type = Object.class;

		public Builder shortName(char shortName) {
			this.shortName = shortName;
			return this;
		}

		public Builder longName(String longName) {
			this.longName = longName;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder required(Boolean required) {
			this.required = required;
			return this;
		}

		public Builder defaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Builder value(String value) {
			this.value = value;
			return this;
		}

		public Builder type(Class<?> type) {
			this.type = type;
			return this;
		}

		public CommandOption build() {
			return new CommandOption(shortName, longName, description, required, defaultValue, value, type);
		}

	}
}
