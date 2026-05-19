/*
 * Copyright 2025-present the original author or authors.
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
package org.springframework.shell.core;

import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandExecutionException;
import org.springframework.shell.core.command.CommandExecutor;
import org.springframework.shell.core.command.CommandNotFoundException;
import org.springframework.shell.core.command.CommandParser;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.ParsedInput;
import org.springframework.shell.core.utils.Utils;

/**
 * Base class for interactive shell runners. Implementations must provide concrete methods
 * to print messages and flush the output.
 *
 * @author Mahmoud Ben Hassine
 * @author David Pilar
 * @since 4.0.0
 */
public abstract class InteractiveShellRunner implements ShellRunner, DisposableBean {

	private static final Log log = LogFactory.getLog(InteractiveShellRunner.class);

	private static final long STOP_JOIN_TIMEOUT_MS = 2000L;

	private final CommandParser commandParser;

	private final CommandExecutor commandExecutor;

	private final CommandRegistry commandRegistry;

	private final InputProvider inputProvider;

	private boolean debugMode = false;

	private volatile boolean running = true;

	private volatile Thread runnerThread;

	/**
	 * Create a new {@link InteractiveShellRunner} instance.
	 * @param inputProvider the input provider
	 * @param commandParser the command parser
	 * @param commandRegistry the command registry
	 */
	public InteractiveShellRunner(InputProvider inputProvider, CommandParser commandParser,
			CommandRegistry commandRegistry) {
		this.commandParser = commandParser;
		this.commandRegistry = commandRegistry;
		this.inputProvider = inputProvider;
		this.commandExecutor = new CommandExecutor(commandRegistry);
	}

	@Override
	public void run(String[] args) throws Exception {
		if (args.length != 0) {
			log.warn("Running in interactive mode, arguments will be ignored");
		}
		this.runnerThread = Thread.currentThread();
		try {
			doRun();
		}
		finally {
			this.runnerThread = null;
		}
	}

	private void doRun() {
		while (this.running) {
			String input;
			try {
				input = this.inputProvider.readInput();
				if (input == null || input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
					print("Exiting the shell");
					break;
				}
				if (input.isEmpty()) {
					// ignore empty lines
					continue;
				}
			}
			catch (Exception e) {
				if (this.running && this.debugMode) {
					e.printStackTrace();
				}
				break;
			}
			finally {
				flush();
			}
			ParsedInput parsedInput;
			try {
				parsedInput = this.commandParser.parse(input);
			}
			catch (Exception exception) {
				print("Error while parsing command: " + exception.getMessage());
				if (this.debugMode) {
					exception.printStackTrace();
				}
				continue;
			}
			try {
				CommandContext commandContext = new CommandContext(parsedInput, this.commandRegistry, getWriter(),
						getReader());
				ExitStatus exitStatus = this.commandExecutor.execute(commandContext);
				if (ExitStatus.OK.code() != exitStatus.code()) { // business error
					print("Error while executing command " + parsedInput.commandName() + ": "
							+ exitStatus.description());
				}
			}
			catch (CommandExecutionException executionException) { // technical error
				// traverse exception causes to find root cause
				Throwable cause = executionException.getCause();
				while (cause != null && cause.getCause() != null) {
					cause = cause.getCause();
				}
				String errorMessage = "Unable to run command " + parsedInput.commandName()
						+ String.join(" ", parsedInput.subCommands());
				if (cause != null && cause.getMessage() != null) {
					errorMessage += ": " + cause.getMessage();
				}
				print(errorMessage);
				if (this.debugMode) {
					executionException.printStackTrace();
				}
			}
			catch (CommandNotFoundException exception) {
				print(String.format("Command %s not found", exception.getCommandName()));
				print(Utils.formatAvailableCommands(this.commandRegistry));
			}
			finally {
				flush();
			}
		}
	}

	/**
	 * Print a message to the output.
	 * @param message the message to print
	 */
	public abstract void print(String message);

	/**
	 * Flush the output.
	 */
	public abstract void flush();

	/**
	 * Get the writer to the output.
	 * @return the print writer
	 */
	public abstract PrintWriter getWriter();

	/**
	 * Get the input reader.
	 * @return the input reader
	 */
	public abstract InputReader getReader();

	/**
	 * Set debug mode.
	 * @param debugMode true to enable debug mode, false otherwise
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	/**
	 * Signal the runner to stop and wait briefly for its thread to exit.
	 */
	public void stop() {
		if (!this.running) {
			return;
		}
		this.running = false;
		try {
			wakeup();
		}
		catch (Exception ex) {
			if (log.isDebugEnabled()) {
				log.debug("Failed to wake up shell runner", ex);
			}
		}
		Thread thread = this.runnerThread;
		if (thread != null && thread != Thread.currentThread()) {
			try {
				thread.join(STOP_JOIN_TIMEOUT_MS);
			}
			catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Unblock a parked {@link InputProvider#readInput()} so {@link #stop()} can return.
	 */
	protected void wakeup() {
	}

	@Override
	public void destroy() {
		stop();
	}

}
