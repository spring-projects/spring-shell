/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell.samples;

import java.util.List;

import org.jline.reader.LineReader;
import org.jline.reader.Parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.Input;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.DefaultShellApplicationRunner;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.util.StringUtils;

@Configuration
public class ExampleApplicationRunnerConfiguration {

	@Autowired
	private Shell shell;

	@Bean
	public ApplicationRunner exampleApplicationRunner() {
		return new ExampleApplicationRunner(shell);
	}

	@Bean
	public ApplicationRunner defaultApplicationRunner(LineReader lineReader, PromptProvider promptProvider, Parser parser) {
		return new DefaultShellApplicationRunner(lineReader, promptProvider, parser, shell);
	}
}

/**
 * Example ApplicationRunner that shows how overall shell behavior can be customized. In
 * this particular example, any program (process) arguments are assumed to be shell
 * commands that need to be executed (and the shell then quits).
 */
@Order(-2)
class ExampleApplicationRunner implements ApplicationRunner {

	private Shell shell;

	public ExampleApplicationRunner(Shell shell) {
		this.shell = shell;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (!args.getNonOptionArgs().isEmpty()) {
			shell.run(new StringInputProvider(args.getNonOptionArgs()));
			throw new ExitRequest(0);
		}
	}
}

class StringInputProvider implements InputProvider {

	private final List<String> words;

	private boolean done;

	public StringInputProvider(List<String> words) {
		this.words = words;
	}

	@Override
	public Input readInput() {
		if (!done) {
			done = true;
			return new Input() {
				@Override
				public List<String> words() {
					return words;
				}

				@Override
				public String rawText() {
					return StringUtils.collectionToDelimitedString(words, " ");
				}
			};
		} else {
			return null;
		}
	}
}
