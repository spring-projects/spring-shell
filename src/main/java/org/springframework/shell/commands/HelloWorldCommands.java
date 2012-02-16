package org.springframework.shell.commands;

import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldCommands implements CommandMarker {

	
	@CliAvailabilityIndicator({"hw help"})
	public boolean isCommandAvailable() {
		return true;
	}
	
	@CliCommand(value = "hw echo", help = "Print a hello world message")
	public void config(
		@CliOption(key = { "message" }, mandatory = true, help = "The hello world message") final String message) {		
		System.out.println("Hello world " + message);
	}
}
