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
package org.springframework.shell;

import org.junit.jupiter.api.Test;

import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.aot.hint.annotation.Reflective;

import static org.assertj.core.api.Assertions.assertThat;

class AvailabilityReflectiveProcessorTests {

	private final AvailabilityReflectiveProcessor processor = new AvailabilityReflectiveProcessor();

	private final ReflectionHints hints = new ReflectionHints();

	@Test
	void registerReflectiveHintsForMethod() throws NoSuchMethodException {
			processor.registerReflectionHints(hints, SampleBean.class);
			assertThat(hints.typeHints()).singleElement().satisfies(typeHint -> {
					assertThat(typeHint.getType()).isEqualTo(TypeReference.of(SampleBean.class));
					assertThat(typeHint.getMemberCategories()).isEmpty();
					assertThat(typeHint.constructors()).isEmpty();
					assertThat(typeHint.fields()).isEmpty();
					assertThat(typeHint.methods()).singleElement().satisfies(methodHint -> {
							assertThat(methodHint.getName()).isEqualTo("test");
							assertThat(methodHint.getMode()).isEqualTo(ExecutableMode.INVOKE);
					});
			});
	}

	@Reflective(AvailabilityReflectiveProcessor.class)
	static class SampleBean {

		public Availability test() {
			return null;
		}
	}
}
