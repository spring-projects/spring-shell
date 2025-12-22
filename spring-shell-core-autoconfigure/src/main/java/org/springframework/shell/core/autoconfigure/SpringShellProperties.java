/*
 * Copyright 2021-present the original author or authors.
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
package org.springframework.shell.core.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Spring Shell.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
@ConfigurationProperties(prefix = "spring.shell")
public class SpringShellProperties {

	private History history = new History();

	private Config config = new Config();

	private Interactive interactive = new Interactive();

	private Theme theme = new Theme();

	private Command command = new Command();

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

	public void setInteractive(Interactive interactive) {
		this.interactive = interactive;
	}

	public Interactive getInteractive() {
		return interactive;
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

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setCommand(Command command) {
		this.command = command;
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

	public static class Interactive {

		private boolean enabled = true;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
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

	public static class Command {

		private HelpCommand help = new HelpCommand();

		private ClearCommand clear = new ClearCommand();

		private ScriptCommand script = new ScriptCommand();

		private HistoryCommand history = new HistoryCommand();

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

		public VersionCommand getVersion() {
			return version;
		}

		public void setVersion(VersionCommand version) {
			this.version = version;
		}

	}

	public static class VersionCommand {

		private boolean enabled = true;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
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

}
