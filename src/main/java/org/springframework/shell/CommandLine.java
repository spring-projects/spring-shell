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


/**
 * Encapsulates the list of argument passed to the shell.
 * 
 * @author Mark Pollack
 */
public class CommandLine {
	
	private String[] args;
	private int historySize;
	private String[] shellCommandsToExecute;

	/**
	 * Construct a new CommandLine  
	 * @param args an array of strings from main(String[] args)
	 * @param historySize the size of this history buffer
	 * @param shellCommandsToExecute semi-colon delimited list of commands for the shell to execute
	 */
	public CommandLine(String[] args, int historySize, String[] shellCommandsToExecute) {
		this.args = args;
		this.historySize = historySize;
		this.shellCommandsToExecute = shellCommandsToExecute;
	}

	/**
	 * Return the command line arguments
	 * @return the command line arguments
	 */
	public String[] getArgs() {
		return args;
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
		return shellCommandsToExecute;
	}

}
