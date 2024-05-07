/*
 * Copyright 2022-2024 the original author or authors.
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

import java.util.ArrayList;

import org.graalvm.buildtools.gradle.NativeImagePlugin;
import org.graalvm.buildtools.gradle.dsl.GraalVMExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginManager;
import org.springframework.boot.gradle.dsl.SpringBootExtension;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;
import org.springframework.boot.gradle.tasks.bundling.BootJar;

/**
 * @author Janne Valkealahti
 */
class SamplePlugin implements Plugin<Project> {

	private static final String NATIVE = "springShellSampleNative";
	private static final String E2E = "springShellSampleE2E";
	private static final String MUSL = "springShellSampleMusl";

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply(JavaPlugin.class);
		pluginManager.apply(ManagementConfigurationPlugin.class);
		pluginManager.apply(SpringBootPlugin.class);
		if (isEnabled(project, NATIVE)) {
			pluginManager.apply(NativeImagePlugin.class);
			customizeNative(project);
		}
		configureBootJar(project);
		configureBootBuildInfo(project);
		new JavaConventions().apply(project);
	}

	private void configureBootJar(Project project) {
		if (isEnabled(project, E2E)) {
			project.getTasks().withType(BootJar.class, (bootJar) -> {
				String name = String.format("%s.%s", bootJar.getArchiveBaseName().get(),
						bootJar.getArchiveExtension().get());
				bootJar.getArchiveFileName().set(name);
			});
		}
	}

	private void configureBootBuildInfo(Project project) {
		SpringBootExtension extension = project.getExtensions().getByType(SpringBootExtension.class);
		extension.buildInfo(buildInfo -> {
			buildInfo.getExcludes().add("time");
		});
	}

	private void customizeNative(Project project) {
		project.getPlugins().withType(NativeImagePlugin.class).all(nativePlugin -> {
			configureGraalVmExtension(project);
		});
	}

	private void configureGraalVmExtension(Project project) {
		GraalVMExtension extension = project.getExtensions().getByType(GraalVMExtension.class);
		ArrayList<String> options = new ArrayList<String>();
		if (isEnabled(project, MUSL)) {
			options.add("--static");
			options.add("--libc=musl");
		}
		// force compatibility as detection i.e. in macos runners is flaky
		options.add("-march=compatibility");
		extension.getBinaries().getByName(NativeImagePlugin.NATIVE_MAIN_EXTENSION).buildArgs(options.toArray());
	}

	private boolean isEnabled(Project project, String property) {
		if (project.hasProperty(property)) {
			return Boolean.valueOf(String.valueOf(project.findProperty(property)));
		}
		return false;
	}
}
