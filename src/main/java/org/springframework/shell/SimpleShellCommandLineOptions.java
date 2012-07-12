/*
 * Copyright (C) 2011 VMware, Inc.  All rights reserved. -- VMware Confidential
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
import org.springframework.roo.support.logging.HandlerUtils;
import org.springframework.shell.commands.OsCommands;

/**
 * Not really used much, but keeping for future use
 * 
 * @author vnagaraja
 */
public class SimpleShellCommandLineOptions {
	
  private static final Logger LOGGER = HandlerUtils.getLogger(SimpleShellCommandLineOptions.class);
  private static final int DEFAULT_HISTORY_SIZE = 3000;
	String[] executeThenQuit = null;
	Map<String, String> extraSystemProperties = new HashMap<String, String>();
	int historySize = DEFAULT_HISTORY_SIZE;

	public static SimpleShellCommandLineOptions parseCommandLine(String[] args) throws IOException {
		SimpleShellCommandLineOptions options = new SimpleShellCommandLineOptions();
		List<String> commands = new ArrayList<String>();
		int i = 0;
		while (i < args.length) {
			String arg = args[i++];
			if (arg.equals("--environment")) {
				String environment = args[i++];
				options.extraSystemProperties.put("napa.application.profile", environment);
			}
			else if (arg.equals("--cmdfile")) {
				File f = new File(args[i++]);
				try {
				  commands.addAll(FileUtils.readLines(f));
				} catch (IOException e) {
					LOGGER.warning("Could not read lines from command file: " +  e.getMessage());
				}
			}
			else if (arg.equals("--histsize")) {
				String histSizeArg = args[i++];				
				try {				
					int histSize = Integer.parseInt(histSizeArg);
					if (histSize <= 0) {
						LOGGER.warning("histsize option must be > 0, using default value of " + DEFAULT_HISTORY_SIZE);
					} else {
					  options.historySize = histSize;
					}
				} catch (NumberFormatException e) {
          LOGGER.warning("Unable to parse histsize value [" + histSizeArg + "] to an integer ");
				}
			}
			else if (arg.equals("--help")) {
				printUsage();
				System.exit(0);
			}
			else {
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
				//add any command line commands after the commands loaded from the file
				commands.add(s.trim());
			}
		}

		if (commands.size() > 0)
			options.executeThenQuit = commands.toArray(new String[commands.size()]);

		return options;
	}
	
	private static void printUsage(){
		System.out.println("Usage:");
		System.out.println("java -jar {jarname} --help --histsize {size} --cmdfile {file}");
	}
}
