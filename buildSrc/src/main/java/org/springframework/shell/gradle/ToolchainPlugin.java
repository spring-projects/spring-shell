/*
 * Copyright 2024 the original author or authors.
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
package org.springframework.shell.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JavaToolchainSpec;

public class ToolchainPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		configureToolchain(project);
	}

	private void configureToolchain(Project project) {
		ToolchainExtension toolchain = project.getExtensions().create("toolchain", ToolchainExtension.class, project);
		configure(project, toolchain);
	}

	private void configure(Project project, ToolchainExtension toolchain) {
		JavaLanguageVersion toolchainVersion = toolchain.getJavaVersion();
		if (toolchainVersion == null) {
			toolchainVersion = JavaLanguageVersion.of(22);
		}
		JavaToolchainSpec toolchainSpec = project.getExtensions()
			.getByType(JavaPluginExtension.class)
			.getToolchain();
		toolchainSpec.getLanguageVersion().set(toolchainVersion);
	}

}