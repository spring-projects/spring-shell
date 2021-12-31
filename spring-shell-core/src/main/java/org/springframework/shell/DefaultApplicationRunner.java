/*
 * Copyright 2021 the original author or authors.
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
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * Default {@link ApplicationRunner} which dispatches to first ordered
 * {@link ShellRunner} able to handle shell.
 *
 * @author Janne Valkealahti
 */
public class DefaultApplicationRunner implements ShellApplicationRunner {

	private final static Logger log = LoggerFactory.getLogger(DefaultApplicationRunner.class);
	private final List<ShellRunner> shellRunners;

	public DefaultApplicationRunner(List<ShellRunner> shellRunners) {
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
