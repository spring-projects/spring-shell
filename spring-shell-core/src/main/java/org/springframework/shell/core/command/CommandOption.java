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
package org.springframework.shell.core.command;

import org.jspecify.annotations.Nullable;
import org.springframework.core.ResolvableType;
import org.springframework.shell.core.completion.CompletionResolver;

/**
 * Interface representing an option in a command.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
// TODO this is better defined as a record.
public interface CommandOption {

	/**
	 * Gets a long name of an option.
	 * @return long name of an option
	 */
	String getLongName();

	/**
	 * Gets a modified long names of an option. Set within a command registration if
	 * option name modifier were used to have an info about original names.
	 * @return modified long names of an option
	 */
	String getLongNameModified();

	/**
	 * Gets a short names of an option.
	 * @return short names of an option
	 */
	Character getShortName();

	/**
	 * Gets a description of an option.
	 * @return description of an option
	 */
	@Nullable String getDescription();

	/**
	 * Gets a {@link ResolvableType} of an option.
	 * @return type of an option
	 */
	@Nullable ResolvableType getType();

	/**
	 * Gets a flag if option is required.
	 * @return the required flag
	 */
	boolean isRequired();

	/**
	 * Gets a default value of an option.
	 * @return the default value
	 */
	@Nullable String getDefaultValue();

	/**
	 * Gets a positional value.
	 * @return the positional value
	 */
	int getPosition();

	/**
	 * Gets a minimum arity.
	 * @return the minimum arity
	 */
	int getArityMin();

	/**
	 * Gets a maximum arity.
	 * @return the maximum arity
	 */
	int getArityMax();

	/**
	 * Gets a completion function.
	 * @return the completion function
	 */
	@Nullable CompletionResolver getCompletion();

	/**
	 * Gets an instance of a default {@link CommandOption}.
	 * @param longName the long name
	 * @param longNameModified the modified long name
	 * @param shortName the short name
	 * @param description the description
	 * @param type the type
	 * @param required the required flag
	 * @param defaultValue the default value
	 * @param position the position value
	 * @param arityMin the min arity
	 * @param arityMax the max arity
	 * @param label the label
	 * @param completion the completion
	 * @return default command option
	 */
	static CommandOption of(String longName, String longNameModified, Character shortName, String description,
			ResolvableType type, boolean required, String defaultValue, Integer position, Integer arityMin,
			Integer arityMax, String label, CompletionResolver completion) {
		return new DefaultCommandOption(longName, longNameModified, shortName, description, type, required,
				defaultValue, position, arityMin, arityMax, label, completion);
	}

	/**
	 * Default implementation of {@link CommandOption}.
	 */
	class DefaultCommandOption implements CommandOption {

		private String longName;

		private String longNameModified;

		private Character shortName;

		private String description;

		private ResolvableType type;

		private boolean required;

		private String defaultValue;

		private int position;

		private int arityMin;

		private int arityMax;

		private CompletionResolver completion;

		public DefaultCommandOption(String longName, String longNameModified, Character shortName, String description,
				ResolvableType type, boolean required, String defaultValue, Integer position, Integer arityMin,
				Integer arityMax, String label, CompletionResolver completion) {
			this.longName = longName;
			this.longNameModified = longNameModified;
			this.shortName = shortName;
			this.description = description;
			this.type = type;
			this.required = required;
			this.defaultValue = defaultValue;
			this.position = position != null && position > -1 ? position : -1;
			this.arityMin = arityMin != null ? arityMin : -1;
			this.arityMax = arityMax != null ? arityMax : -1;
			this.completion = completion;
		}

		@Override
		public String getLongName() {
			return longName;
		}

		@Override
		public String getLongNameModified() {
			return longNameModified;
		}

		@Override
		public Character getShortName() {
			return shortName;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public ResolvableType getType() {
			return type;
		}

		@Override
		public boolean isRequired() {
			return required;
		}

		@Override
		public String getDefaultValue() {
			return defaultValue;
		}

		@Override
		public int getPosition() {
			return position;
		}

		@Override
		public int getArityMin() {
			return arityMin;
		}

		@Override
		public int getArityMax() {
			return arityMax;
		}

		@Override
		public CompletionResolver getCompletion() {
			return completion;
		}

	}

}
