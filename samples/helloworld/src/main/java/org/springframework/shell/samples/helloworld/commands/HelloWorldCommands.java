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
	
	@CliCommand(value = "hw-echo", help = "Print a hello world message")
	public void config(
		@CliOption(key = { "message" }, mandatory = true, help = "The hello world message") final String message,
		@CliOption(key = { "name1"}, mandatory = true, help = "The hello world name1 ") final String name1,
		@CliOption(key = { "name2","name22" }, mandatory = true, help = "The hello world name2 ") final String name2,
		@CliOption(key = { "time" }, mandatory = false, specifiedDefaultValue="now", help = "The hello world time ") final String time,
		@CliOption(key = { "location" }, mandatory = false, help = "The hello world location ") final String location) {		
		System.out.println("Hello world " + message + ", name1: " + name1 + 
				", name2:" + name2 + ". time:" + time + ".location: " + location);
	}
}
