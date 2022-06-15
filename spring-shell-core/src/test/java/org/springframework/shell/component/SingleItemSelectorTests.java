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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.support.SelectorItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.shell.component.ShellAssertions.assertStringOrderThat;

public class SingleItemSelectorTests extends AbstractShellTests {

	private static SimplePojo SIMPLE_POJO_1 = SimplePojo.of("data1");
	private static SimplePojo SIMPLE_POJO_2 = SimplePojo.of("data2");
	private static SimplePojo SIMPLE_POJO_3 = SimplePojo.of("data3");
	private static SimplePojo SIMPLE_POJO_4 = SimplePojo.of("data4");
	private static SimplePojo SIMPLE_POJO_5 = SimplePojo.of("data5");
	private static SimplePojo SIMPLE_POJO_6 = SimplePojo.of("data6");
	private static SelectorItem<SimplePojo> SELECTOR_ITEM_1 = SelectorItem.of("simplePojo1", SIMPLE_POJO_1);
	private static SelectorItem<SimplePojo> SELECTOR_ITEM_2 = SelectorItem.of("simplePojo2", SIMPLE_POJO_2);
	private static SelectorItem<SimplePojo> SELECTOR_ITEM_3 = SelectorItem.of("simplePojo3", SIMPLE_POJO_3);
	private static SelectorItem<SimplePojo> SELECTOR_ITEM_4 = SelectorItem.of("simplePojo4", SIMPLE_POJO_4);
	private static SelectorItem<SimplePojo> SELECTOR_ITEM_5 = SelectorItem.of("simplePojo5", SIMPLE_POJO_5);
	private static SelectorItem<SimplePojo> SELECTOR_ITEM_6 = SelectorItem.of("simplePojo6", SIMPLE_POJO_6);

	private ExecutorService service;
	private CountDownLatch latch;
	private AtomicReference<Optional<SelectorItem<SimplePojo>>> result;

	@BeforeEach
	public void setupMulti() {
		service = Executors.newFixedThreadPool(1);
		latch = new CountDownLatch(1);
		result = new AtomicReference<>();
	}

	@AfterEach
	public void cleanupMulti() {
		latch = null;
		result = null;
		if (service != null) {
			service.shutdown();
		}
		service = null;
	}

	@Test
	public void testItemsShownFirstHovered() {
		scheduleSelect();
		await().atMost(Duration.ofSeconds(4))
				.untilAsserted(() -> {
					assertStringOrderThat(consoleOut()).containsInOrder("> simplePojo1", "simplePojo2", "simplePojo3", "simplePojo4");
				});

	}

	@Test
	public void testMaxItems() {
		scheduleSelect(Arrays.asList(SELECTOR_ITEM_1, SELECTOR_ITEM_2, SELECTOR_ITEM_3, SELECTOR_ITEM_4,
				SELECTOR_ITEM_5, SELECTOR_ITEM_6), 6);
		await().atMost(Duration.ofSeconds(4))
				.untilAsserted(() -> assertStringOrderThat(consoleOut()).containsInOrder("simplePojo1", "simplePojo2",
						"simplePojo3", "simplePojo4", "simplePojo5", "simplePojo6"));
	}

	@Test
	void testNoTty() throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DumbTerminal dumbTerminal = new DumbTerminal("terminal", "ansi", in, out, StandardCharsets.UTF_8);

		scheduleSelect(dumbTerminal);
		awaitLatch();

		Optional<SelectorItem<SimplePojo>> selected = result.get();
		assertThat(selected).isEmpty();
	}

	@Test
	public void testSelectFirst() throws InterruptedException {
		scheduleSelect();

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		awaitLatch();

		Optional<SelectorItem<SimplePojo>> selected = result.get();
		assertThat(selected).isNotEmpty();
		Optional<String> datas = selected.map(SelectorItem::getItem).map(SimplePojo::getData);
		assertThat(datas).contains("data1");
		assertThat(consoleOut()).contains("testSimple data1");
	}

	@Test
	public void testSelectSecond() throws InterruptedException {
		scheduleSelect();

		TestBuffer testBuffer = new TestBuffer().ctrlE().cr();
		write(testBuffer.getBytes());

		awaitLatch();

		Optional<SelectorItem<SimplePojo>> selected = result.get();
		assertThat(selected).isNotEmpty();
		Optional<String> datas = selected.map(SelectorItem::getItem).map(SimplePojo::getData);
		assertThat(datas).contains("data2");
	}

	@Test
	public void testSelectLastBackwards() throws InterruptedException {
		scheduleSelect();

		TestBuffer testBuffer = new TestBuffer().ctrlY().cr();
		write(testBuffer.getBytes());

		awaitLatch();

		Optional<SelectorItem<SimplePojo>> selected = result.get();
		assertThat(selected).isNotEmpty();
		Optional<String> datas = selected.map(SelectorItem::getItem).map(SimplePojo::getData);
		assertThat(datas).contains("data4");
	}

	@Test
	public void testFilterShowsNoneThenSelect() throws InterruptedException {
		scheduleSelect();

		TestBuffer testBuffer = new TestBuffer().append("xxx").cr();
		write(testBuffer.getBytes());

		assertThat(awaitLatch(1)).isFalse();

		testBuffer = new TestBuffer().backspace(3).cr();
		write(testBuffer.getBytes());

		assertThat(awaitLatch()).isTrue();

		Optional<SelectorItem<SimplePojo>> selected = result.get();
		assertThat(selected).isNotEmpty();
	}

	private void scheduleSelect(Terminal terminal) {
		scheduleSelect(Arrays.asList(SELECTOR_ITEM_1, SELECTOR_ITEM_2, SELECTOR_ITEM_3, SELECTOR_ITEM_4), null,
				terminal);
	}

	private void scheduleSelect() {
		scheduleSelect(Arrays.asList(SELECTOR_ITEM_1, SELECTOR_ITEM_2, SELECTOR_ITEM_3, SELECTOR_ITEM_4));
	}

	private void scheduleSelect(List<SelectorItem<SimplePojo>> items) {
		scheduleSelect(items, null);
	}

	private void scheduleSelect(List<SelectorItem<SimplePojo>> items, Integer maxItems) {
		scheduleSelect(items, maxItems, getTerminal());
	}

	private void scheduleSelect(List<SelectorItem<SimplePojo>> items, Integer maxItems, Terminal terminal) {
		SingleItemSelector<SimplePojo, SelectorItem<SimplePojo>> selector = new SingleItemSelector<>(terminal,
				items, "testSimple", null);
		selector.setResourceLoader(new DefaultResourceLoader());
		selector.setTemplateExecutor(getTemplateExecutor());

		selector.setPrintResults(true);
		if (maxItems != null) {
			selector.setMaxItems(maxItems);
		}
		service.execute(() -> {
			ComponentContext<?> context = ComponentContext.empty();
			result.set(selector.run(context).getResultItem());
			latch.countDown();
		});
	}

	private boolean awaitLatch() throws InterruptedException {
		return awaitLatch(4);
	}

	private boolean awaitLatch(int seconds) throws InterruptedException {
		return latch.await(seconds, TimeUnit.SECONDS);
	}

	private static class SimplePojo {
		String data;

		SimplePojo(String data) {
			this.data = data;
		}

		public String getData() {
			return data;
		}

		static SimplePojo of(String data) {
			return new SimplePojo(data);
		}

		@Override
		public String toString() {
			return data;
		}
	}
}
