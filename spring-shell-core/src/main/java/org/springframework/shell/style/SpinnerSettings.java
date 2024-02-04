/*
 * Copyright 2024 the original author or authors.
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
package org.springframework.shell.style;

import org.springframework.shell.component.view.control.Spinner;

/**
 * Base class defining a settings for spinners.
 *
 * @author Janne Valkealahti
 */
public abstract class SpinnerSettings {

	/**
	 * Spinner with line characters.
	 */
	public final static String TAG_LINE = "line";

	/**
	 * Spinner with dot characters.
	 */
	public final static String TAG_DOT = "dot";

	public Spinner line() {
		return Spinner.of(Spinner.LINE1, 130);
	}

	public Spinner dot() {
		return Spinner.of(Spinner.DOTS1, 80);
	}

	public Spinner resolveTag(String tag) {
		switch (tag) {
			case TAG_LINE:
				return line();
			case TAG_DOT:
				return dot();
		}
		throw new IllegalArgumentException(String.format("Unknown tag '%s'", tag));
	}

	public static String[] tags() {
		return new String[] {
				TAG_LINE,
				TAG_DOT,
		};
	}

	public static SpinnerSettings defaults() {
		return new DefaultSpinnerSettings();
	}

	public static SpinnerSettings dump() {
		return new DumpSpinnerSettings();
	}

	private static class DefaultSpinnerSettings extends SpinnerSettings {
	}

	private static class DumpSpinnerSettings extends SpinnerSettings {

		@Override
		public Spinner dot() {
			return Spinner.of(Spinner.DOTS14, 200);
		}
	}

}
