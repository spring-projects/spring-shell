/*
 * Copyright 2022-2024 the original author or authors.
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
package org.springframework.shell.test;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.shell.Shell;
import org.springframework.shell.ShellRunner;
import org.springframework.shell.context.DefaultShellContext;
import org.springframework.shell.jline.InteractiveShellRunner;
import org.springframework.shell.jline.NonInteractiveShellRunner;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.test.jediterm.terminal.ui.TerminalSession;

/**
 * Client for terminal session which can be used as a programmatic way
 * to interact with a shell application. In a typical test it is required
 * to write into a shell and read what is visible in a shell.
 *
 * @author Janne Valkealahti
 */
public interface ShellTestClient extends Closeable {

	/**
	 * Run interactive shell session.
	 *
	 * @return session for chaining
	 */
	InteractiveShellSession interactive();

	/**
	 * Run non-interactive command session.
	 *
	 * @param args the command arguments
	 * @return session for chaining
	 */
	NonInteractiveShellSession nonInterative(String... args);

	/**
	 * Read the screen.
	 *
	 * @return the screen
	 */
	ShellScreen screen();

	/**
	 * Get an instance of a builder.
	 *
	 * @param terminalSession the terminal session
	 * @param shell the shell
	 * @param promptProvider the prompt provider
	 * @param lineReader the line reader
	 * @param terminal the terminal
	 * @return a Builder
	 */
	public static Builder builder(TerminalSession terminalSession, Shell shell, PromptProvider promptProvider,
			LineReader lineReader, Terminal terminal) {
		return new DefaultBuilder(terminalSession, shell, promptProvider, lineReader, terminal);
	}

	/**
	 * Builder interface for {@code ShellClient}.
	 */
	interface Builder {

		/**
		 * Build a shell client.
		 *
		 * @return a shell client
		 */
		ShellTestClient build();
	}

	interface BaseShellSession<T extends BaseShellSession<T>>  {

		/**
		 * Get a write sequencer.
		 *
		 * @return a write sequencer
		 */
		ShellWriteSequence writeSequence();

		/**
		 * Read the screen.
		 *
		 * @return the screen
		 */
		ShellScreen screen();

		/**
		 * Write plain text into a shell.
		 *
		 * @param text the text
		 * @return client for chaining
		 */
		T write(String text);

		/**
		 * Run a session.
		 *
		 * @return client for chaining
		 */
		T run();

		boolean isComplete();
	}

	interface InteractiveShellSession extends BaseShellSession<InteractiveShellSession> {
	}

	interface NonInteractiveShellSession extends BaseShellSession<NonInteractiveShellSession> {
	}

	static class DefaultBuilder implements Builder {

		private TerminalSession terminalSession;
		private Shell shell;
		private PromptProvider promptProvider;
		private LineReader lineReader;
		private Terminal terminal;

		DefaultBuilder(TerminalSession terminalSession, Shell shell, PromptProvider promptProvider,
				LineReader lineReader, Terminal terminal) {
			this.terminalSession = terminalSession;
			this.shell = shell;
			this.promptProvider = promptProvider;
			this.lineReader = lineReader;
			this.terminal = terminal;
		}

		@Override
		public ShellTestClient build() {
			return new DefaultShellClient(terminalSession, shell, promptProvider, lineReader, terminal);
		}
	}

	static class DefaultShellClient implements ShellTestClient {

		private final static Logger log = LoggerFactory.getLogger(DefaultShellClient.class);
		private TerminalSession terminalSession;
		private Shell shell;
		private PromptProvider promptProvider;
		private LineReader lineReader;
		private Thread runnerThread;
		private Terminal terminal;
		private final BlockingQueue<ShellRunnerTaskData> blockingQueue = new LinkedBlockingDeque<>(10);

		DefaultShellClient(TerminalSession terminalSession, Shell shell, PromptProvider promptProvider,
				LineReader lineReader, Terminal terminal) {
			this.terminalSession = terminalSession;
			this.shell = shell;
			this.promptProvider = promptProvider;
			this.lineReader = lineReader;
			this.terminal = terminal;
		}

		@Override
		public InteractiveShellSession interactive() {
			terminalSession.start();
			if (runnerThread == null) {
				runnerThread = new Thread(new ShellRunnerTask(this.blockingQueue));
				runnerThread.start();
			}
			return new DefaultInteractiveShellSession(shell, promptProvider, lineReader, blockingQueue, terminalSession, terminal);
		}

		@Override
		public NonInteractiveShellSession nonInterative(String... args) {
			terminalSession.start();
			if (runnerThread == null) {
				runnerThread = new Thread(new ShellRunnerTask(this.blockingQueue));
				runnerThread.start();
			}
			return new DefaultNonInteractiveShellSession(shell, args, blockingQueue, terminalSession, terminal);
		}

		@Override
		public ShellScreen screen() {
			return ShellScreen.of(terminalSession.getTerminalTextBuffer().getScreen());
		}

		@Override
		public void close() throws IOException {
			log.debug("Closing ShellClient");
			if (runnerThread != null) {
				runnerThread.interrupt();
			}
			runnerThread = null;
			terminalSession.close();
		}
	}

	static class DefaultInteractiveShellSession implements InteractiveShellSession {

		private Shell shell;
		private PromptProvider promptProvider;
		private LineReader lineReader;
		private BlockingQueue<ShellRunnerTaskData> blockingQueue;
		private TerminalSession terminalSession;
		private Terminal terminal;
		private final AtomicInteger state = new AtomicInteger(-2);

		public DefaultInteractiveShellSession(Shell shell, PromptProvider promptProvider, LineReader lineReader,
				BlockingQueue<ShellRunnerTaskData> blockingQueue, TerminalSession terminalSession, Terminal terminal) {
			this.shell = shell;
			this.promptProvider = promptProvider;
			this.lineReader = lineReader;
			this.blockingQueue = blockingQueue;
			this.terminalSession = terminalSession;
			this.terminal = terminal;
		}

		@Override
		public ShellWriteSequence writeSequence() {
			return ShellWriteSequence.of(terminal);
		}

		@Override
		public InteractiveShellSession write(String text) {
			terminalSession.getTerminalStarter().sendString(text);
			return this;
		}

		@Override
		public ShellScreen screen() {
			return ShellScreen.of(terminalSession.getTerminalTextBuffer().getScreen());
		}

		@Override
		public InteractiveShellSession run() {
			ShellRunner runner = new InteractiveShellRunner(lineReader, promptProvider, shell, new DefaultShellContext());
			ApplicationArguments appArgs = new DefaultApplicationArguments();
			this.blockingQueue.add(new ShellRunnerTaskData(runner, appArgs, state));
			return this;
		}

		@Override
		public boolean isComplete() {
			return state.get() >= 0;
		}
	}

	static class DefaultNonInteractiveShellSession implements NonInteractiveShellSession {

		private Shell shell;
		private String[] args;
		private BlockingQueue<ShellRunnerTaskData> blockingQueue;
		private TerminalSession terminalSession;
		private Terminal terminal;
		private final AtomicInteger state = new AtomicInteger(-2);

		public DefaultNonInteractiveShellSession(Shell shell, String[] args,
				BlockingQueue<ShellRunnerTaskData> blockingQueue, TerminalSession terminalSession, Terminal terminal) {
			this.shell = shell;
			this.args = args;
			this.blockingQueue = blockingQueue;
			this.terminalSession = terminalSession;
			this.terminal = terminal;
		}

		@Override
		public ShellWriteSequence writeSequence() {
			return ShellWriteSequence.of(terminal);
		}

		@Override
		public NonInteractiveShellSession write(String text) {
			terminalSession.getTerminalStarter().sendString(text);
			return this;
		}

		@Override
		public ShellScreen screen() {
			return ShellScreen.of(terminalSession.getTerminalTextBuffer().getScreen());
		}

		@Override
		public NonInteractiveShellSession run() {
			ShellRunner runner = new NonInteractiveShellRunner(shell, new DefaultShellContext());
			ApplicationArguments appArgs = new DefaultApplicationArguments(args);
			this.blockingQueue.add(new ShellRunnerTaskData(runner, appArgs, state));
			return this;
		}

		@Override
		public boolean isComplete() {
			return state.get() >= 0;
		}
	}

	static record ShellRunnerTaskData(
		ShellRunner runner,
		ApplicationArguments args,
		AtomicInteger state
	) {}

	static class ShellRunnerTask implements Runnable {

		private final static Logger log = LoggerFactory.getLogger(ShellRunnerTask.class);
		private BlockingQueue<ShellRunnerTaskData> blockingQueue;

		ShellRunnerTask(BlockingQueue<ShellRunnerTaskData> blockingQueue) {
			this.blockingQueue = blockingQueue;
		}

		@Override
		public void run() {
			log.trace("ShellRunnerTask start");
			try {
				Thread.currentThread().setName("ShellRunnerTask");
				while (true) {
					ShellRunnerTaskData data = blockingQueue.take();
					if (data.runner == null) {
						return;
					}
					try {
						log.trace("Running {}", data.runner());
						data.state().set(-1);
						if (data.runner().canRun(data.args)) {
							data.runner().run(data.args());
						}
						else {
							data.runner().run(data.args().getSourceArgs());
						}
						data.state().set(0);
						log.trace("Running done {}", data.runner());
					} catch (Exception e) {
						data.state().set(1);
						log.trace("ShellRunnerThread ex", e);
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			log.trace("ShellRunnerTask end");
		}
	}
}
