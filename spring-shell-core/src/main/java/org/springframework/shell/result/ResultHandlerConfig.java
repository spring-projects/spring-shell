/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell.result;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.ResultHandler;
import org.springframework.shell.TerminalSizeAware;

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
	@Qualifier("iterableResultHandler")
	public IterableResultHandler iterableResultHandler() {
		return new IterableResultHandler();
	}

	@Bean
	@ConditionalOnClass(TerminalSizeAware.class)
	public TerminalSizeAwareResultHandler terminalSizeAwareResultHandler() {
		return new TerminalSizeAwareResultHandler();
	}

	@Bean
	public AttributedCharSequenceResultHandler attributedCharSequenceResultHandler() {
		return new AttributedCharSequenceResultHandler();
	}

	@Bean
	public DefaultResultHandler defaultResultHandler() {
		return new DefaultResultHandler();
	}

	@Bean
	public ParameterValidationExceptionResultHandler parameterValidationExceptionResultHandler() {
		return new ParameterValidationExceptionResultHandler();
	}

	@Bean
	public ThrowableResultHandler throwableResultHandler() {
		return new ThrowableResultHandler();
	}

}
