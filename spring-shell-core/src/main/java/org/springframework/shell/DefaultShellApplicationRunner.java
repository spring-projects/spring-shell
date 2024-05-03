/*
 * Copyright 2021-2024 the original author or authors.
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
package org.springframework.shell;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;

/**
 * Default {@link ShellApplicationRunner} which dispatches to the first ordered {@link ShellRunner} able to handle
 * the shell.
 *
 * @author Janne Valkealahti
 * @author Chris Bono
 */
@Order(DefaultShellApplicationRunner.PRECEDENCE)
public class DefaultShellApplicationRunner implements ShellApplicationRunner {

	/**
	 * The precedence at which this runner is executed with respect to other ApplicationRunner beans
	 */
	public static final int PRECEDENCE = 0;

	private final static Logger log = LoggerFactory.getLogger(DefaultShellApplicationRunner.class);
	private final List<ShellRunner> shellRunners;

	public DefaultShellApplicationRunner(List<ShellRunner> shellRunners) {
		// TODO: follow up with spring-native
		// Looks like with fatjar it comes on a correct order from
		// a context(not really sure if that's how spring context works) but
		// not with native, so call AnnotationAwareOrderComparator manually.
		Collections.sort(shellRunners, new AnnotationAwareOrderComparator());
		this.shellRunners = shellRunners;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.debug("Checking shell runners {}", shellRunners);

		// Handle new ShellRunner api
		String[] sourceArgs = args.getSourceArgs();
		boolean canRun = false;
		for (ShellRunner runner : shellRunners) {
			// let exception to get thrown as needed i.e. for
			// exit code mappings
			canRun = runner.run(sourceArgs);
			if (canRun) {
				break;
			}
		}

		if (canRun) {
			// new api handled execution
			return;
		}

		// Handle old deprecated ShellRunner api
		Optional<ShellRunner> optional = shellRunners.stream()
				.filter(sh -> sh.canRun(args))
				.findFirst();
		ShellRunner shellRunner = optional.orElse(null);
		log.debug("Using shell runner {}", shellRunner);
		if (shellRunner != null) {
			shellRunner.run(args);
		}
	}
}
