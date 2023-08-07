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
package org.springframework.shell.core.tui.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

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
import org.springframework.shell.core.tui.component.NumberInput.NumberInputContext;
import org.springframework.shell.core.tui.component.context.ComponentContext;

public class NumberInputTests extends AbstractShellTests {

	private ExecutorService service;
	private AtomicReference<NumberInputContext> result1;
	private AtomicReference<NumberInputContext> result2;

	@BeforeEach
	public void setupTests() {
		service = Executors.newFixedThreadPool(1);
		result1 = new AtomicReference<>();
		result2 = new AtomicReference<>();
	}

	@AfterEach
	public void cleanupTests() {
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
		NumberInput component1 = new NumberInput(dumbTerminal, "component1", 100, Double.class);
		component1.setPrintResults(true);
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			NumberInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      NumberInputContext run1Context = result1.get();

      assertThat(run1Context).isNotNull();
      assertThat(run1Context.getResultValue()).isNull();
    });
	}

	@Test
	public void testResultBasic() {
		ComponentContext<?> empty = ComponentContext.empty();
		NumberInput component1 = new NumberInput(getTerminal(), "component1", 100);
		component1.setPrintResults(true);
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			NumberInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      NumberInputContext run1Context = result1.get();

      assertThat(run1Context).isNotNull();
      assertThat(run1Context.getResultValue()).isEqualTo(100);
      assertThat(consoleOut()).contains("component1 100");
    });
	}

	@Test
	public void testResultBasicWithType() {
		ComponentContext<?> empty = ComponentContext.empty();
		NumberInput component1 = new NumberInput(getTerminal(), "component1", 50.1, Float.class);
		component1.setPrintResults(true);
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			NumberInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      NumberInputContext run1Context = result1.get();

      assertThat(run1Context).isNotNull();
      assertThat(run1Context.getResultValue()).isEqualTo(50.1);
      assertThat(consoleOut()).contains("component1 50.1");
    });
	}

	@Test
	public void testResultUserInput() {
		ComponentContext<?> empty = ComponentContext.empty();
		NumberInput component1 = new NumberInput(getTerminal(), "component1");
		component1.setNumberClass(Double.class);
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			NumberInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().append("123.3").cr();
		write(testBuffer.getBytes());

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      NumberInputContext run1Context = result1.get();

      assertThat(run1Context).isNotNull();
      assertThat(run1Context.getResultValue()).isEqualTo(123.3d);
    });
	}

	@Test
	public void testPassingViaContext() {
		ComponentContext<?> empty = ComponentContext.empty();
		NumberInput component1 = new NumberInput(getTerminal(), "component1", 1);
		NumberInput component2 = new NumberInput(getTerminal(), "component2", 2);
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());
		component2.setResourceLoader(new DefaultResourceLoader());
		component2.setTemplateExecutor(getTemplateExecutor());

		component1.addPostRunHandler(context -> {
			context.put(1, context.getResultValue());
		});

		component2.addPreRunHandler(context -> {
			Integer component1ResultValue = context.get(1);
			context.setDefaultValue(component1ResultValue);
		});
		component2.addPostRunHandler(context -> {
		});

		service.execute(() -> {
			NumberInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

		service.execute(() -> {
			NumberInputContext run1Context = result1.get();
			NumberInputContext run2Context = component2.run(run1Context);
			result2.set(run2Context);
		});

		write(testBuffer.getBytes());

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      NumberInputContext run1Context = result1.get();
      NumberInputContext run2Context = result2.get();

      assertThat(run1Context).isNotSameAs(run2Context);

      assertThat(run1Context).isNotNull();
      assertThat(run2Context).isNotNull();
      assertThat(run1Context.getResultValue()).isEqualTo(1);
      assertThat(run2Context.getResultValue()).isEqualTo(1);
    });
	}

	@Test
	public void testResultUserInputInvalidInput() {
		ComponentContext<?> empty = ComponentContext.empty();
		NumberInput component1 = new NumberInput(getTerminal(), "component1");
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			NumberInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().append("x").cr();
		write(testBuffer.getBytes());

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      NumberInputContext run1Context = result1.get();

      assertThat(consoleOut()).contains("input is invalid");
      assertThat(run1Context).isNull();
    });

		// backspace 2 : cr + input
		testBuffer.backspace(2).append("2").cr();
		write(testBuffer.getBytes());

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      NumberInputContext run1Context = result1.get();

      assertThat(run1Context).isNotNull();
      assertThat(run1Context.getResultValue()).isEqualTo(2);
    });
	}

	@Test
	public void testResultMandatoryInput() {
		ComponentContext<?> empty = ComponentContext.empty();
		NumberInput component1 = new NumberInput(getTerminal());
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());
		component1.setRequired(true);

		service.execute(() -> {
			NumberInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
		});

		TestBuffer testBuffer = new TestBuffer().cr();
		write(testBuffer.getBytes());

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      NumberInputContext run1Context = result1.get();

      assertThat(consoleOut()).contains("This field is mandatory");
      assertThat(run1Context).isNull();
    });

		testBuffer.append("2").cr();
		write(testBuffer.getBytes());

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      NumberInputContext run1Context = result1.get();

      assertThat(run1Context).isNotNull();
      assertThat(run1Context.getResultValue()).isEqualTo(2);
    });
	}
}
