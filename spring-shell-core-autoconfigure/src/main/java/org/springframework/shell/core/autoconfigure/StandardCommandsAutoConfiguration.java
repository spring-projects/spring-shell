/*
 * Copyright 2017-present the original author or authors.
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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.*;

/**
 * Creates beans for standard commands.
 *
 * @author Eric Bottard
 * @author Mahmoud Ben Hassine
 * @author Piotr Olaszewski
 */
@AutoConfiguration
@EnableConfigurationProperties(SpringShellProperties.class)
public class StandardCommandsAutoConfiguration {

	@Bean
	@ConditionalOnProperty(value = "spring.shell.command.help.enabled", havingValue = "true", matchIfMissing = true)
	public Command helpCommand() {
		return new Help();
	}

	@Bean
	@ConditionalOnProperty(value = "spring.shell.command.clear.enabled", havingValue = "true", matchIfMissing = true)
	public Command clearCommand() {
		return new Clear();
	}

	@Bean
	@ConditionalOnProperty(value = "spring.shell.command.version.enabled", havingValue = "true", matchIfMissing = true)
	public Command versionCommand(SpringShellProperties shellProperties,
			ObjectProvider<BuildProperties> buildProperties, ObjectProvider<GitProperties> gitProperties) {
		SpringShellProperties.VersionCommand properties = shellProperties.getCommand().getVersion();
		Version version = new Version();
		version.setShowBuildGroup(properties.isShowBuildGroup());
		version.setShowBuildArtifact(properties.isShowBuildArtifact());
		version.setShowBuildName(properties.isShowBuildName());
		version.setShowBuildVersion(properties.isShowBuildVersion());
		version.setShowBuildTime(properties.isShowBuildTime());
		version.setShowGitBranch(properties.isShowGitBranch());
		version.setShowGitCommitId(properties.isShowGitCommitId());
		version.setShowGitShortCommitId(properties.isShowGitShortCommitId());
		version.setShowGitCommitTime(properties.isShowGitCommitTime());

		buildProperties.ifAvailable(props -> version.setBuildProperties(new Version.BuildProperties(props.getGroup(),
				props.getArtifact(), props.getName(), props.getVersion(), props.getTime())));

		gitProperties.ifAvailable(props -> version.setGitProperties(new Version.GitProperties(props.getBranch(),
				props.getCommitId(), props.getShortCommitId(), props.getCommitTime())));

		return version;
	}

	@Bean
	@ConditionalOnProperty(value = "spring.shell.command.script.enabled", havingValue = "true", matchIfMissing = true)
	public Command scriptCommand() {
		return new Script();
	}

}
