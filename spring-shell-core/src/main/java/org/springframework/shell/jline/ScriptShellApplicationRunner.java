/*
 * Copyright 2018 the original author or authors.
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

package org.springframework.shell.jline;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.jline.reader.Parser;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.shell.Shell;

/**
 * Spring Boot ApplicationRunner that looks for process arguments that start with
 * {@literal @}, which are then interpreted as references to script files to run and exit.
 * 
 * <p>
 * Has higher precedence than {@link InteractiveShellApplicationRunner} so that it
 * prevents it to run if scripts are found.
 * </p>
 *
 * @author Eric Bottard
 */
//tag::documentation[]
@Order(InteractiveShellApplicationRunner.PRECEDENCE - 100) // Runs before InteractiveShellApplicationRunner
public class ScriptShellApplicationRunner implements ApplicationRunner {
//end::documentation[]

	public static final String SPRING_SHELL_SCRIPT = "spring.shell.script";
	public static final String ENABLED = "enabled";

	/**
	 * The name of the environment property that allows to disable the behavior of this
	 * runner.
	 */
	public static final String SPRING_SHELL_SCRIPT_ENABLED = SPRING_SHELL_SCRIPT + "." + ENABLED;

	private final Parser parser;

	private final Shell shell;

	private final ConfigurableEnvironment environment;

	public ScriptShellApplicationRunner(Parser parser, Shell shell, ConfigurableEnvironment environment) {
		this.parser = parser;
		this.shell = shell;
		this.environment = environment;
	}

	//tag::documentation[]

	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<File> scriptsToRun = args.getNonOptionArgs().stream()
				.filter(s -> s.startsWith("@"))
				.map(s -> new File(s.substring(1)))
				.collect(Collectors.toList());

		boolean batchEnabled = environment.getProperty(SPRING_SHELL_SCRIPT_ENABLED, boolean.class, true);

		if (!scriptsToRun.isEmpty() && batchEnabled) {
			InteractiveShellApplicationRunner.disable(environment);
			for (File file : scriptsToRun) {
				try (Reader reader = new FileReader(file);
						FileInputProvider inputProvider = new FileInputProvider(reader, parser)) {
					shell.run(inputProvider);
				}
			}
		}
	}
	//end::documentation[]

}
