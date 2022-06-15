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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import com.google.common.jimfs.Jimfs;
import org.jline.terminal.impl.DumbTerminal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.shell.component.PathInput.PathInputContext;
import org.springframework.shell.component.context.ComponentContext;

import static org.assertj.core.api.Assertions.assertThat;

public class PathInputTests extends AbstractShellTests {

	private ExecutorService service;
	private CountDownLatch latch1;
	private AtomicReference<PathInputContext> result1;
	private FileSystem fileSystem;
	private Function<String, Path> pathProvider;

	@BeforeEach
	public void setupTests() {
		service = Executors.newFixedThreadPool(1);
		latch1 = new CountDownLatch(1);
		result1 = new AtomicReference<>();
		fileSystem = Jimfs.newFileSystem();
		pathProvider = (path) -> fileSystem.getPath(path);
	}

	@AfterEach
	public void cleanupTests() throws IOException {
		latch1 = null;
		result1 = null;
		if (service != null) {
			service.shutdown();
		}
		service = null;
		if (fileSystem != null) {
			fileSystem.close();
		}
		fileSystem = null;
		pathProvider = null;
	}

	@Test
	void testNoTty() throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DumbTerminal dumbTerminal = new DumbTerminal("terminal", "ansi", in, out, StandardCharsets.UTF_8);

		Path path = fileSystem.getPath("tmp");
		Files.createDirectories(path);
		ComponentContext<?> empty = ComponentContext.empty();
		PathInput component1 = new PathInput(dumbTerminal, "component1");
		component1.setPathProvider(pathProvider);
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			PathInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().append("tmp").cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		PathInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isNull();
	}

	@Test
	public void testResultUserInput() throws InterruptedException, IOException {
		Path path = fileSystem.getPath("tmp");
		Files.createDirectories(path);
		ComponentContext<?> empty = ComponentContext.empty();
		PathInput component1 = new PathInput(getTerminal(), "component1");
		component1.setPathProvider(pathProvider);
		component1.setResourceLoader(new DefaultResourceLoader());
		component1.setTemplateExecutor(getTemplateExecutor());

		service.execute(() -> {
			PathInputContext run1Context = component1.run(empty);
			result1.set(run1Context);
			latch1.countDown();
		});

		TestBuffer testBuffer = new TestBuffer().append("tmp").cr();
		write(testBuffer.getBytes());

		latch1.await(2, TimeUnit.SECONDS);
		PathInputContext run1Context = result1.get();

		assertThat(run1Context).isNotNull();
		assertThat(run1Context.getResultValue()).isNotNull();
		assertThat(run1Context.getResultValue().toString()).contains("tmp");
	}
}
