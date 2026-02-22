/*
 * Copyright 2025-present the original author or authors.
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

import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseComponentContextTests {

	@Test
	void putAndGetShouldWork() {
		// given
		BaseComponentContext<?> context = new BaseComponentContext<>();
		context.put("key", "value");

		// when
		Object value = context.get("key");

		// then
		assertEquals("value", value);
	}

	@Test
	void getMissingKeyShouldThrow() {
		// given
		BaseComponentContext<?> context = new BaseComponentContext<>();

		// when / then
		assertThrows(NoSuchElementException.class, () -> context.get("missing"));
	}

	@Test
	void getWithTypeShouldCast() {
		// given
		BaseComponentContext<?> context = new BaseComponentContext<>();
		context.put("count", 42);

		// when
		Integer value = context.get("count", Integer.class);

		// then
		assertEquals(42, value);
	}

	@Test
	void getWithWrongTypeShouldThrow() {
		// given
		BaseComponentContext<?> context = new BaseComponentContext<>();
		context.put("count", 42);

		// when / then
		assertThrows(IllegalArgumentException.class, () -> context.get("count", String.class));
	}

	@Test
	void containsKeyShouldWork() {
		// given
		BaseComponentContext<?> context = new BaseComponentContext<>();
		context.put("key", "value");

		// then
		assertTrue(context.containsKey("key"));
		assertFalse(context.containsKey("other"));
	}

	@Test
	void streamShouldReturnEntries() {
		// given
		BaseComponentContext<?> context = new BaseComponentContext<>();
		context.put("a", 1);
		context.put("b", 2);

		// when
		long count = context.stream().count();

		// then
		assertEquals(2, count);
	}

	@Test
	void terminalWidthShouldBeNullByDefault() {
		// given
		BaseComponentContext<?> context = new BaseComponentContext<>();

		// then
		assertNull(context.getTerminalWidth());
	}

	@Test
	void setTerminalWidthShouldWork() {
		// given
		BaseComponentContext<?> context = new BaseComponentContext<>();

		// when
		context.setTerminalWidth(80);

		// then
		assertEquals(80, context.getTerminalWidth());
	}

	@Test
	void toTemplateModelShouldContainExpectedKeys() {
		// given
		BaseComponentContext<?> context = new BaseComponentContext<>();
		context.put("key1", "value1");
		context.setTerminalWidth(120);

		// when
		Map<String, ?> model = context.toTemplateModel();

		// then
		assertNotNull(model.get("rawValues"));
		assertEquals(120, model.get("terminalWidth"));
	}

	@Test
	void emptyFactoryMethodShouldReturnEmptyContext() {
		// when
		ComponentContext<?> context = ComponentContext.empty();

		// then
		assertNotNull(context);
		assertNull(context.getTerminalWidth());
	}

}
