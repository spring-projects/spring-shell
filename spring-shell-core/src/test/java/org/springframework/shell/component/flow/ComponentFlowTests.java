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
package org.springframework.shell.component.flow;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.shell.component.flow.ComponentFlow.ComponentFlowResult;

public class ComponentFlowTests extends AbstractShellTests {

	@Test
	public void testSimpleFlow() throws InterruptedException {
		Map<String, String> single1SelectItems = new HashMap<>();
		single1SelectItems.put("key1", "value1");
		single1SelectItems.put("key2", "value2");
		List<SelectItem> multi1SelectItems = Arrays.asList(SelectItem.of("key1", "value1"),
				SelectItem.of("key2", "value2"), SelectItem.of("key3", "value3"));
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
				.withNumberInput("number1")
					.name("Number1")
					.and()
				.withNumberInput("number2")
					.name("Number2")
					.defaultValue(20.5)
					.numberClass(Double.class)
					.and()
				.withNumberInput("number3")
					.name("Number3")
					.required()
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
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<ComponentFlowResult> result = new AtomicReference<>();

		service.execute(() -> {
			result.set(wizard.run());
			latch.countDown();
		});

		// field1
		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());
		// field2
		testBuffer = new TestBuffer().append("Field2Value").cr();
		write(testBuffer.getBytes());
		// number1
		testBuffer = new TestBuffer().append("35").cr();
		write(testBuffer.getBytes());
		// number2
		testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());
		// number3
		testBuffer = new TestBuffer().cr().append("5").cr();
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

		latch.await(4, TimeUnit.SECONDS);
		ComponentFlowResult inputWizardResult = result.get();
		assertThat(inputWizardResult).isNotNull();
		String field1 = inputWizardResult.getContext().get("field1");
		String field2 = inputWizardResult.getContext().get("field2");
		Integer number1 = inputWizardResult.getContext().get("number1");
		Double number2 = inputWizardResult.getContext().get("number2");
		Integer number3 = inputWizardResult.getContext().get("number3");
		Path path1 = inputWizardResult.getContext().get("path1");
		String single1 = inputWizardResult.getContext().get("single1");
		List<String> multi1 = inputWizardResult.getContext().get("multi1");
		assertThat(field1).isEqualTo("defaultField1Value");
		assertThat(field2).isEqualTo("Field2Value");
		assertThat(number1).isEqualTo(35);
		assertThat(number2).isEqualTo(20.5);
		assertThat(number3).isEqualTo(5);
		assertThat(path1.toString()).contains("fakedir");
		assertThat(single1).isEqualTo("value1");
		assertThat(multi1).containsExactlyInAnyOrder("value2");
		assertThat(consoleOut()).contains("Field1 defaultField1Value");
	}

	@Test
	public void testSkipsGivenComponents() throws InterruptedException {
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
				.resultValues(Arrays.asList("value4"))
				.resultMode(ResultMode.ACCEPT)
				.and()
			.withConfirmationInput("id5")
				.resultValue(false)
				.resultMode(ResultMode.ACCEPT)
				.and()
			.withNumberInput("id6")
				.resultValue(50)
				.resultMode(ResultMode.ACCEPT)
				.and()
			.build();

			ExecutorService service = Executors.newFixedThreadPool(1);
			CountDownLatch latch = new CountDownLatch(1);
			AtomicReference<ComponentFlowResult> result = new AtomicReference<>();

			service.execute(() -> {
				result.set(wizard.run());
				latch.countDown();
			});

			latch.await(4, TimeUnit.SECONDS);
			ComponentFlowResult inputWizardResult = result.get();
			assertThat(inputWizardResult).isNotNull();

			String id1 = inputWizardResult.getContext().get("id1");
			Path id2 = inputWizardResult.getContext().get("id2");
			String id3 = inputWizardResult.getContext().get("id3");
			List<String> id4 = inputWizardResult.getContext().get("id4");
			Boolean id5 = inputWizardResult.getContext().get("id5");
			Integer id6 = inputWizardResult.getContext().get("id6");

			assertThat(id1).isEqualTo("value1");
			assertThat(id2.toString()).contains("value2");
			assertThat(id3).isEqualTo("value3");
			assertThat(id4).containsExactlyInAnyOrder("value4");
			assertThat(id5).isFalse();
			assertThat(id6).isEqualTo(50);
		}

	@Test
	public void testChoosesDynamicallyShouldJumpOverAndStop() throws InterruptedException {
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
		TestBuffer testBuffer = new TestBuffer().append("id3").cr();
		write(testBuffer.getBytes());
		// id3
		testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());
		latch.await(4, TimeUnit.SECONDS);
		ComponentFlowResult inputWizardResult = result.get();
		assertThat(inputWizardResult).isNotNull();
		String id1 = inputWizardResult.getContext().get("id1");
		String id3 = inputWizardResult.getContext().get("id3");
		assertThat(id1).isEqualTo("id3");
		assertThat(id3).isEqualTo("value3");
		assertThat(inputWizardResult.getContext().containsKey("id2")).isFalse();
	}

	@Test
	public void testChoosesDynamicallyShouldNotContinueToNext() throws InterruptedException {
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
		latch.await(4, TimeUnit.SECONDS);
		ComponentFlowResult inputWizardResult = result.get();
		assertThat(inputWizardResult).isNotNull();
		String id1 = inputWizardResult.getContext().get("id1");
		String id2 = inputWizardResult.getContext().get("id2");
		assertThat(id1).isEqualTo("id2");
		assertThat(id2).isEqualTo("value2");
		assertThat(inputWizardResult.getContext().containsKey("id3")).isFalse();
	}

	@Test
	public void testChoosesNonExistingComponent() throws InterruptedException {
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
		TestBuffer testBuffer = new TestBuffer().append("fake").cr();
		write(testBuffer.getBytes());

		// don't execute id2 or id3
		testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());
		latch.await(4, TimeUnit.SECONDS);
		ComponentFlowResult inputWizardResult = result.get();
		assertThat(inputWizardResult).isNotNull();
		String id1 = inputWizardResult.getContext().get("id1");
		assertThat(id1).isEqualTo("fake");
		assertThat(inputWizardResult.getContext().containsKey("id2")).isFalse();
		assertThat(inputWizardResult.getContext().containsKey("id3")).isFalse();
	}

	@Test
	public void testAutoShowsDefault() throws InterruptedException {
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
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<ComponentFlowResult> result = new AtomicReference<>();

		service.execute(() -> {
			result.set(wizard.run());
			latch.countDown();
		});

		TestBuffer testBuffer = new TestBuffer();
		testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		latch.await(4, TimeUnit.SECONDS);
		ComponentFlowResult inputWizardResult = result.get();
		assertThat(inputWizardResult).isNotNull();
		String single1 = inputWizardResult.getContext().get("single1");
		assertThat(single1).isEqualTo("value2");
	}
}
