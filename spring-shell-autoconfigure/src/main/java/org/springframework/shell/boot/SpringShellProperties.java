/*
 * Copyright 2021-2022 the original author or authors.
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
package org.springframework.shell.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for shell.
 *
 * @author Janne Valkealahti
 */
@ConfigurationProperties(prefix = "spring.shell")
public class SpringShellProperties {

	private Script script = new Script();
	private Interactive interactive = new Interactive();
	private Noninteractive noninteractive = new Noninteractive();
	private Command command = new Command();

	public void setScript(Script script) {
		this.script = script;
	}

	public Script getScript() {
		return script;
	}

	public void setInteractive(Interactive interactive) {
		this.interactive = interactive;
	}

	public Interactive getInteractive() {
		return interactive;
	}

	public Noninteractive getNoninteractive() {
		return noninteractive;
	}

	public void setNoninteractive(Noninteractive noninteractive) {
		this.noninteractive = noninteractive;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public static class Script {

		private boolean enabled = true;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class Interactive {

		private boolean enabled = true;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class Noninteractive {

		private boolean enabled = true;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class HelpCommand {

		private boolean enabled = true;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class ClearCommand {

		private boolean enabled = true;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class QuitCommand {

		private boolean enabled = true;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class StacktraceCommand {

		private boolean enabled = true;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class ScriptCommand {

		private boolean enabled = true;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class HistoryCommand {

		private boolean enabled = true;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class CompletionCommand {

		private boolean enabled = true;
		private String rootCommand;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getRootCommand() {
			return rootCommand;
		}

		public void setRootCommand(String rootCommand) {
			this.rootCommand = rootCommand;
		}
	}

	public static class Command {

		private HelpCommand help = new HelpCommand();
		private ClearCommand clear = new ClearCommand();
		private QuitCommand quit = new QuitCommand();
		private StacktraceCommand stacktrace = new StacktraceCommand();
		private ScriptCommand script = new ScriptCommand();
		private HistoryCommand history = new HistoryCommand();
		private CompletionCommand completion = new CompletionCommand();

		public void setHelp(HelpCommand help) {
			this.help = help;
		}

		public HelpCommand getHelp() {
			return help;
		}

		public ClearCommand getClear() {
			return clear;
		}

		public void setClear(ClearCommand clear) {
			this.clear = clear;
		}

		public QuitCommand getQuit() {
			return quit;
		}

		public void setQuit(QuitCommand quit) {
			this.quit = quit;
		}

		public StacktraceCommand getStacktrace() {
			return stacktrace;
		}

		public void setStacktrace(StacktraceCommand stacktrace) {
			this.stacktrace = stacktrace;
		}

		public ScriptCommand getScript() {
			return script;
		}

		public void setScript(ScriptCommand script) {
			this.script = script;
		}

		public HistoryCommand getHistory() {
			return history;
		}

		public void setHistory(HistoryCommand history) {
			this.history = history;
		}

		public CompletionCommand getCompletion() {
			return completion;
		}

		public void setCompletion(CompletionCommand completion) {
			this.completion = completion;
		}
	}
}
