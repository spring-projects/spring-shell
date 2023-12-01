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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
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
import org.springframework.shell.component.StringInput.StringInputContext;
import org.springframework.shell.component.context.ComponentContext;

public class StringInputTests extends AbstractShellTests {

	private ExecutorService service;
	private CountDownLatch latch1;
	private CountDownLatch latch2;
	private AtomicReference<StringInputContext> result1;
	private AtomicReference<StringInputContext> result2;

	@BeforeEach
	public void setupTests() {
		service = Executors.newFixedThreadPool(1);
		latch1 = new CountDownLatch(1);
		latch2 = new CountDownLatch(1);
		result1 = new AtomicReference<>();
		result2 = new AtomicReference<>();
	}

	@AfterEach
	public void cleanupTests() {
		latch1 = null;
		latch2 = null;
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
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		StringInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isNull();
	}

	@Test
	public void testResultBasic() throws InterruptedException {
		ComponentContext<?> empty = ComponentContext.empty();
		StringInput component1 = new StringInput(getTerminal(), "component1", "component1ResultValue");
		component1.setPrintResults(true);
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			StringInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		StringInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isEqualTo("component1ResultValue");
		assertThat(consoleOut()).contains("component1 component1ResultValue");
	}

	@Test
	public void testResultBasicWithMask() throws InterruptedException {
		ComponentContext<?> empty = ComponentContext.empty();
		StringInput component1 = new StringInput(getTerminal(), "component1", "component1ResultValue");
		component1.setPrintResults(true);
		component1.setMaskCharacter('*');
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			StringInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		StringInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isEqualTo("component1ResultValue");
		assertThat(consoleOut()).contains("component1 *********************");
	}

	@Test
	public void testResultUserInput() throws InterruptedException {
		ComponentContext<?> empty = ComponentContext.empty();
		StringInput component1 = new StringInput(getTerminal(), "component1", "component1ResultValue");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			StringInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().append("test").cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		StringInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isEqualTo("test");
	}

	@Test
	public void testResultMandatoryInput() throws InterruptedException {
		ComponentContext<?> empty = ComponentContext.empty();
		StringInput component1 = new StringInput(getTerminal());
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());
		component1.setRequired(true);

		service.execute(() -> {
			StringInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);

		StringInputContext run1Context = result1.get();
		assertThat(consoleOut()).contains("This field is mandatory");
		assertThat(run1Context).isNull();

		testBuffer.append("test").cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isEqualTo("test");
	}

	@Test
	public void testPassingViaContext() throws InterruptedException {
		ComponentContext<?> empty = ComponentContext.empty();
		StringInput component1 = new StringInput(getTerminal(), "component1", "component1ResultValue");
		StringInput component2 = new StringInput(getTerminal(), "component2", "component2ResultValue");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());
		component2.setResourceLoader(new DefaultResourceLoader());
		component2.setTemplateExecutor(getTemplateExecutor());

		component1.addPostRunHandler(context -> {
			context.put("component1ResultValue", context.getResultValue());
		});

		component2.addPreRunHandler(context -> {
			String component1ResultValue = context.get("component1ResultValue");
			context.setDefaultValue(component1ResultValue);
		});
		component2.addPostRunHandler(context -> {
		});

		service.execute(() -> {
			StringInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);

		service.execute(() -> {
			StringInputContext run1Context = result1.get();
			StringInputContext run2Context = component2.run(run1Context);
			result2.set(run2Context);
			latch2.countDown();
		});

		write(testBuffer.getBytes());

		latch2.await(2, TimeUnit.SECONDS);

		StringInputContext run1Context = result1.get();
		StringInputContext run2Context = result2.get();

		assertThat(run1Context).isNotSameAs(run2Context);

		assertThat(run1Context).isNotNull();
		assertThat(run2Context).isNotNull();
		assertThat(run1Context.getResultValue()).isEqualTo("component1ResultValue");
		assertThat(run2Context.getResultValue()).isEqualTo("component1ResultValue");
	}
}
