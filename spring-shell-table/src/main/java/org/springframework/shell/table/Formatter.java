/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell.table;

/**
 * A Formatter is responsible for the initial rendering of a value to lines of text.
 *
 * <p>Note that this representation is likely to be altered later in the pipeline, for the
 * purpose of text wrapping and aligning. The role of a formatter is merely to give the
 * raw text representation (<i>e.g.</i> format numbers).</p>
 *
 * @author Eric Bottard
 */
public interface Formatter {

	public String[] format(Object value);
}
