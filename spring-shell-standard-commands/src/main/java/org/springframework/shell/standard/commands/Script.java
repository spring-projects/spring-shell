/*
 * Copyright 2017-2021 the original author or authors.
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
package org.springframework.shell.standard.commands;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.jline.reader.Parser;

import org.springframework.shell.jline.FileInputProvider;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * A command that can read and execute other commands from a file.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 */
@ShellComponent
public class Script extends AbstractShellComponent {

    private final Parser parser;

    public Script(Parser parser) {
        this.parser = parser;
    }

    /**
     * Marker interface for beans providing {@literal script} functionality to the shell.
     * <p>
     * <p>To override the script command, simply register your own bean implementing that interface
     * and the standard implementation will back off.</p>
     * <p>
     * <p>To disable the {@literal script} command entirely, set the {@literal spring.shell.command.script.enabled=false}
     * property in the environment.</p>
     *
     * @author Eric Bottard
     */
    public interface Command {
    }

    @ShellMethod(value = "Read and execute commands from a file.")
    public void script(File file) throws Exception {
        Reader reader = new FileReader(file);
        try (FileInputProvider inputProvider = new FileInputProvider(reader, parser)) {
            getShell().run(inputProvider);
        }
    }

}
