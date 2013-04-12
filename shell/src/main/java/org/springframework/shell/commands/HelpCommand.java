/*
 * Copyright 2011-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.commands;

import static org.springframework.shell.core.CommandConstants.HELP_COMMAND;

import org.springframework.beans.factory.annotation.Autowired;
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
 */
@Component
public class HelpCommand implements CommandMarker {

    private final JLineShellComponent shell;
    
    @Autowired
    public HelpCommand(JLineShellComponent shell) {
        this.shell = shell;
    }
    
	@CliCommand(value = HELP_COMMAND, help = "list all commands usage")
	public void obtainHelp(@CliOption(key = { "", "command" }, optionContext = "availableCommands", help = "Command name to provide help for") String buffer) {
		SimpleParser parser = shell.getSimpleParser();
		parser.obtainHelp(buffer);
	}

}
