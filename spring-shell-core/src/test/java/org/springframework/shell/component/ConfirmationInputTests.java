/*
 * Copyright 2022-2023 the original author or authors.
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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jline.terminal.impl.DumbTerminal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.shell.component.ConfirmationInput.ConfirmationInputContext;
import org.springframework.shell.component.context.ComponentContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class ConfirmationInputTests extends AbstractShellTests {

	private ExecutorService service;
	private CountDownLatch latch1;
	private AtomicReference<ConfirmationInputContext> result1;

	@BeforeEach
	public void setupTests() {
		service = Executors.newFixedThreadPool(1);
		latch1 = new CountDownLatch(1);
		result1 = new AtomicReference<>();
	}

	@AfterEach
	public void cleanupTests() throws IOException {
		latch1 = null;
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
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		ConfirmationInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isNull();
	}

	@Test
	public void testResultUserInputEnterDefaultYes() throws InterruptedException, IOException {
		ComponentContext<?> empty = ComponentContext.empty();
		ConfirmationInput component1 = new ConfirmationInput(getTerminal(), "component1");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		ConfirmationInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isNotNull();
		assertThat(run1Context.getResultValue()).isTrue();
	}

	@Test
	public void testResultUserInputEnterDefaultNo() throws InterruptedException, IOException {
		ComponentContext<?> empty = ComponentContext.empty();
		ConfirmationInput component1 = new ConfirmationInput(getTerminal(), "component1", false);
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		ConfirmationInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isNotNull();
		assertThat(run1Context.getResultValue()).isFalse();
	}

	@Test
	public void testResultUserInputNo() throws InterruptedException, IOException {
		ComponentContext<?> empty = ComponentContext.empty();
		ConfirmationInput component1 = new ConfirmationInput(getTerminal(), "component1");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().append("no").cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		ConfirmationInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isNotNull();
		assertThat(run1Context.getResultValue()).isFalse();
	}

	@Test
	public void testUserInputShown() throws InterruptedException, IOException {
		ComponentContext<?> empty = ComponentContext.empty();
		ConfirmationInput component1 = new ConfirmationInput(getTerminal(), "component1");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().append("N");
		write(testBuffer.getBytes());

		await().atMost(Duration.ofSeconds(4))
				.untilAsserted(() -> assertThat(consoleOut()).contains("N"));

		testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		ConfirmationInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isNotNull();
		assertThat(run1Context.getResultValue()).isFalse();
	}

	@Test
	public void testResultUserInputYes() throws InterruptedException, IOException {
		ComponentContext<?> empty = ComponentContext.empty();
		ConfirmationInput component1 = new ConfirmationInput(getTerminal(), "component1");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().append("yes").cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		ConfirmationInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isNotNull();
		assertThat(run1Context.getResultValue()).isTrue();
	}

	@Test
	public void testResultUserInputInvalidInput() throws InterruptedException, IOException {
		ComponentContext<?> empty = ComponentContext.empty();
		ConfirmationInput component1 = new ConfirmationInput(getTerminal(), "component1");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			ConfirmationInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().append("x").cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);

		assertThat(consoleOut()).contains("input is invalid");

		ConfirmationInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isNull();
	}
}
