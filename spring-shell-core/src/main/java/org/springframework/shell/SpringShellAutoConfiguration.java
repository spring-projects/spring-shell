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

package org.springframework.shell;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.jline.JLineShell;
import org.springframework.shell.result.ResultHandlerConfig;

/**
 * Creates supporting beans for running the Shell
 */
@Configuration
@ComponentScan(basePackageClasses = ResultHandlerConfig.class)
@Import(JLineShell.class)
public class SpringShellAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(ConversionService.class)
	public ConversionService conversionService() {
		return new DefaultConversionService();
	}

	@Bean
	public ApplicationRunner applicationRunner(Shell shell) {
		return new ApplicationRunner() {
			@Override
			public void run(ApplicationArguments args) throws Exception {
				shell.run();
			}
		};
	}

}
