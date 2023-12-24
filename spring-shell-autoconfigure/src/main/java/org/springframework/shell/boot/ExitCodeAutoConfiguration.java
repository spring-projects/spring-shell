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
package org.springframework.shell.boot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandExecution;
import org.springframework.shell.exit.ExitCodeExceptionProvider;
import org.springframework.shell.exit.ExitCodeMappings;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for exit codes.
 *
 * @author Janne Valkealahti
 */
@AutoConfiguration
public class ExitCodeAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ShellExitCodeExceptionMapper shellExitCodeExceptionMapper() {
		return new ShellExitCodeExceptionMapper();
	}

	@Bean
	@ConditionalOnMissingBean
	public ShellExitCodeMappingsExceptionMapper shellExitCodeMappingsExceptionMapper() {
		return new ShellExitCodeMappingsExceptionMapper();
	}

	@Bean
	@ConditionalOnMissingBean
	public ExitCodeExceptionProvider exitCodeExceptionProvider() {
		return (exception, code) -> new ShellExitCodeException(exception, code);
	}

	static class ShellExitCodeExceptionMapper implements ExitCodeExceptionMapper {

		@Override
		public int getExitCode(Throwable exception) {
			// for e vs. its cause, see gh-961
			if (exception instanceof CommandExecution.CommandParserExceptionsException) {
				return 2;
			}
			else if (exception.getCause() instanceof CommandExecution.CommandParserExceptionsException) {
				return 2;
			}
			// only map parsing error so that other mappers can do their job
			return 0;
		}
	}

	static class ShellExitCodeMappingsExceptionMapper implements ExitCodeExceptionMapper, ExitCodeMappings {

		private final List<Function<Throwable, Integer>> functions = new ArrayList<>();

		@Override
		public void reset(List<Function<Throwable, Integer>> functions) {
			this.functions.clear();
			if (functions != null) {
				this.functions.addAll(functions);
			}
		}

		@Override
		public int getExitCode(Throwable exception) {
			int exitCode = 0;
			for (Function<Throwable, Integer> function : functions) {
				Throwable cause = exception.getCause();
				if (cause == null) {
					cause = exception;
				}
				Integer code = function.apply(cause);
				if (code != null) {
					if (code > 0 && code > exitCode || code < 0 && code < exitCode) {
						exitCode = code;
					}
				}
			}
			return exitCode;
		}
	}

	static class ShellExitCodeException extends RuntimeException implements ExitCodeGenerator {

		private int code;

		ShellExitCodeException(Throwable throwable, int code) {
			super(throwable);
			this.code = code;
		}

		@Override
		public int getExitCode() {
			return code;
		}
	}
}
