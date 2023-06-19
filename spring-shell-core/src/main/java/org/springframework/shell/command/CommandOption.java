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

import org.springframework.core.ResolvableType;
import org.springframework.shell.completion.CompletionResolver;

/**
 * Interface representing an option in a command.
 *
 * @author Janne Valkealahti
 */
public interface CommandOption {

	/**
	 * Gets a long names of an option.
	 *
	 * @return long names of an option
	 */
	String[] getLongNames();

	/**
	 * Gets a modified long names of an option. Set within a command registration if
	 * option name modifier were used to have an info about original names.
	 *
	 * @return modified long names of an option
	 */
	String[] getLongNamesModified();

	/**
	 * Gets a short names of an option.
	 *
	 * @return short names of an option
	 */
	Character[] getShortNames();

	/**
	 * Gets a description of an option.
	 *
	 * @return description of an option
	 */
	String getDescription();

	/**
	 * Gets a {@link ResolvableType} of an option.
	 *
	 * @return type of an option
	 */
	ResolvableType getType();

	/**
	 * Gets a flag if option is required.
	 *
	 * @return the required flag
	 */
	boolean isRequired();

	/**
	 * Gets a default value of an option.
	 *
	 * @return the default value
	 */
	String getDefaultValue();

	/**
	 * Gets a positional value.
	 *
	 * @return the positional value
	 */
	int getPosition();

	/**
	 * Gets a minimum arity.
	 *
	 * @return the minimum arity
	 */
	int getArityMin();

	/**
	 * Gets a maximum arity.
	 *
	 * @return the maximum arity
	 */
	int getArityMax();

	/**
	 * Gets a label.
	 *
	 * @return the label
	 */
	String getLabel();

	/**
	 * Gets a completion function.
	 *
	 * @return the completion function
	 */
	CompletionResolver getCompletion();

	/**
	 * Gets an instance of a default {@link CommandOption}.
	 *
	 * @param longNames the long names
	 * @param shortNames the short names
	 * @param description the description
	 * @return default command option
	 */
	public static CommandOption of(String[] longNames, Character[] shortNames, String description) {
		return of(longNames, null, shortNames, description, null, false, null, null, null, null, null, null);
	}

	/**
	 * Gets an instance of a default {@link CommandOption}.
	 *
	 * @param longNames the long names
	 * @param shortNames the short names
	 * @param description the description
	 * @param type the type
	 * @return default command option
	 */
	public static CommandOption of(String[] longNames, Character[] shortNames, String description,
			ResolvableType type) {
		return of(longNames, null, shortNames, description, type, false, null, null, null, null, null, null);
	}

	/**
	 * Gets an instance of a default {@link CommandOption}.
	 *
	 * @param longNames the long names
	 * @param longNamesModified the modified long names
	 * @param shortNames the short names
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
	public static CommandOption of(String[] longNames, String[] longNamesModified, Character[] shortNames, String description,
			ResolvableType type, boolean required, String defaultValue, Integer position, Integer arityMin,
			Integer arityMax, String label, CompletionResolver completion) {
		return new DefaultCommandOption(longNames, longNamesModified, shortNames, description, type, required, defaultValue, position,
				arityMin, arityMax, label, completion);
	}

	/**
	 * Default implementation of {@link CommandOption}.
	 */
	public static class DefaultCommandOption implements CommandOption {

		private String[] longNames;
		private String[] longNamesModified;
		private Character[] shortNames;
		private String description;
		private ResolvableType type;
		private boolean required;
		private String defaultValue;
		private int position;
		private int arityMin;
		private int arityMax;
		private String label;
		private CompletionResolver completion;

		public DefaultCommandOption(String[] longNames, String[] longNamesModified, Character[] shortNames, String description,
				ResolvableType type, boolean required, String defaultValue, Integer position,
				Integer arityMin, Integer arityMax, String label,
				CompletionResolver completion) {
			this.longNames = longNames != null ? longNames : new String[0];
			this.longNamesModified = longNamesModified != null ? longNamesModified : new String[0];
			this.shortNames = shortNames != null ? shortNames : new Character[0];
			this.description = description;
			this.type = type;
			this.required = required;
			this.defaultValue = defaultValue;
			this.position = position != null && position > -1 ? position : -1 ;
			this.arityMin = arityMin != null ? arityMin : -1;
			this.arityMax = arityMax != null ? arityMax : -1;
			this.label = label;
			this.completion = completion;
		}

		@Override
		public String[] getLongNames() {
			return longNames;
		}

		@Override
		public String[] getLongNamesModified() {
			return longNamesModified;
		}

		@Override
		public Character[] getShortNames() {
			return shortNames;
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
		public String getLabel() {
			return label;
		}

		@Override
		public CompletionResolver getCompletion() {
			return completion;
		}
	}
}
