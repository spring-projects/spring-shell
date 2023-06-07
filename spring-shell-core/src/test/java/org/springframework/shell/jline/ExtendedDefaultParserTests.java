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
package org.springframework.shell.jline;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtendedDefaultParserTests {

	@Test
	void wordsParsing() {
		ExtendedDefaultParser parser = new ExtendedDefaultParser();

		assertThat(parser.parse("one", 0).words()).hasSize(1);
		assertThat(parser.parse("one", 3).words()).hasSize(1);

		assertThat(parser.parse("one two", 0).words()).hasSize(2);
		assertThat(parser.parse("one two", 7).words()).hasSize(2);

		assertThat(parser.parse("'one'", 0).words()).hasSize(1);
		assertThat(parser.parse("'one'", 5).words()).hasSize(1);

		assertThat(parser.parse("'one' two", 0).words()).hasSize(2);
		assertThat(parser.parse("'one' two", 9).words()).hasSize(2);

		assertThat(parser.parse("one 'two'", 0).words()).hasSize(2);
		assertThat(parser.parse("one 'two'", 9).words()).hasSize(2);

		assertThat(parser.parse("\"one\"", 0).words()).hasSize(1);
		assertThat(parser.parse("\"one\"", 5).words()).hasSize(1);

		assertThat(parser.parse("\"one\" two", 0).words()).hasSize(2);
		assertThat(parser.parse("\"one\" two", 9).words()).hasSize(2);

		assertThat(parser.parse("one \"two\"", 0).words()).hasSize(2);
		assertThat(parser.parse("one \"two\"", 9).words()).hasSize(2);
	}
}
