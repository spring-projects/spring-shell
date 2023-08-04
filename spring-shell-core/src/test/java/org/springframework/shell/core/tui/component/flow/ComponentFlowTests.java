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
package org.springframework.shell.core.tui.component.flow;

import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import org.springframework.shell.core.tui.component.flow.ComponentFlow.Builder;
import org.springframework.shell.core.tui.component.flow.ComponentFlow.ComponentFlowResult;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class ComponentFlowTests extends AbstractShellTests {

	@Test
	void testSimpleFlow() {
		Map<String, String> single1SelectItems = new HashMap<>();
		single1SelectItems.put("key1", "value1");
		single1SelectItems.put("key2", "value2");
		List<SelectItem> multi1SelectItems = List.of(SelectItem.of("key1", "value1"), SelectItem.of("key2", "value2"),
				SelectItem.of("key3", "value3"));
		ComponentFlow wizard = ComponentFlow.builder()
			.terminal(getTerminal())
			.resourceLoader(getResourceLoader())
			.templateExecutor(getTemplateExecutor())
			.withStringInput("field1")
			.name("Field1")
			.defaultValue("defaultField1Value")
			.and()
			.withStringInput("field2")
			.name("Field2")
			.and()
			.withStringInput("field3")
			.name("Field3")
			.mandatory()
			.and()
			.withPathInput("path1")
			.name("Path1")
			.and()
			.withSingleItemSelector("single1")
			.name("Single1")
			.selectItems(single1SelectItems)
			.and()
			.withMultiItemSelector("multi1")
			.name("Multi1")
			.selectItems(multi1SelectItems)
			.and()
			.build();

		ExecutorService service = Executors.newFixedThreadPool(1);
		AtomicReference<ComponentFlowResult> result = new AtomicReference<>();

		service.execute(() -> result.set(wizard.run()));

		// field1
		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());
		// field2
		testBuffer = new TestBuffer().append("Field2Value").cr();
		write(testBuffer.getBytes());
		// field3
		testBuffer = new TestBuffer().cr().append("Field3Value").cr();
		write(testBuffer.getBytes());
		// path1
		testBuffer = new TestBuffer().append("fakedir").cr();
		write(testBuffer.getBytes());
		// single1
		testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());
		// multi1
		testBuffer = new TestBuffer().ctrlE().space().cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(4)).untilAsserted(() -> {
			ComponentFlowResult inputWizardResult = result.get();
			assertThat(inputWizardResult).isNotNull();
			String field1 = inputWizardResult.getContext().get("field1");
			String field2 = inputWizardResult.getContext().get("field2");
			String field3 = inputWizardResult.getContext().get("field3");
			Path path1 = inputWizardResult.getContext().get("path1");
			String single1 = inputWizardResult.getContext().get("single1");
			List<String> multi1 = inputWizardResult.getContext().get("multi1");
			assertThat(field1).isEqualTo("defaultField1Value");
			assertThat(field3).isEqualTo("Field3Value");
			assertThat(field2).isEqualTo("Field2Value");
			assertThat(path1.toString()).contains("fakedir");
			assertThat(single1).isEqualTo("value1");
			assertThat(multi1).containsExactlyInAnyOrder("value2");
			assertThat(consoleOut()).contains("Field1 defaultField1Value");
		});
	}

	@Test
	void testSkipsGivenComponents() {
		ComponentFlow wizard = ComponentFlow.builder()
			.terminal(getTerminal())
			.withStringInput("id1")
			.name("name")
			.resultValue("value1")
			.resultMode(ResultMode.ACCEPT)
			.and()
			.withPathInput("id2")
			.name("name")
			.resultValue("value2")
			.resultMode(ResultMode.ACCEPT)
			.and()
			.withSingleItemSelector("id3")
			.resultValue("value3")
			.resultMode(ResultMode.ACCEPT)
			.and()
			.withMultiItemSelector("id4")
			.resultValues(List.of("value4"))
			.resultMode(ResultMode.ACCEPT)
			.and()
			.withConfirmationInput("id5")
			.resultValue(false)
			.resultMode(ResultMode.ACCEPT)
			.and()
			.build();

		ExecutorService service = Executors.newFixedThreadPool(1);
		AtomicReference<ComponentFlowResult> result = new AtomicReference<>();

		service.execute(() -> result.set(wizard.run()));

		await().atMost(Duration.ofSeconds(4)).untilAsserted(() -> {
			ComponentFlowResult inputWizardResult = result.get();
			assertThat(inputWizardResult).isNotNull();

			String id1 = inputWizardResult.getContext().get("id1");
			Path id2 = inputWizardResult.getContext().get("id2");
			String id3 = inputWizardResult.getContext().get("id3");
			List<String> id4 = inputWizardResult.getContext().get("id4");
			Boolean id5 = inputWizardResult.getContext().get("id5");

			assertThat(id1).isEqualTo("value1");
			assertThat(id2.toString()).contains("value2");
			assertThat(id3).isEqualTo("value3");
			assertThat(id4).containsExactlyInAnyOrder("value4");
			assertThat(id5).isFalse();
		});
	}

	@Test
	void testChoosesDynamicallyShouldJumpOverAndStop() {
		ComponentFlow wizard = ComponentFlow.builder()
			.terminal(getTerminal())
			.resourceLoader(getResourceLoader())
			.templateExecutor(getTemplateExecutor())
			.withStringInput("id1")
			.name("name")
			.next(ctx -> ctx.get("id1"))
			.and()
			.withStringInput("id2")
			.name("name")
			.resultValue("value2")
			.resultMode(ResultMode.ACCEPT)
			.next(ctx -> null)
			.and()
			.withStringInput("id3")
			.name("name")
			.resultValue("value3")
			.resultMode(ResultMode.ACCEPT)
			.next(ctx -> null)
			.and()
			.build();

		ExecutorService service = Executors.newFixedThreadPool(1);
		AtomicReference<ComponentFlowResult> result = new AtomicReference<>();
		service.execute(() -> result.set(wizard.run()));

		// id1
		TestBuffer testBuffer = new TestBuffer().append("id3").cr();
		write(testBuffer.getBytes());
		// id3
		testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(4)).untilAsserted(() -> {
			ComponentFlowResult inputWizardResult = result.get();
			assertThat(inputWizardResult).isNotNull();
			String id1 = inputWizardResult.getContext().get("id1");
			String id3 = inputWizardResult.getContext().get("id3");
			assertThat(id1).isEqualTo("id3");
			assertThat(id3).isEqualTo("value3");
			assertThat(inputWizardResult.getContext().containsKey("id2")).isFalse();
		});
	}

	@Test
	void testChoosesDynamicallyShouldNotContinueToNext() {
		ComponentFlow wizard = ComponentFlow.builder()
			.terminal(getTerminal())
			.resourceLoader(getResourceLoader())
			.templateExecutor(getTemplateExecutor())
			.withStringInput("id1")
			.name("name")
			.next(ctx -> ctx.get("id1"))
			.and()
			.withStringInput("id2")
			.name("name")
			.resultValue("value2")
			.resultMode(ResultMode.ACCEPT)
			.next(ctx -> null)
			.and()
			.withStringInput("id3")
			.name("name")
			.resultValue("value3")
			.resultMode(ResultMode.ACCEPT)
			.next(ctx -> null)
			.and()
			.build();

		ExecutorService service = Executors.newFixedThreadPool(1);
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<ComponentFlowResult> result = new AtomicReference<>();
		service.execute(() -> {
			result.set(wizard.run());
			latch.countDown();
		});

		// id1
		TestBuffer testBuffer = new TestBuffer().append("id2").cr();
		write(testBuffer.getBytes());
		// id2
		testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(4)).untilAsserted(() -> {
			ComponentFlowResult inputWizardResult = result.get();
			assertThat(inputWizardResult).isNotNull();
			String id1 = inputWizardResult.getContext().get("id1");
			String id2 = inputWizardResult.getContext().get("id2");
			assertThat(id1).isEqualTo("id2");
			assertThat(id2).isEqualTo("value2");
			assertThat(inputWizardResult.getContext().containsKey("id3")).isFalse();
		});
	}

	@Test
	void testChoosesNonExistingComponent() {
		ComponentFlow wizard = ComponentFlow.builder()
			.terminal(getTerminal())
			.resourceLoader(getResourceLoader())
			.templateExecutor(getTemplateExecutor())
			.withStringInput("id1")
			.name("name")
			.next(ctx -> ctx.get("id1"))
			.and()
			.withStringInput("id2")
			.name("name")
			.resultValue("value2")
			.resultMode(ResultMode.ACCEPT)
			.next(ctx -> null)
			.and()
			.withStringInput("id3")
			.name("name")
			.resultValue("value3")
			.resultMode(ResultMode.ACCEPT)
			.next(ctx -> null)
			.and()
			.build();

		ExecutorService service = Executors.newFixedThreadPool(1);
		AtomicReference<ComponentFlowResult> result = new AtomicReference<>();
		service.execute(() -> result.set(wizard.run()));

		// id1
		TestBuffer testBuffer = new TestBuffer().append("fake").cr();
		write(testBuffer.getBytes());

		// don't execute id2 or id3
		testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(4)).untilAsserted(() -> {
			ComponentFlowResult inputWizardResult = result.get();
			assertThat(inputWizardResult).isNotNull();
			String id1 = inputWizardResult.getContext().get("id1");
			assertThat(id1).isEqualTo("fake");
			assertThat(inputWizardResult.getContext().containsKey("id2")).isFalse();
			assertThat(inputWizardResult.getContext().containsKey("id3")).isFalse();
		});
	}

	@Test
	void testAutoShowsDefault() {
		Map<String, String> single1SelectItems = new HashMap<>();
		single1SelectItems.put("key1", "value1");
		single1SelectItems.put("key2", "value2");
		ComponentFlow wizard = ComponentFlow.builder()
			.terminal(getTerminal())
			.resourceLoader(getResourceLoader())
			.templateExecutor(getTemplateExecutor())
			.withSingleItemSelector("single1")
			.name("Single1")
			.selectItems(single1SelectItems)
			.defaultSelect("key2")
			.and()
			.build();

		ExecutorService service = Executors.newFixedThreadPool(1);
		AtomicReference<ComponentFlowResult> result = new AtomicReference<>();

		service.execute(() -> result.set(wizard.run()));

		TestBuffer testBuffer = new TestBuffer();
		testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(4)).untilAsserted(() -> {
			ComponentFlowResult inputWizardResult = result.get();
			assertThat(inputWizardResult).isNotNull();
			String single1 = inputWizardResult.getContext().get("single1");
			assertThat(single1).isEqualTo("value2");
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	void testBuilderSingleTypes() {
		List<SelectItem> selectItems1 = List.of(SelectItem.of("key1", "value1"), SelectItem.of("key2", "value2"));
		Map<String, String> selectItems2 = new HashMap<>();
		selectItems2.put("key2", "value2");
		selectItems2.put("key3", "value3");

		Builder builder1 = ComponentFlow.builder().withSingleItemSelector("field1").selectItems(selectItems1).and();
		Builder builder2 = ComponentFlow.builder().withSingleItemSelector("field2").selectItems(selectItems2).and();

		List<BaseSingleItemSelector> field1 = (List<BaseSingleItemSelector>) ReflectionTestUtils.getField(builder1,
				"singleItemSelectors");
		List<BaseSingleItemSelector> field2 = (List<BaseSingleItemSelector>) ReflectionTestUtils.getField(builder2,
				"singleItemSelectors");
		assertThat(field1).hasSize(1);
		assertThat(field2).hasSize(1);

		List<SelectItem> selectItems11 = (List<SelectItem>) ReflectionTestUtils.getField(field1.get(0), "selectItems");
		List<SelectItem> selectItems21 = (List<SelectItem>) ReflectionTestUtils.getField(field2.get(0), "selectItems");
		assertThat(selectItems11).hasSize(2);
		assertThat(selectItems21).hasSize(2);
	}

}
