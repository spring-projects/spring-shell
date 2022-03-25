/*
 * Copyright 2018-2022 the original author or authors.
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
import org.springframework.core.annotation.Order;
import org.springframework.shell.Shell;
import org.springframework.shell.ShellRunner;
import org.springframework.util.ObjectUtils;

/**
 * A {@link ShellRunner} that looks for process arguments that start with {@literal @}, which are then interpreted as
 * references to script files to run and exit.
 *
 * <p>Has higher precedence than {@link NonInteractiveShellRunner} and {@link InteractiveShellRunner} which gives it
 * top priority to run the shell if scripts are found.
 *
 * @author Eric Bottard
 */
//tag::documentation[]
@Order(ScriptShellRunner.PRECEDENCE)
public class ScriptShellRunner extends AbstractShellRunner{
//end::documentation[]

	/**
	 * The precedence at which this runner is ordered by the DefaultApplicationRunner - which also controls
	 * the order it is consulted on the ability to handle the current shell.
	 */
	public static final int PRECEDENCE = InteractiveShellRunner.PRECEDENCE - 100;

	private final Parser parser;

	public ScriptShellRunner(Parser parser, Shell shell) {
		super(shell);
		this.parser = parser;
	}

	@Override
	public boolean canRun(ApplicationArguments args) {
		List<File> scriptsToRun = args.getNonOptionArgs().stream()
				.filter(s -> s.startsWith("@"))
				.map(s -> new File(s.substring(1)))
				.collect(Collectors.toList());
		return !ObjectUtils.isEmpty(scriptsToRun);
	}

	//tag::documentation[]

	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<File> scriptsToRun = args.getNonOptionArgs().stream()
				.filter(s -> s.startsWith("@"))
				.map(s -> new File(s.substring(1)))
				.collect(Collectors.toList());

		for (File file : scriptsToRun) {
			try (Reader reader = new FileReader(file);
					FileInputProvider inputProvider = new FileInputProvider(reader, parser)) {
				shell.run(inputProvider);
			}
		}
	}
	//end::documentation[]

}
