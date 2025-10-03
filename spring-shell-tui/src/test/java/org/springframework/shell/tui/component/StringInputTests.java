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
package org.springframework.shell.tui.component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.jline.terminal.impl.DumbTerminal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.shell.tui.component.StringInput.StringInputContext;
import org.springframework.shell.tui.component.context.ComponentContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class StringInputTests extends AbstractShellTests {

	private ExecutorService service;

	private AtomicReference<StringInputContext> result1;

	private AtomicReference<StringInputContext> result2;

	@BeforeEach
	void setupTests() {
		service = Executors.newFixedThreadPool(1);
		result1 = new AtomicReference<>();
		result2 = new AtomicReference<>();
	}

	@AfterEach
	void cleanupTests() {
		result1 = null;
		result2 = null;
		if (service != null) {
			service.shutdown();
		}
		service = null;
	}

	@Test
	void testNoTty() throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DumbTerminal dumbTerminal = new DumbTerminal("terminal", "ansi", in, out, StandardCharsets.UTF_8);

		ComponentContext<?> empty = ComponentContext.empty();
		StringInput component1 = new StringInput(dumbTerminal, "component1", "component1ResultValue");
		component1.setPrintResults(true);
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			StringInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			StringInputContext run1Context = result1.get();

			assertThat(run1Context).isNotNull();
			assertThat(run1Context.getResultValue()).isNull();
		});
	}

	@Test
	void testResultBasic() {
		ComponentContext<?> empty = ComponentContext.empty();
		StringInput component1 = new StringInput(getTerminal(), "component1", "component1ResultValue");
		component1.setPrintResults(true);
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			StringInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			StringInputContext run1Context = result1.get();

			assertThat(run1Context).isNotNull();
			assertThat(run1Context.getResultValue()).isEqualTo("component1ResultValue");
			assertThat(consoleOut()).contains("component1 component1ResultValue");
		});
	}

	@Test
	void testResultBasicWithMask() {
		ComponentContext<?> empty = ComponentContext.empty();
		StringInput component1 = new StringInput(getTerminal(), "component1", "component1ResultValue");
		component1.setPrintResults(true);
		component1.setMaskCharacter('*');
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			StringInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			StringInputContext run1Context = result1.get();

			assertThat(run1Context).isNotNull();
			assertThat(run1Context.getResultValue()).isEqualTo("component1ResultValue");
			assertThat(consoleOut()).contains("component1 *********************");
		});
	}

	@Test
	void testResultUserInput() {
		ComponentContext<?> empty = ComponentContext.empty();
		StringInput component1 = new StringInput(getTerminal(), "component1", "component1ResultValue");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			StringInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().append("test").cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			StringInputContext run1Context = result1.get();

			assertThat(run1Context).isNotNull();
			assertThat(run1Context.getResultValue()).isEqualTo("test");
		});
	}

	@Test
	void testResultUserInputUnicode() {
		ComponentContext<?> empty = ComponentContext.empty();
		StringInput component1 = new StringInput(getTerminal(), "component1", "component1ResultValue");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			StringInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().append("😂").cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			StringInputContext run1Context = result1.get();

			assertThat(run1Context).isNotNull();
			assertThat(run1Context.getResultValue()).isEqualTo("😂");
		});
	}

	@Test
	void testPassingViaContext() {
		ComponentContext<?> empty = ComponentContext.empty();
		StringInput component1 = new StringInput(getTerminal(), "component1", "component1ResultValue");
		StringInput component2 = new StringInput(getTerminal(), "component2", "component2ResultValue");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());
		component2.setResourceLoader(new DefaultResourceLoader());
		component2.setTemplateExecutor(getTemplateExecutor());

		component1.addPostRunHandler(context -> context.put("component1ResultValue", context.getResultValue()));

		component2.addPreRunHandler(context -> {
			String component1ResultValue = context.get("component1ResultValue");
			context.setDefaultValue(component1ResultValue);
		});
		component2.addPostRunHandler(context -> {
		});

		service.execute(() -> {
			StringInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		service.execute(() -> {
			StringInputContext run1Context = result1.get();
			StringInputContext run2Context = component2.run(run1Context);
			result2.set(run2Context);
		});

		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			StringInputContext run1Context = result1.get();
			StringInputContext run2Context = result2.get();

			assertThat(run1Context).isNotSameAs(run2Context);

			assertThat(run1Context).isNotNull();
			assertThat(run2Context).isNotNull();
			assertThat(run1Context.getResultValue()).isEqualTo("component1ResultValue");
			assertThat(run2Context.getResultValue()).isEqualTo("component1ResultValue");
		});
	}

}
