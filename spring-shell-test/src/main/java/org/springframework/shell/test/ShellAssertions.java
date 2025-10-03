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
package org.springframework.shell.test;

import org.assertj.core.api.InstanceOfAssertFactory;

/**
 * Entry point for assertion methods for shell components.
 *
 * @author Janne Valkealahti
 */
public class ShellAssertions {

	/**
	 * Instance of a assert factory for {@link ShellScreen}.
	 */
	public static final InstanceOfAssertFactory<ShellScreen, ShellScreenAssert> SHELLSCREEN = new InstanceOfAssertFactory<>(
			ShellScreen.class, ShellAssertions::assertThat);

	/**
	 * Creates an instance of {@link ShellScreenAssert}.
	 * @param actual the actual value
	 * @return the created assertion object
	 */
	public static ShellScreenAssert assertThat(ShellScreen actual) {
		return new ShellScreenAssert(actual);
	}

}
