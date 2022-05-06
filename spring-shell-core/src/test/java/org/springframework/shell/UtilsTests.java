/*
 * Copyright 2015-2022 the original author or authors.
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

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Utils}.
 *
 * @author Eric Bottard
 */
public class UtilsTests {

	@Test
	public void testUnCamelify() throws Exception {
		assertThat(Utils.unCamelify("HelloWorld")).isEqualTo("hello-world");
		assertThat(Utils.unCamelify("helloWorld")).isEqualTo("hello-world");
		assertThat(Utils.unCamelify("helloWorldHowAreYou")).isEqualTo("hello-world-how-are-you");
		assertThat(Utils.unCamelify("URL")).isEqualTo("url");
	}

	@Test
	public void testSplit() {
		Predicate<String> predicate = t -> t.startsWith("-");
		List<List<String>> split = null;

		split = Utils.split(new String[] { "-a1", "a1" }, predicate);
		assertThat(split).containsExactly(Arrays.asList("-a1", "a1"));

		split = Utils.split(new String[] { "-a1", "a1", "-a2", "a2" }, predicate);
		assertThat(split).containsExactly(Arrays.asList("-a1", "a1"), Arrays.asList("-a2", "a2"));

		split = Utils.split(new String[] { "a0", "-a1", "a1" }, predicate);
		assertThat(split).containsExactly(Arrays.asList("a0"), Arrays.asList("-a1", "a1"));

		split = Utils.split(new String[] { "-a1", "-a2" }, predicate);
		assertThat(split).containsExactly(Arrays.asList("-a1"), Arrays.asList("-a2"));

		split = Utils.split(new String[] { "a1", "a2" }, predicate);
		assertThat(split).containsExactly(Arrays.asList("a1", "a2"));

		split = Utils.split(new String[] { "-a1", "a1", "a2" }, predicate);
		assertThat(split).containsExactly(Arrays.asList("-a1", "a1", "a2"));
	}
}
