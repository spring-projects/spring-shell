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
package org.springframework.shell.test.jediterm.terminal;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.test.jediterm.terminal.emulator.Emulator;
import org.springframework.shell.test.jediterm.terminal.emulator.JediEmulator;


/**
 * Runs terminal emulator. Manages threads to send response.
 *
 * @author jediterm authors
 */
public class TerminalStarter implements TerminalOutputStream {

	private static final Logger LOG = LoggerFactory.getLogger(TerminalStarter.class);

	private final Emulator myEmulator;

	private final Terminal myTerminal;

	private final TtyConnector myTtyConnector;

	private final ScheduledExecutorService myEmulatorExecutor = Executors.newSingleThreadScheduledExecutor();

	public TerminalStarter(final Terminal terminal, final TtyConnector ttyConnector, TerminalDataStream dataStream) {
		myTtyConnector = ttyConnector;
		myTerminal = terminal;
		myTerminal.setTerminalOutput(this);
		myEmulator = createEmulator(dataStream, terminal);
	}

	protected JediEmulator createEmulator(TerminalDataStream dataStream, Terminal terminal) {
		return new JediEmulator(dataStream, terminal);
	}

	private void execute(Runnable runnable) {
		if (!myEmulatorExecutor.isShutdown()) {
			myEmulatorExecutor.execute(runnable);
		}
	}

	public void start() {
		try {
			while (!Thread.currentThread().isInterrupted() && myEmulator.hasNext()) {
				myEmulator.next();
			}
		}
		catch (final InterruptedIOException e) {
			LOG.info("Terminal exiting");
		}
		catch (final Exception e) {
			if (!myTtyConnector.isConnected()) {
				myTerminal.disconnected();
				return;
			}
			LOG.error("Caught exception in terminal thread", e);
		}
	}

	// public byte[] getCode(final int key, final int modifiers) {
	// 	return myTerminal.getCodeForKey(key, modifiers);
	// }

	public void postResize(int width, int height, RequestOrigin origin) {
		execute(() -> {
			resize(myEmulator, myTerminal, myTtyConnector, width, height, origin, (millisDelay, runnable) -> {
				myEmulatorExecutor.schedule(runnable, millisDelay, TimeUnit.MILLISECONDS);
			});
		});
	}

	/**
	 * Resizes terminal and tty connector, should be called on a pooled thread.
	 */
	public static void resize(Emulator emulator, Terminal terminal, TtyConnector ttyConnector, int width, int height,
			RequestOrigin origin, BiConsumer<Long, Runnable> taskScheduler) {
		CompletableFuture<?> promptUpdated = ((JediEmulator)emulator).getPromptUpdatedAfterResizeFuture(taskScheduler);
		terminal.resize(width, height, origin, promptUpdated);
		ttyConnector.resize(width, height);
	}

	@Override
	public void sendBytes(final byte[] bytes) {
		sendBytes(bytes, false);
	}

	@Override
	public void sendBytes(final byte[] bytes, boolean userInput) {
		execute(() -> {
			try {
				myTtyConnector.write(bytes);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void sendString(final String string) {
		sendString(string, false);
	}

	@Override
	public void sendString(final String string, boolean userInput) {
		execute(() -> {
			try {

				myTtyConnector.write(string);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public void close() {
		execute(() -> {
			try {
				myTtyConnector.close();
			}
			catch (Exception e) {
				LOG.error("Error closing terminal", e);
			}
			finally {
				myEmulatorExecutor.shutdown();
			}
		});
	}
}
