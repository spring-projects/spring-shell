package org.springframework.shell.samples.helloworld.commands;

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
		@CliOption(key = { "message" }, mandatory = true, help = "The hello world message") final String message,
		@CliOption(key = { "name" }, mandatory = true, help = "The hello world name ") final String name,
		@CliOption(key = { "time" }, mandatory = false, help = "The hello world time ") final String time) {		
		System.out.println("Hello world " + message + "," + name + ". time:" + time);
	}
}
