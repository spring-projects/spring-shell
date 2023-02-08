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
package org.springframework.shell.command;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.command.CommandParser.CommandParserResults;
import org.springframework.shell.command.parser.ParserConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandParserTests extends AbstractCommandTests {

	@Test
	public void testMethodExecution1() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.description("some arg1")
				.and()
			.withTarget()
				.method(pojo1, "method3", String.class)
				.and()
			.build();

		ConversionService conversionService = new DefaultConversionService();
		Map<String, CommandRegistration> registrations = new HashMap<>();
		registrations.put("command1", r1);
		CommandParser parser = CommandParser.of(conversionService, registrations, new ParserConfig());
		CommandParserResults results = parser.parse(new String[] { "command1", "--arg1", "myarg1value" });
		assertThat(results.results()).hasSize(1);
		assertThat(results.results().get(0).value()).isEqualTo("myarg1value");
	}
}
