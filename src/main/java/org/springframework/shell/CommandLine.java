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
package org.springframework.shell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Encapsulates the list of argument passed to the shell.
 * 
 * @author Mark Pollack
 * @author Rodrigo Meneses
 * 
 */
public class CommandLine {
	
	private List<String> argList = new ArrayList<String> ();
	private List<String> shellExecuteList =  new ArrayList<String> ();
	
	private int historySize;
	
	private boolean disableInteralCommands;

	/**
	 * Construct a new CommandLine  
	 * @param args an array of strings from main(String[] args)
	 * @param historySize the size of this history buffer
	 * @param shellCommandsToExecute semi-colon delimited list of commands for the shell to execute
	 */
	public CommandLine(String[] args, int historySize, String[] shellCommandsToExecute) {
		this(args,historySize,shellCommandsToExecute, false);
	}

	
	
	/**
	 * Construct a new CommandLine  
	 * @param args an array of strings from main(String[] args)
	 * @param historySize the size of this history buffer
	 * @param shellCommandsToExecute semi-colon delimited list of commands for the shell to execute
	 * @param disableInteralCommands if true, do not load the built-in shell commands
	 */
	public CommandLine(String[] args, int historySize, String[] shellCommandsToExecute, boolean disableInteralCommands) {
		this.argList = Arrays.asList(args);
		if (shellCommandsToExecute!=null)
			this.shellExecuteList = Arrays.asList(shellCommandsToExecute);
		this.historySize = historySize;
		this.disableInteralCommands = disableInteralCommands;
	}

	/**
	 * Return the command line arguments
	 * @return the command line arguments
	 */
	public String[] getArgs() {
		if (argList.size() == 0)
			return null;
		return  argList.toArray(new String[argList.size()]);
	}

	/**
	 * @return the historySize
	 */
	public int getHistorySize() {
		return historySize;
	}

	/**
	 * @return the shellCommandsToExecute
	 */
	public String[] getShellCommandsToExecute() {
		if (shellExecuteList.size()==0)
			return null;
		return shellExecuteList.toArray(new String[shellExecuteList.size()]);
	}
	
	/**
	 * 
	 * @return the disableInteralCommands value
	 */
	public boolean getDisableInternalCommands() {
		return disableInteralCommands;
	}
	
	/**
	 *remove a parameter along with its value from the shell to execute commands list
	 * @param arg argument name
	 * @param val argument value
	 */
	public void removeFromShell (String arg, String val) {
		List<String> newShellList = new ArrayList<String> ();
		for (String shellCommand : shellExecuteList) {
			
			String cmd = shellCommand.replaceFirst(arg + " " + val, "").trim();
			if (cmd.length()>0)
				newShellList.add(cmd);
		}
		this.shellExecuteList = newShellList;
		
	}
}
