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

import java.util.Arrays;
import java.util.List;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;
import org.gradle.external.javadoc.CoreJavadocOptions;

/**
 * @author Janne Valkealahti
 */
class JavaConventions {

	private static final String SOURCE_AND_TARGET_COMPATIBILITY = "17";

	void apply(Project project) {
		project.getPlugins().withType(JavaBasePlugin.class, java -> {
			configureJavaConventions(project);
			configureJavadocConventions(project);
			configureTestConventions(project);
		});
	}

	private void configureJavadocConventions(Project project) {
		project.getTasks().withType(Javadoc.class, (javadoc) -> {
			CoreJavadocOptions options = (CoreJavadocOptions) javadoc.getOptions();
			options.source("17");
			options.encoding("UTF-8");
			options.addStringOption("Xdoclint:none", "-quiet");
		});
	}

	private void configureJavaConventions(Project project) {
		project.getTasks().withType(JavaCompile.class, (compile) -> {
			compile.getOptions().setEncoding("UTF-8");
			List<String> args = compile.getOptions().getCompilerArgs();
			if (!args.contains("-parameters")) {
				args.add("-parameters");
			}
			if (project.hasProperty("toolchainVersion")) {
				compile.setSourceCompatibility(SOURCE_AND_TARGET_COMPATIBILITY);
				compile.setTargetCompatibility(SOURCE_AND_TARGET_COMPATIBILITY);
			}
			else if (buildingWithJava17(project)) {
				args.addAll(Arrays.asList("-Xdoclint:none"));
				// TODO: When we're without javadoc errors
				// args.addAll(Arrays.asList("-Werror", "-Xlint:unchecked", "-Xlint:deprecation", "-Xlint:rawtypes",
				// 		"-Xlint:varargs"));
			}
		});
	}

	private boolean buildingWithJava17(Project project) {
		return JavaVersion.current() == JavaVersion.VERSION_17;
	}

	private void configureTestConventions(Project project) {
		project.getTasks().withType(Test.class, test -> {
			test.useJUnitPlatform();
		});
	}
}
