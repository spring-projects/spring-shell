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
package org.springframework.shell.jline.tui.component.context;

import java.util.AbstractMap;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ComponentContextTests {

	@Test
	@SuppressWarnings("unused")
	void testBasics() {
		ComponentContext<?> context = ComponentContext.empty();
		assertThat(context.stream()).isEmpty();

		context.put("foo", "bar");

		assertThat(context.stream()).containsExactlyInAnyOrder(new AbstractMap.SimpleEntry<>("foo", "bar"));

		String foo = context.get("foo");
		assertThat(foo).isEqualTo("bar");
		assertThatThrownBy(() -> {
			Integer unused = context.get("foo");
		}).isInstanceOf(ClassCastException.class);

		assertThat(context.get("foo", String.class)).isEqualTo("bar");
		assertThatThrownBy(() -> context.get("foo", Integer.class)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void containsKeyIsTrueWhenValueIsStored() {
		ComponentContext<?> context = ComponentContext.empty();
		context.put("foo", "bar");

		assertThat(context.containsKey("foo")).isTrue();
		assertThat(context.<String>get("foo")).isEqualTo("bar");
	}

	@Test
	void containsKeyIsFalseWhenStoredValueIsNull() {
		ComponentContext<?> context = ComponentContext.empty();
		context.put("id", null);

		assertThat(context.containsKey("id")).isFalse();
		assertThatThrownBy(() -> context.get("id")).isInstanceOf(NoSuchElementException.class);
	}

}
