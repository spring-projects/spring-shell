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

package org.springframework.shell.table;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for DelimiterTextWrapper.
 *
 * @author Eric Bottard
 */
public class DelimiterTextWrapperTest {

	private final TextWrapper wrapper = new DelimiterTextWrapper();

	@Test
	public void testNoWordSplit() {
		String[] text = new String[] {"the quick brown fox jumps over the lazy dog."};
		assertThat(wrapper.wrap(text, 10)).containsExactly("the quick ", "brown fox ", "jumps over", "the lazy  ",
				"dog.      ");
	}

	@Test
	public void testWordSplit() {
		String[] text = new String[] {"the quick brown fox jumps over the lazy dog."};
		assertThat(wrapper.wrap(text, 4)).containsExactly("the ", "quic", "k   ", "brow", "n   ", "fox ", "jump",
				"s   ", "over", "the ", "lazy", "dog.");
	}
}
