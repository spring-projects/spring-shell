package org.springframework.shell.commands;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.stereotype.Component;

/**
 * Command type to allow execution of native OS commands from the Spring Roo
 * shell.
 * 
 * @author Stefan Schmidt
 * @since 1.2.0
 */
@Component
public class OsCommands implements CommandMarker {

    private static final Logger LOGGER = HandlerUtils
            .getLogger(OsCommands.class);

    private OsOperations osOperations = new OsOperationsImpl();

    @CliCommand(value = "!", help = "Allows execution of operating system (OS) commands.")
    public void command(
            @CliOption(key = { "", "command" }, mandatory = false, specifiedDefaultValue = "", unspecifiedDefaultValue = "", help = "The command to execute") final String command) {

    	System.out.println("command is:" + command);
        if (command != null && command.length() > 0) {
            try {
                osOperations.executeCommand(command);
            }
            catch (final IOException e) {
                LOGGER.severe("Unable to execute command " + command + " ["
                        + e.getMessage() + "]");
            }
        }
    }
}