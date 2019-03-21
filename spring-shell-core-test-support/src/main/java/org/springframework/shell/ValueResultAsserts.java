/*
 * Copyright 2017 the original author or authors.
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

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

/**
 * Assertions for {@link ValueResult}.
 * 
 * @author Camilo Gonzalez
 */
public class ValueResultAsserts extends AbstractAssert<ValueResultAsserts, ValueResult> {

	public ValueResultAsserts(ValueResult actual) {
		super(actual, ValueResultAsserts.class);
	}

	public ValueResultAsserts hasValue(Object expectedValue) {
		isNotNull();
		Assertions.assertThat(actual.resolvedValue()).isEqualTo(expectedValue);
		return this;
	}

	public ValueResultAsserts usesWords(int... expectedWordsUsed) {
		isNotNull();

		Assertions.assertThat(actual.wordsUsed().stream().toArray()).containsExactly(expectedWordsUsed);

		return this;
	}

	public ValueResultAsserts notUsesWords() {
		isNotNull();
		Assertions.assertThat(actual.wordsUsed().isEmpty());
		return this;
	}

	public ValueResultAsserts usesWordsForValue(int... expectedWordsUsedForValue) {
		isNotNull();

		Assertions.assertThat(actual.wordsUsedForValue().stream().toArray()).containsExactly(expectedWordsUsedForValue);

		return this;
	}

	public ValueResultAsserts notUsesWordsForValue() {
		isNotNull();
		Assertions.assertThat(actual.wordsUsedForValue().isEmpty());
		return this;
	}

	public static ValueResultAsserts assertThat(ValueResult valueResult) {
		return new ValueResultAsserts(valueResult);
	}
}
