/*
 * Copyright 2022 the original author or authors.
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
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.asciidoctor.gradle.base.AsciidoctorAttributeProvider;
import org.asciidoctor.gradle.jvm.AbstractAsciidoctorTask;
import org.asciidoctor.gradle.jvm.AsciidoctorJPlugin;
import org.asciidoctor.gradle.jvm.AsciidoctorTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.Sync;

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
		pluginManager.apply(AsciidoctorJPlugin.class);

		configureAdocPlugins(project);
	}

	private void configureAdocPlugins(Project project) {
		project.getPlugins().withType(AsciidoctorJPlugin.class, (asciidoctorPlugin) -> {
			createDefaultAsciidoctorRepository(project);
			makeAllWarningsFatal(project);
			Sync unzipResources = createUnzipDocumentationResourcesTask(project);
			Sync snippetsResources = createSnippetsResourcesTask(project);
			project.getTasks().withType(AbstractAsciidoctorTask.class, (asciidoctorTask) -> {
				asciidoctorTask.dependsOn(unzipResources);
				asciidoctorTask.dependsOn(snippetsResources);
				// configureExtensions(project, asciidoctorTask);
				configureCommonAttributes(project, asciidoctorTask);
				configureOptions(asciidoctorTask);
				asciidoctorTask.sourceDir("src/main/asciidoc");
				asciidoctorTask.baseDirFollowsSourceDir();
				asciidoctorTask.useIntermediateWorkDir();
				asciidoctorTask.resources(new Action<CopySpec>() {
					@Override
					public void execute(CopySpec resourcesSpec) {
						resourcesSpec.from(unzipResources);
						// resourcesSpec.from(snippetsResources);
						resourcesSpec.from(snippetsResources, copySpec -> {
							copySpec.include("docs/*");
						});
						resourcesSpec.from(asciidoctorTask.getSourceDir(), new Action<CopySpec>() {
							@Override
							public void execute(CopySpec resourcesSrcDirSpec) {
								// https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/523
								// For now copy the entire sourceDir over so that include files are
								// available in the intermediateWorkDir
								resourcesSrcDirSpec.include("images/*");
							}
						});
					}
				});
				if (asciidoctorTask instanceof AsciidoctorTask) {
					configureHtmlOnlyAttributes(project, asciidoctorTask);
				}
			});
		});
	}

	private void createDefaultAsciidoctorRepository(Project project) {
		project.getGradle().afterProject(new Action<Project>() {
			@Override
			public void execute(Project project) {
				RepositoryHandler repositories = project.getRepositories();
				if (repositories.isEmpty()) {
					repositories.mavenCentral();
					repositories.maven(repo -> {
						repo.setUrl(URI.create("https://repo.spring.io/release"));
					});
				}
			}
		});
	}

	private void makeAllWarningsFatal(Project project) {
		// project.getExtensions().getByType(AsciidoctorJExtension.class).fatalWarnings(".*");
	}

	// private void configureExtensions(Project project, AbstractAsciidoctorTask asciidoctorTask) {
	// 	Configuration extensionsConfiguration = project.getConfigurations().maybeCreate("asciidoctorExtensions");
	// 	extensionsConfiguration.defaultDependencies(new Action<DependencySet>() {
	// 		@Override
	// 		public void execute(DependencySet dependencies) {
	// 			dependencies.add(project.getDependencies().create("io.spring.asciidoctor:spring-asciidoctor-extensions-block-switch:0.4.2.RELEASE"));
	// 		}
	// 	});
	// 	asciidoctorTask.configurations(extensionsConfiguration);
	// }

	private Sync createSnippetsResourcesTask(Project project) {
		Sync sync = project.getTasks().create("snippetResources", Sync.class, s -> {
			s.from(new File(project.getRootProject().getRootDir(), "spring-shell-docs/src/test/java/org/springframework/shell"), spec -> {
				spec.include("docs/*");
			});
			File destination = new File(project.getBuildDir(), "docs/snippets");
			s.into(destination);
		});
		return sync;
	}

	private Sync createUnzipDocumentationResourcesTask(Project project) {
		Configuration documentationResources = project.getConfigurations().maybeCreate("documentationResources");
		documentationResources.getDependencies()
				.add(project.getDependencies().create("io.spring.docresources:spring-doc-resources:0.2.5"));
		Sync unzipResources = project.getTasks().create("unzipDocumentationResources",
				Sync.class, new Action<Sync>() {
					@Override
			public void execute(Sync sync) {
				sync.dependsOn(documentationResources);
				sync.from(new Callable<List<FileTree>>() {
					@Override
					public List<FileTree> call() throws Exception {
						List<FileTree> result = new ArrayList<>();
						documentationResources.getAsFileTree().forEach(new Consumer<File>() {
							@Override
							public void accept(File file) {
								result.add(project.zipTree(file));
							}
						});
						return result;
					}
				});
				File destination = new File(project.getBuildDir(), "docs/resources");
				sync.into(project.relativePath(destination));
			}
		});
		return unzipResources;
	}

	private void configureOptions(AbstractAsciidoctorTask asciidoctorTask) {
		asciidoctorTask.options(Collections.singletonMap("doctype", "book"));
	}

	private void configureHtmlOnlyAttributes(Project project, AbstractAsciidoctorTask asciidoctorTask) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("toc", "left");
		attributes.put("source-highlighter", "highlight.js");
		attributes.put("highlightjsdir", "js/highlight");
		attributes.put("highlightjs-theme", "github");
		attributes.put("linkcss", true);
		attributes.put("icons", "font");
		attributes.put("stylesheet", "css/spring.css");
		attributes.put("snippets", "docs");
		asciidoctorTask.getAttributeProviders().add(new AsciidoctorAttributeProvider() {
			@Override
			public Map<String, Object> getAttributes() {
				Object version = project.getVersion();
				Map<String, Object> attrs = new HashMap<>();
				if (version != null && version.toString() != Project.DEFAULT_VERSION) {
					attrs.put("revnumber", version);
					attrs.put("projectVersion", version);
				}
				return attrs;
			}
		});
		asciidoctorTask.attributes(attributes);
	}

	private void configureCommonAttributes(Project project, AbstractAsciidoctorTask asciidoctorTask) {
		Map<String, Object> attributes = new HashMap<>();
		// attributes.put("attribute-missing", "warn");
		attributes.put("icons", "font");
		attributes.put("idprefix", "");
		attributes.put("idseparator", "-");
		attributes.put("docinfo", "shared");
		attributes.put("sectanchors", "");
		attributes.put("sectnums", "");
		attributes.put("today-year", LocalDate.now().getYear());
		asciidoctorTask.attributes(attributes);
	}

}
