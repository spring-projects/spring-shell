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

import java.io.ByteArrayOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;
import org.jline.utils.AttributedString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.style.TemplateExecutor;
import org.springframework.shell.style.Theme;
import org.springframework.shell.style.ThemeRegistry;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.shell.style.ThemeSettings;

import static org.jline.keymap.KeyMap.del;

public abstract class AbstractShellTests {

    private ExecutorService executorService;
    private PipedInputStream pipedInputStream;
    private PipedOutputStream pipedOutputStream;
    private LinkedBlockingQueue<byte[]> bytesQueue;
	private ByteArrayOutputStream consoleOut;
	private Terminal terminal;
	private TemplateExecutor templateExecutor;
    private ResourceLoader resourceLoader;

	@BeforeEach
	public void setup() throws Exception {
		executorService = Executors.newFixedThreadPool(1);
		pipedInputStream = new PipedInputStream();
		pipedOutputStream = new PipedOutputStream();
		bytesQueue = new LinkedBlockingQueue<>();
		consoleOut = new ByteArrayOutputStream();

		ThemeRegistry themeRegistry = new ThemeRegistry();
		themeRegistry.register(new Theme() {
			@Override
			public String getName() {
				return "default";
			}

			@Override
			public ThemeSettings getSettings() {
				return ThemeSettings.defaults();
			}
		});
		ThemeResolver themeResolver = new ThemeResolver(themeRegistry, "default");
		templateExecutor = new TemplateExecutor(themeResolver);

        resourceLoader = new DefaultResourceLoader();

        pipedInputStream.connect(pipedOutputStream);
		terminal = new DumbTerminal("terminal", "ansi", pipedInputStream, consoleOut, StandardCharsets.UTF_8);
        terminal.setSize(new Size(1, 1));

        executorService.execute(() -> {
            try {
                while (true) {
                    byte[] take = bytesQueue.take();
                    pipedOutputStream.write(take);
                    pipedOutputStream.flush();
                }
            } catch (Exception e) {
            }
        });
	}

	@AfterEach
	public void cleanup() {
		executorService.shutdown();
	}

	protected void write(byte[] bytes) {
		bytesQueue.add(bytes);
	}

	protected String consoleOut() {
        return AttributedString.fromAnsi(consoleOut.toString()).toString();
	}

	protected Terminal getTerminal() {
		return terminal;
	}

    protected ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    protected TemplateExecutor getTemplateExecutor() {
        return templateExecutor;
    }

    protected class TestBuffer {
        private final ByteArrayOutputStream out = new ByteArrayOutputStream();

        public TestBuffer() {
        }

        public TestBuffer(String str) {
            append(str);
        }

        public TestBuffer(char[] chars) {
            append(new String(chars));
        }

        @Override
        public String toString() {
            try {
                return out.toString(StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public TestBuffer cr() {
			return append("\r");
        }

        public TestBuffer backspace() {
            return append(del());
        }

        public TestBuffer backspace(int count) {
            TestBuffer buf = this;
            for (int i = 0; i < count; i++) {
                buf = backspace();
            }
            return buf;
        }

		public TestBuffer down() {
			return append("\033[B");
        }

		public TestBuffer ctrl(char let) {
            return append(KeyMap.ctrl(let));
        }

        public TestBuffer ctrlE() {
            return ctrl('E');
        }

        public TestBuffer ctrlY() {
            return ctrl('Y');
        }

		public TestBuffer space() {
			return append(" ");
        }

        public byte[] getBytes() {
            return out.toByteArray();
        }

        public TestBuffer append(final String str) {
            for (byte b : str.getBytes(StandardCharsets.UTF_8)) {
                append(b);
            }
            return this;
        }

        public TestBuffer append(final int i) {
            // consoleOut.reset();
            out.write((byte) i);
            return this;
        }
	}
}
