/*
 * Copyright 2022-present the original author or authors.
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
package org.springframework.shell.core.command;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.time.Instant;

/**
 * Command to print the current version of Spring Shell.
 *
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 * @author Piotr Olaszewski
 * @author David Pilar
 */
public class Version implements Command, InitializingBean, ApplicationContextAware {

	public record BuildProperties(@Nullable String group, @Nullable String artifact, @Nullable String name,
			@Nullable String version, @Nullable Instant time) {
	}

	public record GitProperties(@Nullable String branch, @Nullable String commitId, @Nullable String shortCommitId,
			@Nullable Instant commitTime) {
	}

	private ApplicationContext applicationContext;

	private @Nullable BuildProperties buildProperties;

	private @Nullable GitProperties gitProperties;

	public void setBuildProperties(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	public void setGitProperties(GitProperties gitProperties) {
		this.gitProperties = gitProperties;
	}

	@Override
	public String getDescription() {
		return "Show version info";
	}

	@Override
	public String getGroup() {
		return "Built-In Commands";
	}

	@Override
	public ExitStatus execute(CommandContext commandContext) throws Exception {
		Package pkg = Version.class.getPackage();
		String version = "N/A";
		if (buildProperties != null && StringUtils.hasText(buildProperties.version())) {
			version = buildProperties.version();
		}
		else if (pkg != null && pkg.getImplementationVersion() != null) {
			version = pkg.getImplementationVersion();
		}
		PrintWriter printWriter = commandContext.outputWriter();
		printWriter.println("Version: " + version);

		if (buildProperties != null) {
			if (StringUtils.hasText(buildProperties.group())) {
				printWriter.println("Build Group: " + buildProperties.group());
			}
			if (StringUtils.hasText(buildProperties.artifact())) {
				printWriter.println("Build Artifact: " + buildProperties.artifact());
			}
			if (StringUtils.hasText(buildProperties.name())) {
				printWriter.println("Build Name: " + buildProperties.name());
			}
			if (buildProperties.time() != null) {
				printWriter.println("Build Time: " + buildProperties.time());
			}
		}
		if (gitProperties != null) {
			if (StringUtils.hasText(gitProperties.shortCommitId())) {
				printWriter.println("Git Short Commit ID: " + gitProperties.shortCommitId());
			}
			if (StringUtils.hasText(gitProperties.commitId())) {
				printWriter.println("Git Commit ID: " + gitProperties.commitId());
			}
			if (StringUtils.hasText(gitProperties.branch())) {
				printWriter.println("Git Branch: " + gitProperties.branch());
			}
			if (gitProperties.commitTime() != null) {
				printWriter.println("Git Commit Time: " + gitProperties.commitTime());
			}
		}

		printWriter.flush();
		return ExitStatus.OK;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() {
		applicationContext.getBeanProvider(Version.BuildProperties.class).ifAvailable(this::setBuildProperties);
		applicationContext.getBeanProvider(Version.GitProperties.class).ifAvailable(this::setGitProperties);
	}

}
