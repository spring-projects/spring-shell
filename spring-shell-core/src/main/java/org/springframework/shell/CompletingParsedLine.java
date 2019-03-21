/*
 * Copyright 2017 the original author or authors.
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

import org.jline.reader.ParsedLine;

/**
 * An extension of {@link ParsedLine} that, being aware of the quoting and escaping rules
 * of the {@link org.jline.reader.Parser} that produced it, knows if and how a completion candidate
 * should be escaped/quoted.
 *
 * @author Eric Bottard
 */
@FunctionalInterface
public interface CompletingParsedLine {

	public CharSequence emit(CharSequence candidate);
}
