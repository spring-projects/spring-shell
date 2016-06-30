package org.springframework.shell.samples.hiworld.commands;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

/**
 * Separate CommandMarker implementation to demonstrate mixing
 * XML and Java-based Configurations 
 * 
 * @author Robin Howlett
 *
 */
@Component
public class HiWorldCommands implements CommandMarker {
	
	@CliCommand(value = "hi", help = "Print a Hi World! message")
	public String hi() {
		return "Hi World!";
	}
}
