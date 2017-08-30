package org.springframework.shell.standard.commands;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.jline.reader.Parser;
import org.springframework.shell.Input;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.FileInputProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * A command that can read and execute other commands from a file.
 *
 * @author Eric Bottard
 */
@ShellComponent
public class Script {

    private final Shell shell;

    private final Parser parser;

    public Script(Shell shell, Parser parser) {
        this.shell = shell;
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
    public void script(File file) throws IOException {
        Reader reader = new FileReader(file);
        try (FileInputProvider inputProvider = new FileInputProvider(reader, parser)) {
            shell.run(inputProvider);
        }
    }

}
