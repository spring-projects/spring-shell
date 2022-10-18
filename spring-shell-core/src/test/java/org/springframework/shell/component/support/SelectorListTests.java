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
package org.springframework.shell.component.support;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SelectorListTests {

	@Test
	void hasCorrectInitialStateWhenResetZero() {
		SelectorList<TestItem> list = SelectorList.of(5);
		list.reset(Collections.emptyList());
		assertThat(list).asInstanceOf(SELECTOR_LIST).hasProjectionSize(0);
	}

	@Test
	void hasCorrectInitialState() {
		SelectorList<TestItem> list = SelectorList.of(5);
		list.reset(items(5));

		assertThat(list.getSelected().getName()).isEqualTo("name0");
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.hasProjectionSize(5)
			.namesContainsExactly("name0", "name1", "name2", "name3", "name4")
			.selectedContainsExactly(true, false, false, false, false);
	}

	@Test
	void scrollDownLessItemsThanMax() {
		SelectorList<TestItem> list = SelectorList.of(3);
		list.reset(items(3));
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(true, false, false);

		list.scrollDown();
		assertThat(list.getSelected().getName()).isEqualTo("name1");
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(false, true, false);

		list.scrollDown();
		assertThat(list.getSelected().getName()).isEqualTo("name2");
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(false, false, true);

		list.scrollDown();
		assertThat(list.getSelected().getName()).isEqualTo("name0");
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(true, false, false);

		list.scrollDown();
		assertThat(list.getSelected().getName()).isEqualTo("name1");
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(false, true, false);

		list.scrollDown();
		assertThat(list.getSelected().getName()).isEqualTo("name2");
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(false, false, true);
	}

	@Test
	void scrollUpLessItemsThanMax() {
		SelectorList<TestItem> list = SelectorList.of(3);
		list.reset(items(3));
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(true, false, false);

		list.scrollUp();
		assertThat(list.getSelected().getName()).isEqualTo("name2");
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(false, false, true);

		list.scrollUp();
		assertThat(list.getSelected().getName()).isEqualTo("name1");
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(false, true, false);

		list.scrollUp();
		assertThat(list.getSelected().getName()).isEqualTo("name0");
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(true, false, false);

		list.scrollUp();
		assertThat(list.getSelected().getName()).isEqualTo("name2");
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(false, false, true);

		list.scrollUp();
		assertThat(list.getSelected().getName()).isEqualTo("name1");
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(false, true, false);
	}

	@Test
	void scrollDownMoreItemsThanMax() {
		SelectorList<TestItem> list = SelectorList.of(3);
		list.reset(items(5));
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(true, false, false);

		list.scrollDown();
		list.scrollDown();
		list.scrollDown();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name1", "name2", "name3")
			.selectedContainsExactly(false, false, true);

		list.scrollDown();
		list.scrollDown();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(true, false, false);
	}

	@Test
	void scrollUpMoreItemsThanMax() {
		SelectorList<TestItem> list = SelectorList.of(3);
		list.reset(items(5));
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(true, false, false);

		list.scrollUp();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name2", "name3", "name4")
			.selectedContainsExactly(false, false, true);

		list.scrollUp();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name2", "name3", "name4")
			.selectedContainsExactly(false, true, false);

		list.scrollUp();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name2", "name3", "name4")
			.selectedContainsExactly(true, false, false);

		list.scrollUp();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name1", "name2", "name3")
			.selectedContainsExactly(true, false, false);

		list.scrollUp();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1", "name2")
			.selectedContainsExactly(true, false, false);
	}

	@Test
	void scrollUpAndDownMoreItemsThanMax() {
		SelectorList<TestItem> list = SelectorList.of(3);
		list.reset(items(5));

		list.scrollUp();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name2", "name3", "name4")
			.selectedContainsExactly(false, false, true);

		list.scrollUp();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name2", "name3", "name4")
			.selectedContainsExactly(false, true, false);

		list.scrollDown();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name2", "name3", "name4")
			.selectedContainsExactly(false, false, true);
	}

	@Test
	void scrollUpAndDownMoreItemsThanMax2() {
		SelectorList<TestItem> list = SelectorList.of(3);
		list.reset(items(5));

		list.scrollUp();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name2", "name3", "name4")
			.selectedContainsExactly(false, false, true);

		list.scrollUp();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name2", "name3", "name4")
			.selectedContainsExactly(false, true, false);

		list.scrollUp();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name2", "name3", "name4")
			.selectedContainsExactly(true, false, false);

		list.scrollDown();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name2", "name3", "name4")
			.selectedContainsExactly(false, true, false);

	}

	@Test
	void upWhenLessItemsThanMax() {
		SelectorList<TestItem> list = SelectorList.of(3);
		list.reset(items(2));

		list.scrollUp();
		assertThat(list).asInstanceOf(SELECTOR_LIST)
			.namesContainsExactly("name0", "name1")
			.selectedContainsExactly(false, true);
	}

	private static class TestItem implements Nameable {

		String name;
		TestItem(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	List<TestItem> items(int count) {
		return IntStream.range(0, count)
			.mapToObj(i -> {
				return new TestItem("name" + i);
			})
			.collect(Collectors.toList());
	}

	@SuppressWarnings("rawtypes")
	InstanceOfAssertFactory<SelectorList, SelectorListAssert<TestItem>> SELECTOR_LIST = selectorList(TestItem.class);

	@SuppressWarnings("rawtypes")
	static <RESULT extends Nameable> InstanceOfAssertFactory<SelectorList, SelectorListAssert<RESULT>> selectorList(Class<RESULT> resultType) {
		return new InstanceOfAssertFactory<>(SelectorList.class, SelectorListAssertions::<RESULT> assertThat);
	}

	static class SelectorListAssertions {

		public static <T extends Nameable> SelectorListAssert<T> assertThat(SelectorList<T> actual) {
			return new SelectorListAssert<>(actual);
		}
	}

	static class SelectorListAssert<T extends Nameable> extends AbstractAssert<SelectorListAssert<T>, SelectorList<T>> {

		public SelectorListAssert(SelectorList<T> actual) {
			super(actual, SelectorListAssert.class);
		}

		public SelectorListAssert<T> namesContainsExactly(String... names) {
			isNotNull();
			List<String> actualNames = actual.getProjection().stream()
				.map(i -> i.getName())
				.collect(Collectors.toList());
			assertThat(actualNames).containsExactly(names);
			return this;
		}

		public SelectorListAssert<T> selectedContainsExactly(Boolean... selected) {
			isNotNull();
			List<Boolean> actualSelected = actual.getProjection().stream()
				.map(i -> i.isSelected())
				.collect(Collectors.toList());
			assertThat(actualSelected).containsExactly(selected);
			return this;
		}

		public SelectorListAssert<T> hasProjectionSize(int size) {
			isNotNull();
			assertThat(actual.getProjection()).hasSize(size);
			return this;
		}
	}
}
