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

package org.springframework.shell.result;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.ResultHandler;

/**
 * Used for explicit configuration of {@link org.springframework.shell.ResultHandler}s.
 *
 * @author Eric Bottard
 */
@Configuration
public class ResultHandlerConfig {

	@Bean
	@Qualifier("main")
	public ResultHandler<?> mainResultHandler() {
		return new TypeHierarchyResultHandler();
	}

	@Bean
	public IterableResultHandler iterableResultHandler() {
		return new IterableResultHandler();
	}

	@PostConstruct
	public void wireIterableResultHandler() {
		iterableResultHandler().setDelegate(mainResultHandler());
	}

}
