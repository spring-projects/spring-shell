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

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.AbstractAssert;

/**
 * Asserts for {@link ShellScreen}.
 *
 * @author Janne Valkealahti
 */
public class ShellScreenAssert extends AbstractAssert<ShellScreenAssert, ShellScreen> {

	public ShellScreenAssert(ShellScreen actual) {
		super(actual, ShellScreenAssert.class);
	}

	/**
	 * Verifies that text if found from a screen.
	 *
	 * @param text the text to look for
	 * @return this assertion object
	 */
	public ShellScreenAssert containsText(String text) {
		isNotNull();
		List<String> lines = actual.lines();
		boolean match = lines.stream().filter(n -> n.contains(text)).findFirst().isPresent();
		if (!match) {
			failWithMessage("Expected to find %s from screen but was %s", text,
					lines.stream().collect(Collectors.joining("\n")));
		}
		return this;
	}
}
