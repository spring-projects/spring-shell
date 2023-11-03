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
package org.springframework.shell.test.autoconfigure;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.boot.JLineShellAutoConfiguration;
import org.springframework.shell.boot.TerminalCustomizer;
import org.springframework.shell.test.jediterm.terminal.TtyConnector;
import org.springframework.shell.test.jediterm.terminal.ui.JediTermWidget;
import org.springframework.shell.test.jediterm.terminal.ui.TerminalSession;

@AutoConfiguration(before = JLineShellAutoConfiguration.class)
@EnableConfigurationProperties(SpringShellTestProperties.class)
public class ShellAutoConfiguration {

	@Bean
	TerminalCustomizer terminalStreamsTerminalCustomizer(TerminalStreams terminalStreams) {
		return builder -> {
			builder.streams(terminalStreams.input, terminalStreams.output)
				.jansi(false)
				.jna(false)
				.jni(false);
		};
	}

	@Bean
	TerminalStreams terminalStreams() {
		return new TerminalStreams();
	}

	@Bean
	TtyConnector ttyConnector(TerminalStreams terminalStreams) {
		return new TestTtyConnector(terminalStreams.myReader, terminalStreams.myWriter);
	}

	@Bean
	TerminalSession terminalSession(TtyConnector ttyConnector, SpringShellTestProperties properties) {
		JediTermWidget widget = new JediTermWidget(properties.getTerminalWidth(), properties.getTerminalHeight());
		widget.setTtyConnector(ttyConnector);
		return widget;
	}

	public static class TerminalStreams {
		PipedInputStream input;
		PipedOutputStream output;
		InputStreamReader myReader;
		OutputStreamWriter myWriter;

		public TerminalStreams() {
			input = new PipedInputStream();
			output = new PipedOutputStream();
			try {
				myReader = new InputStreamReader(new PipedInputStream(this.output));
				myWriter = new OutputStreamWriter(new PipedOutputStream(this.input));
			} catch (IOException e) {
			}

		}
	}

	private static class TestTtyConnector implements TtyConnector {

		private final static Logger log = LoggerFactory.getLogger(TestTtyConnector.class);
		InputStreamReader myReader;
		OutputStreamWriter myWriter;

		TestTtyConnector(InputStreamReader myReader, OutputStreamWriter myWriter) {
			this.myReader = myReader;
			this.myWriter = myWriter;
		}

		@Override
		public boolean init() {
			return true;
		}

		@Override
		public void close() {
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public int read(char[] buf, int offset, int length) throws IOException {
			log.trace("read1");
			int read = this.myReader.read(buf, offset, length);
			log.trace("read2 {}", read);
			return read;
		}

		@Override
		public void write(byte[] bytes) throws IOException {
			log.trace("write1 {}", bytes);
			this.myWriter.write(new String(bytes));
			this.myWriter.flush();
			log.trace("write2");
		}

		@Override
		public boolean isConnected() {
			return true;
		}

		@Override
		public void write(String string) throws IOException {
			this.write(string.getBytes());
		}

		@Override
		public int waitFor() throws InterruptedException {
			return 0;
		}

		@Override
		public boolean ready() throws IOException {
			log.trace("ready1");
			boolean ready = myReader.ready();
			log.trace("ready2 {}", ready);
			return ready;
		}
	}

}
