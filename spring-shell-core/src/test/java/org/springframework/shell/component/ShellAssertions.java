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
package org.springframework.shell.component;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.assertj.core.api.AbstractAssert;

/**
 * Custom assertj assertions.
 *
 * @author Janne Valkealahti
 */
public class ShellAssertions {

	public static StringOrderAssert assertStringOrderThat(String actual) {
		return new StringOrderAssert(actual);
	}

	public static class StringOrderAssert extends AbstractAssert<StringOrderAssert, String> {

		protected StringOrderAssert(String out) {
			super(out, StringOrderAssert.class);
		}

		public StringOrderAssert containsInOrder(String... expected) {
			isNotNull();
			int[] indexes = new int[expected.length];
			for (int i = 0; i < expected.length; i++) {
				indexes[i] = actual.indexOf(expected[i]);
			}
			for (int i = 0; i < expected.length; i++) {
				if (indexes[i] < 0) {
					failWithMessage("Item [%s] not found from output [%s]", expected[i], actual);
				}
			}
			if (!isSorted(indexes)) {
				String expectedStr = Stream.of(expected).collect(Collectors.joining(","));
				String indexStr = IntStream.of(indexes).mapToObj(i -> ((Integer) i).toString())
						.collect(Collectors.joining(","));
				failWithMessage("Items [%s] are in wrong order, indexes are [%s], output is [%s]", expectedStr,
						indexStr, actual);
			}
			return this;
		}

		private boolean isSorted(int[] array) {
			for (int i = 0; i < array.length - 1; i++) {
				if (array[i] > array[i + 1])
					return false;
			}
			return true;
		}
	}
}
