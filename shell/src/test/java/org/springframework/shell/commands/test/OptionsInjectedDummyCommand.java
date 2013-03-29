package org.springframework.shell.commands.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.CommandLine;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

@Component
public class OptionsInjectedDummyCommand implements CommandMarker {

	@Autowired
	private CommandLine commandLine;
	
	@CliCommand("do nothing")
	public String simple() {		
		return "foo";
	}
	
	public CommandLine getCommandLine() {
		return commandLine;
	}
	
}
