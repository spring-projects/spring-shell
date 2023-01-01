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

import org.springframework.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionResolverMethodResolverTests {

	@Test
	void resolvesFromAnnotation() {
		ExceptionResolverMethodResolver resolver = new ExceptionResolverMethodResolver(InAnnotation.class);
		assertThat(resolver.hasExceptionMappings()).isTrue();
		assertThat(resolver.resolveMethod(new RuntimeException()))
				.isSameAs(ReflectionUtils.findMethod(InAnnotation.class, "errorHandler"));
		assertThat(resolver.resolveMethod(new IOException()))
				.isSameAs(ReflectionUtils.findMethod(InAnnotation.class, "errorHandler"));
	}

	private static class InAnnotation {

		@ExceptionResolver({ RuntimeException.class, IOException.class })
		void errorHandler() {
		}
	}

	@Test
	void resolvesFromMethodParameters() {
		ExceptionResolverMethodResolver resolver = new ExceptionResolverMethodResolver(InMethodParameter.class);
		assertThat(resolver.hasExceptionMappings()).isTrue();
		assertThat(resolver.resolveMethod(new RuntimeException())).isSameAs(ReflectionUtils
				.findMethod(InMethodParameter.class, "errorHandler", RuntimeException.class, IOException.class));
		assertThat(resolver.resolveMethod(new IOException())).isSameAs(ReflectionUtils
				.findMethod(InMethodParameter.class, "errorHandler", RuntimeException.class, IOException.class));
	}

	private static class InMethodParameter {

		@ExceptionResolver
		void errorHandler(RuntimeException e1, IOException e2) {
		}
	}

}
