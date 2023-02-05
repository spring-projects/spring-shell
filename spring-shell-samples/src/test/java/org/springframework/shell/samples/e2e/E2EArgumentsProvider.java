/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.samples.e2e;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

class E2EArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<E2ESource> {

	private E2ESource annotation;

	@Override
	public void accept(E2ESource annotation) {
		this.annotation = annotation;
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		String command = this.annotation.command();
		List<Arguments> arguments = new ArrayList<>();
		boolean anno = this.annotation.anno();
		boolean annox = this.annotation.annox();
		boolean reg = this.annotation.reg();
		if (anno) {
			arguments.add(Arguments.of(String.format("e2e %s %s", "anno", command), false));
			arguments.add(Arguments.of(String.format("e2e %s %s", "anno", command), true));
		}
		if (annox) {
			arguments.add(Arguments.of(String.format("e2e %s %s", "annox", command), false));
			arguments.add(Arguments.of(String.format("e2e %s %s", "annox", command), true));
		}
		if (reg) {
			arguments.add(Arguments.of(String.format("e2e %s %s", "reg", command), false));
			arguments.add(Arguments.of(String.format("e2e %s %s", "reg", command), true));
		}
		return arguments.stream();
	}
}
