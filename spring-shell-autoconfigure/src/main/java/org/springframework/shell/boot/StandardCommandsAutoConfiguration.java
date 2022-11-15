/*
 * Copyright 2017-2022 the original author or authors.
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

import org.jline.reader.Parser;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.shell.boot.SpringShellProperties.HelpCommand.GroupingMode;
import org.springframework.shell.boot.SpringShellProperties.VersionCommand;
import org.springframework.shell.boot.condition.OnCompletionCommandCondition;
import org.springframework.shell.result.ThrowableResultHandler;
import org.springframework.shell.standard.commands.Clear;
import org.springframework.shell.standard.commands.Completion;
import org.springframework.shell.standard.commands.Help;
import org.springframework.shell.standard.commands.History;
import org.springframework.shell.standard.commands.Quit;
import org.springframework.shell.standard.commands.Script;
import org.springframework.shell.standard.commands.Stacktrace;
import org.springframework.shell.standard.commands.Version;
import org.springframework.shell.style.TemplateExecutor;

/**
 * Creates beans for standard commands.
 *
 * @author Eric Bottard
 */
@AutoConfiguration
@ConditionalOnClass({ Help.Command.class })
@EnableConfigurationProperties(SpringShellProperties.class)
public class StandardCommandsAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(Help.Command.class)
	@ConditionalOnProperty(prefix = "spring.shell.command.help", value = "enabled", havingValue = "true", matchIfMissing = true)
	public Help help(SpringShellProperties properties, ObjectProvider<TemplateExecutor> templateExecutor) {
		Help help = new Help(templateExecutor.getIfAvailable());
		if (properties.getCommand().getHelp().getGroupingMode() == GroupingMode.FLAT) {
			help.setShowGroups(false);
		}
		help.setCommandTemplate(properties.getCommand().getHelp().getCommandTemplate());
		help.setCommandsTemplate(properties.getCommand().getHelp().getCommandsTemplate());
		return help;
	}

	@Bean
	@ConditionalOnMissingBean(Clear.Command.class)
	@ConditionalOnProperty(prefix = "spring.shell.command.clear", value = "enabled", havingValue = "true", matchIfMissing = true)
	public Clear clear() {
		return new Clear();
	}

	@Bean
	@ConditionalOnMissingBean(Quit.Command.class)
	@ConditionalOnProperty(prefix = "spring.shell.command.quit", value = "enabled", havingValue = "true", matchIfMissing = true)
	public Quit quit() {
		return new Quit();
	}

	@Bean
	@ConditionalOnMissingBean(Stacktrace.Command.class)
	@ConditionalOnProperty(prefix = "spring.shell.command.stacktrace", value = "enabled", havingValue = "true", matchIfMissing = true)
	public Stacktrace stacktrace(ObjectProvider<ThrowableResultHandler> throwableResultHandler) {
		return new Stacktrace(throwableResultHandler);
	}

	@Bean
	@ConditionalOnMissingBean(Script.Command.class)
	@ConditionalOnProperty(prefix = "spring.shell.command.script", value = "enabled", havingValue = "true", matchIfMissing = true)
	public Script script(Parser parser) {
		return new Script(parser);
	}

	@Bean
	@ConditionalOnMissingBean(History.Command.class)
	@ConditionalOnProperty(prefix = "spring.shell.command.history", value = "enabled", havingValue = "true", matchIfMissing = true)
	public History historyCommand(org.jline.reader.History jLineHistory) {
		return new History(jLineHistory);
	}

	@Bean
	@ConditionalOnMissingBean(Completion.Command.class)
	@Conditional(OnCompletionCommandCondition.class)
	public Completion completion(SpringShellProperties properties) {
		return new Completion(properties.getCommand().getCompletion().getRootCommand());
	}

	@Bean
	@ConditionalOnMissingBean(Version.Command.class)
	@ConditionalOnProperty(prefix = "spring.shell.command.version", value = "enabled", havingValue = "true", matchIfMissing = true)
	public Version version(SpringShellProperties properties, ObjectProvider<BuildProperties> buildProperties,
			ObjectProvider<GitProperties> gitProperties, ObjectProvider<TemplateExecutor> templateExecutor) {
		Version version = new Version(templateExecutor.getIfAvailable());
		version.setBuildProperties(buildProperties.getIfAvailable());
		version.setGitProperties(gitProperties.getIfAvailable());
		VersionCommand versionProperties = properties.getCommand().getVersion();
		version.setTemplate(versionProperties.getTemplate());
		version.setShowBuildArtifact(versionProperties.isShowBuildArtifact());
		version.setShowBuildGroup(versionProperties.isShowBuildGroup());
		version.setShowBuildName(versionProperties.isShowBuildName());
		version.setShowBuildTime(versionProperties.isShowBuildTime());
		version.setShowBuildVersion(versionProperties.isShowBuildVersion());
		version.setShowGitBranch(versionProperties.isShowGitBranch());
		version.setShowGitCommitId(versionProperties.isShowGitCommitId());
		version.setShowGitShortCommitId(versionProperties.isShowGitShortCommitId());
		version.setShowGitCommitTime(versionProperties.isShowGitCommitTime());
		return version;
	}
}
