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
package org.springframework.shell.command.parser;

public class ParserConfig {

	private long features;

	public ParserConfig() {
		this.features = Feature.collectDefaults();
	}

	boolean isEnabled(Feature feature) {
		return (features & feature.getMask()) != 0;
	}

	ParserConfig configure(Feature feature, boolean state) {
		return state ? enable(feature) : disable(feature);
	}

	ParserConfig enable(Feature feature) {
		features |= feature.getMask();
		return this;
	}

	ParserConfig disable(Feature feature) {
		features &= ~feature.getMask();
		return this;
	}

	public static enum Feature {

		/**
		 * Defines if directives support is enabled, disabled on default.
		 */
		ALLOW_DIRECTIVES(false),

		/**
		 * Used in a case where directive support is disabled and parser should ignore
		 * ones found instead of reporting error, disabled on default.
		 */
		IGNORE_DIRECTIVES(false),

		/**
		 * Defines if commands are parsed using case-sensitivity, enabled on default.
		 */
		CASE_SENSITIVE_COMMANDS(true),

		/**
		 * Defines if options are parsed using case-sensitivity, enabled on default.
		 */
		CASE_SENSITIVE_OPTIONS(true)
		;

		private final boolean defaultState;
		private final long mask;

		private Feature(boolean defaultState) {
			this.mask = (1 << ordinal());
			this.defaultState = defaultState;
		}

		public static long collectDefaults() {
			long flags = 0;
			for (Feature f : values()) {
				if (f.enabledByDefault()) {
					flags |= f.getMask();
				}
			}
			return flags;
		}

		public boolean enabledByDefault() {
			return defaultState;
		}

		public boolean enabledIn(int flags) {
			return (flags & mask) != 0;
		}

		public long getMask() {
			return mask;
		}
	}
}
