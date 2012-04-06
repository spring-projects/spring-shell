package org.springframework.shell.commands;

import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;
import org.springframework.roo.shell.SimpleParser;
import org.springframework.stereotype.Component;

/**
 * Provides a listing of commands known to the shell.
 * 
 * @author Ben Alex
 * @author Mark Pollack
 *
 */
@Component
public class HelpCommands extends SimpleParser implements CommandMarker {

	
	// Taken from SimpleParserComponent in the Roo code base
	@Override
	@CliCommand(value = "help", help = "Shows system help")
	public void obtainHelp(
		@CliOption(key = { "", "command" }, optionContext = "availableCommands", help = "Command name to provide help for") final String buffer) {		
		super.obtainHelp(buffer);
	}
}
