/*
 * Copyright 2011-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.core;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import jline.TerminalFactory;

import org.springframework.shell.TerminalSizeAware;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.event.AbstractShellStatusPublisher;
import org.springframework.shell.event.ParseResult;
import org.springframework.shell.event.ShellStatus;
import org.springframework.shell.event.ShellStatus.Status;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.shell.support.util.VersionUtils;
import org.springframework.util.Assert;

/**
 * Provides a base {@link Shell} implementation.
 *
 * @author Ben Alex
 * @author Gunnar Hillert
 */
public abstract class AbstractShell extends AbstractShellStatusPublisher implements Shell {

	// Constants
	private static final String MY_SLOT = AbstractShell.class.getName();

	//TODO Abstract out to make configurable.
	protected static final String ROO_PROMPT = "spring> ";

	// Public static fields; don't rename, make final, or make non-public, as
	// they are part of the public API, e.g. are changed by STS.
	public static String completionKeys = "TAB";
	public static String shellPrompt = ROO_PROMPT;

	// Instance fields
	protected final Logger logger = HandlerUtils.getLogger(getClass());

	protected final Logger exceptionLogger = Logger.getLogger(getClass().getName() + ".exceptions");

	protected boolean inBlockComment;
	protected ExitShellRequest exitShellRequest;

	protected abstract String getHomeAsString();

	protected abstract ExecutionStrategy getExecutionStrategy();

	protected abstract Parser getParser();


	/**
	 * Execute the single line from a script.
	 * <p>
	 * This method can be overridden by sub-classes to pre-process script lines.
	 */
	public boolean executeScriptLine(final String line) {
		return executeCommand(line).isSuccess();
	}

	public CommandResult executeCommand(String line) {
		// Another command was attempted
		setShellStatus(ShellStatus.Status.PARSING);

		final ExecutionStrategy executionStrategy = getExecutionStrategy();
		boolean flashedMessage = false;
		while (executionStrategy == null || !executionStrategy.isReadyForCommands()) {
			// Wait
			try {
				Thread.sleep(500);
			} catch (InterruptedException ignore) {}
			if (!flashedMessage) {
				flash(Level.INFO, "Please wait - still loading", MY_SLOT);
				flashedMessage = true;
			}
		}
		if (flashedMessage) {
			flash(Level.INFO, "", MY_SLOT);
		}

		ParseResult parseResult = null;
		try {
			// We support simple block comments; ie a single pair per line
			if (!inBlockComment && line.contains("/*") && line.contains("*/")) {
				blockCommentBegin();
				String lhs = line.substring(0, line.lastIndexOf("/*"));
				if (line.contains("*/")) {
					line = lhs + line.substring(line.lastIndexOf("*/") + 2);
					blockCommentFinish();
				} else {
					line = lhs;
				}
			}
			if (inBlockComment) {
				if (!line.contains("*/")) {
					return new CommandResult(true);
				}
				blockCommentFinish();
				line = line.substring(line.lastIndexOf("*/") + 2);
			}
			// We also support inline comments (but only at start of line, otherwise valid
			// command options like http://www.helloworld.com will fail as per ROO-517)
			if (!inBlockComment && (line.trim().startsWith("//") || line.trim().startsWith("#"))) { // # support in ROO-1116
				line = "";
			}
			// Convert any TAB characters to whitespace (ROO-527)
			line = line.replace('\t', ' ');
			if ("".equals(line.trim())) {
				setShellStatus(Status.EXECUTION_SUCCESS);
				return new CommandResult(true);
			}
			parseResult = getParser().parse(line);
			if (parseResult == null) {
				return new CommandResult(false);
			}

			setShellStatus(Status.EXECUTING);
			Object result = executionStrategy.execute(parseResult);
			setShellStatus(Status.EXECUTION_RESULT_PROCESSING);
			if (result != null) {
				if (result instanceof ExitShellRequest) {
					exitShellRequest = (ExitShellRequest) result;
					// Give ProcessManager a chance to close down its threads before the overall OSGi framework is terminated (ROO-1938)
					executionStrategy.terminate();
				} else {
					handleExecutionResult(result);
				}
			}

			logCommandIfRequired(line, true);
			setShellStatus(Status.EXECUTION_SUCCESS, line, parseResult);
			return new CommandResult(true, result, null);
		} catch (RuntimeException e) {
			setShellStatus(Status.EXECUTION_FAILED, line, parseResult);
			exceptionLogger.log(Level.WARNING, e.getMessage(), e);
			// We rely on execution strategy to log it
			try {
				logCommandIfRequired(line, false);
			} catch (Exception ignored) {}
			return new CommandResult(false, null, e);
		} finally {
			setShellStatus(Status.USER_INPUT);
		}
	}

	/**
	 * Allows a subclass to log the execution of a well-formed command. This is invoked after a command
	 * has completed, and indicates whether the command returned normally or returned an exception. Note
	 * that attempted commands that are not well-formed (eg they are missing a mandatory argument) will
	 * never be presented to this method, as the command execution is never actually attempted in those
	 * cases. This method is only invoked if an attempt is made to execute a particular command.
	 *
	 * <p>
	 * Implementations should consider specially handling the "script" commands, and also
	 * indicating whether a command was successful or not. Implementations that wish to behave
	 * consistently with other {@link AbstractShell} subclasses are encouraged to simply override
	 * {@link #logCommandToOutput(String)} instead, and only override this method if you actually
	 * need to fine-tune the output logic.
	 *
	 * @param line the parsed line (any comments have been removed; never null)
	 * @param successful if the command was successful or not
	 */
	protected void logCommandIfRequired(final String line, final boolean successful) {
		if (line.startsWith("script")) {
			logCommandToOutput((successful ? "// " : "// [failed] ") + line);
		} else {
			logCommandToOutput((successful ? "" : "// [failed] ") + line);
		}
	}

	/**
	 * Allows a subclass to actually write the resulting logged command to some form of output. This
	 * frees subclasses from needing to implement the logic within {@link #logCommandIfRequired(String, boolean)}.
	 *
	 * <p>
	 * Implementations should invoke {@link #getExitShellRequest()} to monitor any attempts to exit the shell and
	 * release resources such as output log files.
	 *
	 * @param processedLine the line that should be appended to some type of output (excluding the \n character)
	 */
	protected void logCommandToOutput(final String processedLine) {}

	/**
	 * Base implementation of the {@link Shell#setPromptPath(String)} method, designed for simple shell
	 * implementations. Advanced implementations (eg those that support ANSI codes etc) will likely want
	 * to override this method and set the {@link #shellPrompt} variable directly.
	 *
	 * @param path to set (can be null or empty; must NOT be formatted in any special way eg ANSI codes)
	 */
	public void setPromptPath(final String path) {
		if (path == null || "".equals(path)) {
			shellPrompt = ROO_PROMPT;
		} else {
			shellPrompt = path + " " + ROO_PROMPT;
		}
	}

	/**
	 * Default implementation of {@link Shell#setPromptPath(String, boolean))} method to satisfy STS compatibility.
	 *
	 * @param path to set (can be null or empty)
	 * @param overrideStyle
	 */
	public void setPromptPath(String path, boolean overrideStyle) {
		setPromptPath(path);
	}

	public ExitShellRequest getExitShellRequest() {
		return exitShellRequest;
	}

	@CliCommand(value = { "/*" }, help = "Start of block comment")
	public void blockCommentBegin() {
		Assert.isTrue(!inBlockComment, "Cannot open a new block comment when one already active");
		inBlockComment = true;
	}

	@CliCommand(value = { "*/" }, help = "End of block comment")
	public void blockCommentFinish() {
		Assert.isTrue(inBlockComment, "Cannot close a block comment when it has not been opened");
		inBlockComment = false;
	}

	public String versionInfo(){
		return VersionUtils.versionInfo();
	}

	public String getShellPrompt() {
		return shellPrompt;
	}

	/**
	 * Obtains the home directory for the current shell instance.
	 *
	 * <p>
	 * Note: calls the {@link #getHomeAsString()} method to allow subclasses to provide the home directory location as
	 * string using different environment-specific strategies.
 	 *
	 * <p>
	 * If the path indicated by {@link #getHomeAsString()} exists and refers to a directory, that directory
	 * is returned.
	 *
	 * <p>
	 * If the path indicated by {@link #getHomeAsString()} exists and refers to a file, an exception is thrown.
	 *
	 * <p>
	 * If the path indicated by {@link #getHomeAsString()} does not exist, it will be created as a directory.
	 * If this fails, an exception will be thrown.
	 *
	 * @return the home directory for the current shell instance (which is guaranteed to exist and be a directory)
	 */
	public File getHome() {
		String rooHome = getHomeAsString();
		File f = new File(rooHome);
		Assert.isTrue(!f.exists() || (f.exists() && f.isDirectory()), "Path '" + f.getAbsolutePath() + "' must be a directory, or it must not exist");
		if (!f.exists()) {
			f.mkdirs();
		}
		Assert.isTrue(f.exists() && f.isDirectory(), "Path '" + f.getAbsolutePath() + "' is not a directory; please specify roo.home system property correctly");
		return f;
	}

	/**
	 * Simple implementation of {@link #flash(Level, String, String)} that simply displays the message via the logger. It is
	 * strongly recommended shell implementations override this method with a more effective approach.
	 */
	public void flash(final Level level, final String message, final String slot) {
		Assert.notNull(level, "Level is required for a flash message");
		Assert.notNull(message, "Message is required for a flash message");
		Assert.hasText(slot, "Slot name must be specified for a flash message");
		if (!("".equals(message))) {
			logger.log(level, message);
		}
	}

	/**
	 * Handles the result of execution of a command. Given <i>result</i> is
	 * expected to be not <code>null</code>. If <i>result</i> is a
	 * {@link java.lang.Iterable} object, it will be iterated through to print
	 * the output of <i>toString</i>. For other type of objects, simply output
	 * of <i>toString</i> is shown. Subclasses can alter this implementation
	 * to handle the <i>result</i> differently.
	 *
	 * @param result not <code>null</code> result of execution of a command.
	 */
	protected void handleExecutionResult(Object result) {
		if (result instanceof Iterable<?>) {
			for (Object o : (Iterable<?>) result) {
				handleExecutionResult(o);
			}
		} else if (result instanceof TerminalSizeAware) {
			int width = TerminalFactory.get().getWidth();
			logger.info(((TerminalSizeAware) result).render(width).toString());
		} else {
			logger.info(result.toString());
		}
	}
}
