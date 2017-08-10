/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell.jline;

import org.jline.reader.ParsedLine;
import org.springframework.shell.Input;

import java.util.List;

/**
 * An implementation of {@link Input} backed by the result of a {@link org.jline.reader.Parser#parse(String, int)}.
 *
 * @author Eric Bottard
 */
class ParsedLineInput implements Input {

    private final ParsedLine parsedLine;

    ParsedLineInput(ParsedLine parsedLine) {
        this.parsedLine = parsedLine;
    }

    @Override
    public String rawText() {
        return parsedLine.line();
    }

    @Override
    public List<String> words() {
        return JLineShellAutoConfiguration.sanitizeInput(parsedLine.words());
    }
}
