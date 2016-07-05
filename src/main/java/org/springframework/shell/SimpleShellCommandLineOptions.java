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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.springframework.shell.support.logging.HandlerUtils;

/**
 * Used to pass in command line options to customize the shell on launch
 * 
 * @author vnagaraja
 */
public class SimpleShellCommandLineOptions {

	private static final Logger LOGGER = HandlerUtils.getLogger(SimpleShellCommandLineOptions.class);
	public static final int DEFAULT_HISTORY_SIZE = 3000;
	String[] executeThenQuit = null;
	Map<String, String> extraSystemProperties = new HashMap<String, String>();
	int historySize = DEFAULT_HISTORY_SIZE;
	boolean disableCommands;
	boolean disableColor = false;

	public static CommandLine parseCommandLine(String[] args)
			throws IOException {
		if (args == null) {
			args = new String[] {};
		}
		SimpleShellCommandLineOptions options = new SimpleShellCommandLineOptions();
		List<String> commands = new ArrayList<String>();
		int i = 0;
		while (i < args.length) {
			String arg = args[i++];
			if (arg.equals("--profiles")) {
				try {
					String profiles = args[i++];
					options.extraSystemProperties.put("spring.profiles.active",	profiles);
				} catch (ArrayIndexOutOfBoundsException e) {
					LOGGER.warning("No value specified for --profiles option");
				}
			} else if (arg.equals("--cmdfile")) {
				try {
					File f = new File(args[i++]);
					commands.addAll(FileUtils.readLines(f));
				} catch (IOException e) {
					LOGGER.warning("Could not read lines from command file: " + e.getMessage());
				} catch (ArrayIndexOutOfBoundsException e) {
					LOGGER.warning("No value specified for --cmdfile option");
				}
			} else if (arg.equals("--histsize")) {
				try {
					String histSizeArg = args[i++];
					int histSize = Integer.parseInt(histSizeArg);
					if (histSize <= 0) {
						LOGGER.warning("histsize option must be > 0, using default value of " + DEFAULT_HISTORY_SIZE);
					} else {
						options.historySize = histSize;
					}
				} catch (NumberFormatException e) {
					LOGGER.warning("Unable to parse histsize value to an integer ");
				} catch (ArrayIndexOutOfBoundsException ae) {
					LOGGER.warning("No value specified for --histsize option");
				}
			} else if (arg.equals("--disableInternalCommands")) {
				options.disableCommands = true;
			} else if (arg.equals("--disableColor")) {
				options.disableColor = true;
			} else if (arg.equals("--help")) {
				printUsage();
				System.exit(0);
			} else {
				i--;
				break;
			}
		}

		StringBuilder sb = new StringBuilder();
		for (; i < args.length; i++) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(args[i]);
		}

		if (sb.length() > 0) {
			String[] cmdLineCommands = sb.toString().split(";");
			for (String s : cmdLineCommands) {
				// add any command line commands after the commands loaded from the file
				commands.add(s.trim());
			}
		}

		if (commands.size() > 0) {
			options.executeThenQuit = commands.toArray(new String[commands.size()]);
		}

		for (Map.Entry<String, String> entry : options.extraSystemProperties.entrySet()) {
			System.setProperty(entry.getKey(), entry.getValue());
		}

		return new CommandLine(args, options.historySize, options.executeThenQuit, options.disableCommands,
				options.disableColor);
	}

	private static void printUsage() {
		System.out.println("Usage:  --help --histsize [size] --cmdfile [file name] --profiles [comma-separated list of profile names]");
	}
}
