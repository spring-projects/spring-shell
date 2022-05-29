package org.springframework.shell.jline;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.junit.jupiter.api.Test;
import org.springframework.shell.ExitRequest;
import static org.junit.jupiter.api.Assertions.*;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class InteractiveShellRunnerTests {


    public PromptProvider dummyPromptProvider() {
        return () -> new AttributedString("dummy-shell:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

    @Test
    public void testExitShortcuts() throws Exception {

        PipedInputStream in = new PipedInputStream();
        final PipedOutputStream outIn = new PipedOutputStream(in);
        Terminal terminal = TerminalBuilder.builder()
                .streams(in, System.out)
                .nativeSignals(true)
                .build();
        LineReaderBuilder builder =
                LineReaderBuilder.builder()
                        .terminal(terminal);

        LineReader reader = builder.build();
        InteractiveShellRunner.JLineInputProvider jLineInputProvider = new InteractiveShellRunner.JLineInputProvider(reader, dummyPromptProvider());

        // clear with ctrl + c
        outIn.write('a');
        outIn.write(3); // ctrl + c
        assertDoesNotThrow(jLineInputProvider::readInput);

        // exit with ctrl + c
        outIn.write(3);
        assertThrows(ExitRequest.class, jLineInputProvider::readInput);

        // exit with ctrl + d
        outIn.write(4); // ctrl + d
        assertThrows(ExitRequest.class, jLineInputProvider::readInput);
    }
}
