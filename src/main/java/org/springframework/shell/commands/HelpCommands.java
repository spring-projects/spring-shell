package org.springframework.shell.commands;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.core.SimpleParser;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 * Provides a listing of commands known to the shell.
 * 
 * @author Ben Alex
 * @author Mark Pollack
 * @author Jarred Li
 *
 */
@Component
public class HelpCommands implements CommandMarker, ApplicationContextAware {

	private ApplicationContext ctx;

	@CliCommand(value = "help", help = "list all commands usage")
	public void obtainHelp(@CliOption(key = { "", "command" }, optionContext = "availableCommands", help = "Command name to provide help for") String buffer) {
		JLineShellComponent shell = ctx.getBean("shell", JLineShellComponent.class);
		SimpleParser parser = shell.getSimpleParser();
		parser.obtainHelp(buffer);
	}


	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}
}
