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
package org.springframework.shell.command.annotation;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import org.springframework.shell.command.CommandHandlingResult;

import static org.assertj.core.api.Assertions.assertThat;

class MethodCommandExceptionResolverTests {

	@Test
	void annoHaveMatchingParameter() {
		AnnoHaveMatchingParameter bean = new AnnoHaveMatchingParameter();
		MethodCommandExceptionResolver resolver = new MethodCommandExceptionResolver(bean);
		CustomException1 e = new CustomException1();
		CommandHandlingResult result = resolver.resolve(e);
		assertThat(result).isNotNull();
		assertThat(e).isSameAs(bean.called);
	}

	private static class AnnoHaveMatchingParameter {

		Exception called;

		@ExceptionResolver({ CustomException1.class })
		CommandHandlingResult errorHandler(Exception e) {
			called = e;
			return CommandHandlingResult.of("Hi, handled exception\n", 42);
		}
	}

	@Test
	void annoHaveNotMatchingParameter() {
		AnnoHaveNotMatchingParameter bean = new AnnoHaveNotMatchingParameter();
		MethodCommandExceptionResolver resolver = new MethodCommandExceptionResolver(bean);
		CustomException1 e = new CustomException1();
		CommandHandlingResult result = resolver.resolve(e);
		assertThat(result).isNotNull();
		assertThat(bean.called).isTrue();
	}

	private static class AnnoHaveNotMatchingParameter {

		boolean called;

		@ExceptionResolver({ CustomException1.class })
		CommandHandlingResult errorHandler() {
			called = true;
			return CommandHandlingResult.of("Hi, handled exception\n", 42);
		}
	}

	@Test
	void resolvedFromParameter() {
		ResolvedFromParameter bean = new ResolvedFromParameter();
		MethodCommandExceptionResolver resolver = new MethodCommandExceptionResolver(bean);
		CustomException1 e = new CustomException1();
		CommandHandlingResult result = resolver.resolve(e);
		assertThat(result).isNotNull();
		assertThat(e).isSameAs(bean.called);
	}

	private static class ResolvedFromParameter {

		Exception called;

		@ExceptionResolver
		CommandHandlingResult errorHandler(CustomException1 e) {
			called = e;
			return CommandHandlingResult.of("Hi, handled exception\n", 42);
		}
	}

	@Test
	void noMappedExceptions() {
		NoMappedExceptions bean = new NoMappedExceptions();
		MethodCommandExceptionResolver resolver = new MethodCommandExceptionResolver(bean);
		assertThat(resolver).isNotNull();
	}

	private static class NoMappedExceptions {

		@ExceptionResolver
		CommandHandlingResult errorHandler() {
			return RESULT;
		}
	}

	@Test
	void shouldErrorWhenResolving() {
		ShouldErrorWhenResolving bean = new ShouldErrorWhenResolving();
		MethodCommandExceptionResolver resolver = new MethodCommandExceptionResolver(bean);
		RuntimeException e = new RuntimeException();
		CommandHandlingResult result = resolver.resolve(e);
		// Internal error doesn't get through - IOException cannot be resolved
		assertThat(result).isNull();
	}

	private static class ShouldErrorWhenResolving {

		@ExceptionResolver
		CommandHandlingResult errorHandler(RuntimeException e1, IOException e2) {
			return RESULT;
		}
	}

	private static CommandHandlingResult RESULT = CommandHandlingResult.of("Hi, handled exception\n", 42);

	private static class CustomException1 extends RuntimeException {
	}
}
