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
package org.springframework.shell.core.tui.component;

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
import org.springframework.shell.core.tui.component.ConfirmationInput.ConfirmationInputContext;
import org.springframework.shell.core.tui.component.context.ComponentContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class ConfirmationInputTests extends AbstractShellTests {

	private ExecutorService service;

	private AtomicReference<ConfirmationInputContext> result1;

	@BeforeEach
	void setupTests() {
		service = Executors.newFixedThreadPool(1);
		result1 = new AtomicReference<>();
	}

	@AfterEach
	void cleanupTests() {
		result1 = null;
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
		ConfirmationInput component1 = new ConfirmationInput(dumbTerminal, "component1");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			ConfirmationInputContext run1Context = result1.get();

			assertThat(run1Context).isNotNull();
			assertThat(run1Context.getResultValue()).isNull();
		});
	}

	@Test
	void testResultUserInputEnterDefaultYes() {
		ComponentContext<?> empty = ComponentContext.empty();
		ConfirmationInput component1 = new ConfirmationInput(getTerminal(), "component1");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			ConfirmationInputContext run1Context = result1.get();

			assertThat(run1Context).isNotNull();
			assertThat(run1Context.getResultValue()).isNotNull();
			assertThat(run1Context.getResultValue()).isTrue();
		});
	}

	@Test
	void testResultUserInputEnterDefaultNo() {
		ComponentContext<?> empty = ComponentContext.empty();
		ConfirmationInput component1 = new ConfirmationInput(getTerminal(), "component1", false);
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			ConfirmationInputContext run1Context = result1.get();

			assertThat(run1Context).isNotNull();
			assertThat(run1Context.getResultValue()).isNotNull();
			assertThat(run1Context.getResultValue()).isFalse();
		});
	}

	@Test
	void testResultUserInputNo() {
		ComponentContext<?> empty = ComponentContext.empty();
		ConfirmationInput component1 = new ConfirmationInput(getTerminal(), "component1");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().append("no").cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			ConfirmationInputContext run1Context = result1.get();

			assertThat(run1Context).isNotNull();
			assertThat(run1Context.getResultValue()).isNotNull();
			assertThat(run1Context.getResultValue()).isFalse();
		});
	}

	@Test
	void testUserInputShown() {
		ComponentContext<?> empty = ComponentContext.empty();
		ConfirmationInput component1 = new ConfirmationInput(getTerminal(), "component1");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().append("N");
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(4)).untilAsserted(() -> assertThat(consoleOut()).contains("N"));

		testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			ConfirmationInputContext run1Context = result1.get();

			assertThat(run1Context).isNotNull();
			assertThat(run1Context.getResultValue()).isNotNull();
			assertThat(run1Context.getResultValue()).isFalse();
		});
	}

	@Test
	void testResultUserInputYes() {
		ComponentContext<?> empty = ComponentContext.empty();
		ConfirmationInput component1 = new ConfirmationInput(getTerminal(), "component1");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().append("yes").cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			ConfirmationInputContext run1Context = result1.get();

			assertThat(run1Context).isNotNull();
			assertThat(run1Context.getResultValue()).isNotNull();
			assertThat(run1Context.getResultValue()).isTrue();
		});
	}

	@Test
	void testResultUserInputInvalidInput() {
		ComponentContext<?> empty = ComponentContext.empty();
		ConfirmationInput component1 = new ConfirmationInput(getTerminal(), "component1");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().append("x").cr();
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
			assertThat(consoleOut()).contains("input is invalid");
			ConfirmationInputContext run1Context = result1.get();

			assertThat(run1Context).isNotNull();
			assertThat(run1Context.getResultValue()).isNull();
		});
	}

}
