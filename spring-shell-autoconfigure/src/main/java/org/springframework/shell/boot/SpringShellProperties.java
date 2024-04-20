/*
 * Copyright 2021-2024 the original author or authors.
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

	private History history = new History();
	private Config config = new Config();
	private Script script = new Script();
	private Interactive interactive = new Interactive();
	private Noninteractive noninteractive = new Noninteractive();
	private Theme theme = new Theme();
	private Command command = new Command();
	private Help help = new Help();
	private Option option = new Option();
	private Context context = new Context();

	public void setConfig(Config config) {
		this.config = config;
	}

	public Config getConfig() {
		return config;
	}

	public History getHistory() {
		return history;
	}

	public void setHistory(History history) {
		this.history = history;
	}

	public Script getScript() {
		return script;
	}

	public void setScript(Script script) {
		this.script = script;
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

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public void setHelp(Help help) {
		this.help = help;
	}

	public Help getHelp() {
		return help;
	}

	public Option getOption() {
		return option;
	}

	public void setOption(Option option) {
		this.option = option;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public static class Config {

		private String env;
		private String location;

		public String getEnv() {
			return env;
		}

		public void setEnv(String env) {
			this.env = env;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}
	}

	public static class History {

		private String name;
		private boolean enabled = true;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class Script {

		private boolean enabled = false;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class Interactive {

		private boolean enabled = false;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class Noninteractive {

		private boolean enabled = true;
		private String primaryCommand;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getPrimaryCommand() {
			return primaryCommand;
		}

		public void setPrimaryCommand(String primaryCommand) {
			this.primaryCommand = primaryCommand;
		}
	}

	public static class Theme {

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class HelpCommand {

		private boolean enabled = true;
		private String commandTemplate = "classpath:template/help-command-default.stg";
		private String commandsTemplate = "classpath:template/help-commands-default.stg";
		private GroupingMode groupingMode = GroupingMode.GROUP;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getCommandTemplate() {
			return commandTemplate;
		}

		public void setCommandTemplate(String commandTemplate) {
			this.commandTemplate = commandTemplate;
		}

		public String getCommandsTemplate() {
			return commandsTemplate;
		}

		public void setCommandsTemplate(String commandsTemplate) {
			this.commandsTemplate = commandsTemplate;
		}

		public GroupingMode getGroupingMode() {
			return groupingMode;
		}

		public void setGroupingMode(GroupingMode groupingMode) {
			this.groupingMode = groupingMode;
		}

		public enum GroupingMode {
			GROUP,
			FLAT
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
		private VersionCommand version = new VersionCommand();

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

		public VersionCommand getVersion() {
			return version;
		}

		public void setVersion(VersionCommand version) {
			this.version = version;
		}
	}

	public static class VersionCommand {

		private boolean enabled = true;
		private String template = "classpath:template/version-default.st";
		private boolean showBuildGroup = false;
		private boolean showBuildArtifact = false;
		private boolean showBuildName = false;
		private boolean showBuildVersion = true;
		private boolean showBuildTime = false;
		private boolean showGitBranch = false;
		private boolean showGitCommitId = false;
		private boolean showGitShortCommitId = false;
		private boolean showGitCommitTime = false;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getTemplate() {
			return template;
		}

		public void setTemplate(String template) {
			this.template = template;
		}

		public boolean isShowBuildGroup() {
			return showBuildGroup;
		}

		public void setShowBuildGroup(boolean showBuildGroup) {
			this.showBuildGroup = showBuildGroup;
		}

		public boolean isShowBuildArtifact() {
			return showBuildArtifact;
		}

		public void setShowBuildArtifact(boolean showBuildArtifact) {
			this.showBuildArtifact = showBuildArtifact;
		}

		public boolean isShowBuildName() {
			return showBuildName;
		}

		public void setShowBuildName(boolean showBuildName) {
			this.showBuildName = showBuildName;
		}

		public boolean isShowBuildVersion() {
			return showBuildVersion;
		}

		public void setShowBuildVersion(boolean showBuildVersion) {
			this.showBuildVersion = showBuildVersion;
		}

		public boolean isShowBuildTime() {
			return showBuildTime;
		}

		public void setShowBuildTime(boolean showBuildTime) {
			this.showBuildTime = showBuildTime;
		}

		public boolean isShowGitBranch() {
			return showGitBranch;
		}

		public void setShowGitBranch(boolean showGitBranch) {
			this.showGitBranch = showGitBranch;
		}

		public boolean isShowGitCommitId() {
			return showGitCommitId;
		}

		public void setShowGitCommitId(boolean showGitCommitId) {
			this.showGitCommitId = showGitCommitId;
		}

		public boolean isShowGitShortCommitId() {
			return showGitShortCommitId;
		}

		public void setShowGitShortCommitId(boolean showGitShortCommitId) {
			this.showGitShortCommitId = showGitShortCommitId;
		}

		public boolean isShowGitCommitTime() {
			return showGitCommitTime;
		}

		public void setShowGitCommitTime(boolean showGitCommitTime) {
			this.showGitCommitTime = showGitCommitTime;
		}
	}

	public static class Help {

		/**
		 * Command to call when presense of help option is detected.
		 */
		private String command = "help";

		/**
		 * Long style help option, without a prefix "--".
		 */
		private String[] longNames = new String[] { "help" };

		/**
		 * Short style help option, without a prefix "-".
		 */
		private Character[] shortNames = new Character[] { 'h' };

		/**
		 * Whether to enable help options for commands.
		 */
		private boolean enabled = true;

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		public String[] getLongNames() {
			return longNames;
		}

		public void setLongNames(String[] longNames) {
			this.longNames = longNames;
		}

		public Character[] getShortNames() {
			return shortNames;
		}

		public void setShortNames(Character[] shortNames) {
			this.shortNames = shortNames;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static class Option {

		private OptionNaming naming = new OptionNaming();

		public OptionNaming getNaming() {
			return naming;
		}

		public void setNaming(OptionNaming naming) {
			this.naming = naming;
		}
	}

	public static class OptionNaming {
		private OptionNamingCase caseType = OptionNamingCase.NOOP;

		public OptionNamingCase getCaseType() {
			return caseType;
		}

		public void setCaseType(OptionNamingCase caseType) {
			this.caseType = caseType;
		}
	}

	public static class Context {

		private boolean close = false;

		public boolean isClose() {
			return close;
		}

		public void setClose(boolean close) {
			this.close = close;
		}
	}

	public static enum OptionNamingCase {
		NOOP,
		CAMEL,
		SNAKE,
		KEBAB,
		PASCAL
	}

}
