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

import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.shell.boot.SpringShellProperties.OptionNamingCase;
import org.springframework.shell.boot.SpringShellProperties.HelpCommand.GroupingMode;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringShellPropertiesTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

	@Test
	public void defaultNoPropertiesSet() {
		this.contextRunner
				.withUserConfiguration(Config1.class)
				.run((context) -> {
					SpringShellProperties properties = context.getBean(SpringShellProperties.class);
					assertThat(properties.getHistory().isEnabled()).isTrue();
					assertThat(properties.getHistory().getName()).isNull();
					assertThat(properties.getConfig().getLocation()).isNull();
					assertThat(properties.getConfig().getEnv()).isNull();
					assertThat(properties.getScript().isEnabled()).isFalse();
					assertThat(properties.getInteractive().isEnabled()).isFalse();
					assertThat(properties.getNoninteractive().isEnabled()).isTrue();
					assertThat(properties.getNoninteractive().getPrimaryCommand()).isNull();
					assertThat(properties.getTheme().getName()).isNull();
					assertThat(properties.getCommand().getClear().isEnabled()).isTrue();
					assertThat(properties.getCommand().getHelp().isEnabled()).isTrue();
					assertThat(properties.getCommand().getHelp().getGroupingMode()).isEqualTo(GroupingMode.GROUP);
					assertThat(properties.getCommand().getHelp().getCommandTemplate()).isNotNull();
					assertThat(properties.getCommand().getHelp().getCommandsTemplate()).isNotNull();
					assertThat(properties.getCommand().getHistory().isEnabled()).isTrue();
					assertThat(properties.getCommand().getQuit().isEnabled()).isTrue();
					assertThat(properties.getCommand().getScript().isEnabled()).isTrue();
					assertThat(properties.getCommand().getStacktrace().isEnabled()).isTrue();
					assertThat(properties.getCommand().getCompletion().isEnabled()).isTrue();
					assertThat(properties.getCommand().getCompletion().getRootCommand()).isNull();
					assertThat(properties.getCommand().getVersion().isEnabled()).isTrue();
					assertThat(properties.getCommand().getVersion().getTemplate()).isNotNull();
					assertThat(properties.getCommand().getVersion().isShowBuildArtifact()).isFalse();
					assertThat(properties.getCommand().getVersion().isShowBuildGroup()).isFalse();
					assertThat(properties.getCommand().getVersion().isShowBuildName()).isFalse();
					assertThat(properties.getCommand().getVersion().isShowBuildTime()).isFalse();
					assertThat(properties.getCommand().getVersion().isShowBuildVersion()).isTrue();
					assertThat(properties.getCommand().getVersion().isShowGitBranch()).isFalse();
					assertThat(properties.getCommand().getVersion().isShowGitCommitId()).isFalse();
					assertThat(properties.getCommand().getVersion().isShowGitShortCommitId()).isFalse();
					assertThat(properties.getCommand().getVersion().isShowGitCommitTime()).isFalse();
					assertThat(properties.getHelp().isEnabled()).isTrue();
					assertThat(properties.getHelp().getCommand()).isEqualTo("help");
					assertThat(properties.getHelp().getLongNames()).containsExactly("help");
					assertThat(properties.getHelp().getShortNames()).containsExactly('h');
					assertThat(properties.getOption().getNaming().getCaseType()).isEqualTo(OptionNamingCase.NOOP);
					assertThat(properties.getContext().isClose()).isFalse();
				});
	}

	@Test
	public void setProperties() {
		this.contextRunner
				.withPropertyValues("spring.shell.history.enabled=false")
				.withPropertyValues("spring.shell.history.name=fakename")
				.withPropertyValues("spring.shell.config.location=fakelocation")
				.withPropertyValues("spring.shell.config.env=FAKE_ENV")
				.withPropertyValues("spring.shell.script.enabled=true")
				.withPropertyValues("spring.shell.interactive.enabled=true")
				.withPropertyValues("spring.shell.noninteractive.enabled=false")
				.withPropertyValues("spring.shell.noninteractive.primary-command=fakecommand")
				.withPropertyValues("spring.shell.theme.name=fake")
				.withPropertyValues("spring.shell.command.clear.enabled=false")
				.withPropertyValues("spring.shell.command.help.enabled=false")
				.withPropertyValues("spring.shell.command.help.grouping-mode=flat")
				.withPropertyValues("spring.shell.command.help.command-template=fake1")
				.withPropertyValues("spring.shell.command.help.commands-template=fake2")
				.withPropertyValues("spring.shell.command.history.enabled=false")
				.withPropertyValues("spring.shell.command.quit.enabled=false")
				.withPropertyValues("spring.shell.command.script.enabled=false")
				.withPropertyValues("spring.shell.command.stacktrace.enabled=false")
				.withPropertyValues("spring.shell.command.completion.enabled=false")
				.withPropertyValues("spring.shell.command.completion.root-command=fake")
				.withPropertyValues("spring.shell.command.version.enabled=false")
				.withPropertyValues("spring.shell.command.version.template=fake")
				.withPropertyValues("spring.shell.command.version.show-build-artifact=true")
				.withPropertyValues("spring.shell.command.version.show-build-group=true")
				.withPropertyValues("spring.shell.command.version.show-build-name=true")
				.withPropertyValues("spring.shell.command.version.show-build-time=true")
				.withPropertyValues("spring.shell.command.version.show-build-version=false")
				.withPropertyValues("spring.shell.command.version.show-git-branch=true")
				.withPropertyValues("spring.shell.command.version.show-git-commit-id=true")
				.withPropertyValues("spring.shell.command.version.show-git-short-commit-id=true")
				.withPropertyValues("spring.shell.command.version.show-git-commit-time=true")
				.withPropertyValues("spring.shell.help.enabled=false")
				.withPropertyValues("spring.shell.help.command=fake")
				.withPropertyValues("spring.shell.help.long-names=fake")
				.withPropertyValues("spring.shell.help.short-names=f")
				.withPropertyValues("spring.shell.option.naming.case-type=camel")
				.withPropertyValues("spring.shell.context.close=true")
				.withUserConfiguration(Config1.class)
				.run((context) -> {
					SpringShellProperties properties = context.getBean(SpringShellProperties.class);
					assertThat(properties.getHistory().isEnabled()).isFalse();
					assertThat(properties.getHistory().getName()).isEqualTo("fakename");
					assertThat(properties.getConfig().getLocation()).isEqualTo("fakelocation");
					assertThat(properties.getConfig().getEnv()).isEqualTo("FAKE_ENV");
					assertThat(properties.getScript().isEnabled()).isTrue();
					assertThat(properties.getInteractive().isEnabled()).isTrue();
					assertThat(properties.getNoninteractive().isEnabled()).isFalse();
					assertThat(properties.getNoninteractive().getPrimaryCommand()).isEqualTo("fakecommand");
					assertThat(properties.getTheme().getName()).isEqualTo("fake");
					assertThat(properties.getCommand().getClear().isEnabled()).isFalse();
					assertThat(properties.getCommand().getHelp().isEnabled()).isFalse();
					assertThat(properties.getCommand().getHelp().getGroupingMode()).isEqualTo(GroupingMode.FLAT);
					assertThat(properties.getCommand().getHelp().getCommandTemplate()).isEqualTo("fake1");
					assertThat(properties.getCommand().getHelp().getCommandsTemplate()).isEqualTo("fake2");
					assertThat(properties.getCommand().getHistory().isEnabled()).isFalse();
					assertThat(properties.getCommand().getQuit().isEnabled()).isFalse();
					assertThat(properties.getCommand().getScript().isEnabled()).isFalse();
					assertThat(properties.getCommand().getStacktrace().isEnabled()).isFalse();
					assertThat(properties.getCommand().getCompletion().isEnabled()).isFalse();
					assertThat(properties.getCommand().getCompletion().getRootCommand()).isEqualTo("fake");
					assertThat(properties.getCommand().getVersion().isEnabled()).isFalse();
					assertThat(properties.getCommand().getVersion().getTemplate()).isEqualTo("fake");
					assertThat(properties.getCommand().getVersion().isShowBuildArtifact()).isTrue();
					assertThat(properties.getCommand().getVersion().isShowBuildGroup()).isTrue();
					assertThat(properties.getCommand().getVersion().isShowBuildName()).isTrue();
					assertThat(properties.getCommand().getVersion().isShowBuildTime()).isTrue();
					assertThat(properties.getCommand().getVersion().isShowBuildVersion()).isFalse();
					assertThat(properties.getCommand().getVersion().isShowGitBranch()).isTrue();
					assertThat(properties.getCommand().getVersion().isShowGitCommitId()).isTrue();
					assertThat(properties.getCommand().getVersion().isShowGitShortCommitId()).isTrue();
					assertThat(properties.getCommand().getVersion().isShowGitCommitTime()).isTrue();
					assertThat(properties.getHelp().isEnabled()).isFalse();
					assertThat(properties.getHelp().getCommand()).isEqualTo("fake");
					assertThat(properties.getHelp().getLongNames()).containsExactly("fake");
					assertThat(properties.getHelp().getShortNames()).containsExactly('f');
					assertThat(properties.getOption().getNaming().getCaseType()).isEqualTo(OptionNamingCase.CAMEL);
					assertThat(properties.getContext().isClose()).isTrue();
				});
	}


	@Test
	public void essentiallyUnset() {
		this.contextRunner
				.withPropertyValues("spring.shell.help.long-names=")
				.withPropertyValues("spring.shell.help.short-names=")
				.withUserConfiguration(Config1.class)
				.run((context) -> {
					SpringShellProperties properties = context.getBean(SpringShellProperties.class);
					assertThat(properties.getHelp().getLongNames()).isEmpty();
					assertThat(properties.getHelp().getShortNames()).isEmpty();
				});
	}

	@EnableConfigurationProperties({ SpringShellProperties.class })
	private static class Config1 {
	}
}
