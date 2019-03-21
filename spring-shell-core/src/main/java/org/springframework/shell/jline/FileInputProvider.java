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

package org.springframework.shell.jline;

import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.springframework.shell.Input;
import org.springframework.shell.InputProvider;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

/**
 * An {@link InputProvider} that reads input from file-like sources.
 * <p>
 * <p>Still uses a {@link org.jline.reader.Parser} to interpret word boundaries. Supports backslashes at end
 * of line to signal line continuation.</p>
 *
 * @author Eric Bottard
 */
public class FileInputProvider implements InputProvider, Closeable {

    private  static final String BACKSLASH_AT_EOL_REGEX = "(.*)\\\\\\s*$";
    private final BufferedReader reader;

    private final Parser parser;

    public FileInputProvider(Reader reader, Parser parser) {
        this.reader = new BufferedReader(reader);
        this.parser = parser;
    }

    @Override
    public Input readInput() {
        StringBuilder sb = new StringBuilder();
        boolean continued = false;
        String line;
        try {
            do {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                continued = line.matches(BACKSLASH_AT_EOL_REGEX);
                sb.append(line.replaceFirst(BACKSLASH_AT_EOL_REGEX, "$1 "));
            } while (continued);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (line == null) {
            return null;
        } else {
            ParsedLine parsedLine = parser.parse(sb.toString(), sb.toString().length());
            return new ParsedLineInput(parsedLine);
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
