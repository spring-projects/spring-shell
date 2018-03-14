/*
 * Copyright 2018 the original author or authors.
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

package org.springframework.shell.standard.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * A command that displays all previously run commands, optionally dumping to a file readable by {@link Script}.
 *
 * @author Eric Bottard
 */
@ShellComponent
public class History {

    private final org.jline.reader.History jLineHistory;

    public History(org.jline.reader.History jLineHistory) {
        this.jLineHistory = jLineHistory;
    }

    /**
     * Marker interface for beans providing {@literal history} functionality to the shell.
     * <p>
     * <p>To override the history command, simply register your own bean implementing that interface
     * and the standard implementation will back off.</p>
     * <p>
     * <p>To disable the {@literal history} command entirely, set the {@literal spring.shell.command.history.enabled=false}
     * property in the environment.</p>
     *
     * @author Eric Bottard
     */
    public interface Command {
    }

    @ShellMethod(value = "Display or save the history of previously run commands")
    public List<String> history(@ShellOption(help = "A file to save history to.", defaultValue = ShellOption.NULL) File file) throws IOException {
        if (file == null) {
            List<String> result = new ArrayList<>(jLineHistory.size());
            jLineHistory.forEach(e -> result.add(e.line()));
            return result;
        } else {
            try (FileWriter w = new FileWriter(file)) {
                for (org.jline.reader.History.Entry entry : jLineHistory) {
                    w.append(entry.line()).append(System.lineSeparator());
                }
            }
            return Collections.singletonList(String.format("Wrote %d entries to %s", jLineHistory.size(), file));
        }
    }
}
