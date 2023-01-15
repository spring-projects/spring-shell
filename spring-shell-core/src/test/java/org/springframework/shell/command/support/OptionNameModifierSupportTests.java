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
package org.springframework.shell.command.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class OptionNameModifierSupportTests {

	@ParameterizedTest
	@CsvSource({
		"camel case,camelCase",
		"camel_case,camelCase",
		"camel-case,camelCase",
		"camelCase,camelCase",
		"CamelCase,camelCase"
	})
	void testCamel(String name, String expected) {
		assertThat(camel(name)).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
		"pascal-case,PascalCase",
		"pascal_case,PascalCase",
		"pascalCase,PascalCase",
		"PascalCase,PascalCase"
	})
	void testPascal(String name, String expected) {
		assertThat(pascal(name)).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
		"kebabCase,kebab-case",
		"kebab_case,kebab-case",
		"kebab_Case,kebab-case",
		"Kebab_case,kebab-case"
	})
	void testKebab(String name, String expected) {
		assertThat(kebab(name)).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
		"snakeCase,snake_case",
		"snake_case,snake_case",
		"snake_Case,snake_case",
		"Snake_case,snake_case"
	})
	void testSnake(String name, String expected) {
		assertThat(snake(name)).isEqualTo(expected);
	}

	private String camel(String name) {
		return OptionNameModifierSupport.toCamelCase(name);
	}

	private String kebab(String name) {
		return OptionNameModifierSupport.toKebabCase(name);
	}

	private String snake(String name) {
		return OptionNameModifierSupport.toSnakeCase(name);
	}

	private String pascal(String name) {
		return OptionNameModifierSupport.toPascalCase(name);
	}
}
