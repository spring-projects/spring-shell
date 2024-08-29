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

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.publish.tasks.GenerateModuleMetadata;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.CoreJavadocOptions;
import org.gradle.external.javadoc.JavadocMemberLevel;
import org.gradle.external.javadoc.JavadocOutputLevel;

/**
 * @author Janne Valkealahti
 */
class DocsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply(JavaPlugin.class);
		pluginManager.apply(JavaLibraryPlugin.class);
		pluginManager.apply(ManagementConfigurationPlugin.class);
		pluginManager.apply(SpringMavenPlugin.class);

		createApiTask(project);

		project.getTasks().withType(GenerateModuleMetadata.class, metadata -> {
			metadata.setEnabled(false);
		});
	}

	private Javadoc createApiTask(Project project) {
		Javadoc api = project.getTasks().create("aggregatedJavadoc", Javadoc.class, a -> {
			a.setGroup("Documentation");
			a.setDescription("Generates aggregated Javadoc API documentation.");
			a.setDestinationDir(new File(project.getBuildDir(), "generated-antora-javadocs/modules/ROOT/assets/attachments/api/java"));
			a.setTitle(String.format("Spring Shell %s API", project.getVersion()));
			CoreJavadocOptions options = (CoreJavadocOptions) a.getOptions();
			options.windowTitle(String.format("Spring Shell %s API", project.getVersion()));
			options.setMemberLevel(JavadocMemberLevel.PROTECTED);
			options.setOutputLevel(JavadocOutputLevel.QUIET);
			options.addStringOption("Xdoclint:none", "-quiet");
		});

		project.getRootProject().getSubprojects().forEach(p -> {
			p.getPlugins().withType(ModulePlugin.class, m -> {
				JavaPluginConvention java = p.getConvention().getPlugin(JavaPluginConvention.class);
				SourceSet mainSourceSet = java.getSourceSets().getByName("main");

				api.setSource(api.getSource().plus(mainSourceSet.getAllJava()));

				p.getTasks().withType(Javadoc.class, j -> {
					api.setClasspath(api.getClasspath().plus(j.getClasspath()));
				});
			});
		});

		return api;
	}

}
