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
package org.springframework.shell.jline;

import java.io.ByteArrayOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Attributes;
import org.jline.terminal.impl.ExternalTerminal;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Disabled("Hands intermittently")
class InteractiveShellRunnerTests {

	private PipedOutputStream outIn;

	private JLineInputProvider jLineInputProvider;

	private void initForShortcutKeyTest() throws Exception {
		PipedInputStream in = new PipedInputStream();
		outIn = new PipedOutputStream(in);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ExternalTerminal terminal = new ExternalTerminal("foo", "ansi", in, out, StandardCharsets.UTF_8);
		Attributes attributes = terminal.getAttributes();
		attributes.setLocalFlag(Attributes.LocalFlag.ISIG, true);
		attributes.setControlChar(Attributes.ControlChar.VINTR, 3);
		terminal.setAttributes(attributes);
		LineReaderBuilder builder = LineReaderBuilder.builder().terminal(terminal);

		LineReader lineReader = builder.build();
		jLineInputProvider = new JLineInputProvider(lineReader);
	}

	@Test
	void testClearWithCtrlC() throws Exception {

		initForShortcutKeyTest();

		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch endLatch = new CountDownLatch(1);
		Thread writeThread = new Thread(() -> {
			try {
				startLatch.await();
				outIn.write('a');
				outIn.write(3);
				endLatch.await();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
		Thread readThread = new Thread(() -> {
			assertThatNoException().isThrownBy(() -> assertThat(jLineInputProvider.readInput()).isEqualTo(""));
			endLatch.countDown();
		});
		readThread.start();
		startLatch.countDown();
		writeThread.start();

		readThread.join();
		writeThread.join();
	}

	@Test
	void testExitWithCtrlC() throws Exception {

		initForShortcutKeyTest();

		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch endLatch = new CountDownLatch(1);
		Thread writeThread = new Thread(() -> {
			try {
				startLatch.await();
				outIn.write(3);
				endLatch.await();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
		Thread readThread = new Thread(() -> {
			assertDoesNotThrow(jLineInputProvider::readInput);
			endLatch.countDown();
		});
		readThread.start();
		startLatch.countDown();
		writeThread.start();

		readThread.join();
		writeThread.join();
	}

	@Test
	void testExitWithCtrlD() throws Exception {

		initForShortcutKeyTest();

		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch endLatch = new CountDownLatch(1);
		Thread writeThread = new Thread(() -> {
			try {
				startLatch.await();
				outIn.write(4);
				endLatch.await();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
		Thread readThread = new Thread(() -> {
			assertDoesNotThrow(jLineInputProvider::readInput);
			endLatch.countDown();
		});
		readThread.start();
		startLatch.countDown();
		writeThread.start();

		readThread.join();
		writeThread.join();
	}

}
