package org.springframework.shell.commands;

import java.io.IOException;

/**
 * Operations type to allow execution of native OS commands from the Spring Roo
 * shell.
 * 
 * @author Stefan Schmidt
 * @since 1.2.0
 */
public interface OsOperations {

    /**
     * Attempts the execution of a commands and delegates the output to the
     * standard logger.
     * 
     * @param command the command to execute
     * @throws IOException if an error occurs
     */
    void executeCommand(String command) throws IOException;
}