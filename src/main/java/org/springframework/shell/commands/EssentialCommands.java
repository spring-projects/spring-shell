package org.springframework.shell.commands;

import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CommandMarker;
import org.springframework.roo.shell.ExitShellRequest;
import org.springframework.stereotype.Component;

@Component
public class EssentialCommands implements CommandMarker { 
	
	@CliCommand(value={"exit", "quit"}, help="Exits the shell")
	public ExitShellRequest quit() {
		return ExitShellRequest.NORMAL_EXIT;
	}

}
