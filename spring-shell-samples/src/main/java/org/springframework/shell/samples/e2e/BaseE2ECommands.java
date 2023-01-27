/*
 * Copyright 2022-2023 the original author or authors.
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

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.util.StringUtils;

/**
 * Base class for all e2e commands.
 *
 * @author Janne Valkealahti
 */
abstract class BaseE2ECommands {

	static final String GROUP = "E2E Commands";
	static final String REG = "e2e reg";
	static final String LEGACY_ANNO = "e2e anno ";
	// TODO: anno should become anno-legacy and annox to anno
	static final String ANNO = "e2e annox ";

	@Autowired
	private CommandRegistration.BuilderSupplier builder;

	CommandRegistration.Builder getBuilder() {
		return builder.get();
	}

	static String stringOfStrings(String[] values) {
		return String.format("[%s]", StringUtils.arrayToCommaDelimitedString(values));
	}

	static String stringOfInts(int[] values) {
		String joined = IntStream.range(0, values.length)
			.mapToLong(i -> values[i])
			.boxed()
			.map(d -> d.toString())
			.collect(Collectors.joining(","));
		return String.format("[%s]", joined);
	}

	static String stringOfFloats(float[] values) {
		String joined = IntStream.range(0, values.length)
			.mapToDouble(i -> values[i])
			.boxed()
			.map(d -> d.toString())
			.collect(Collectors.joining(","));
		return String.format("[%s]", joined);
	}
}
