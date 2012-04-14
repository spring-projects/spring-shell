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

import org.apache.commons.io.FileUtils;

/**
 * Not really used much, but keeping for future use
 * 
 * @author vnagaraja
 */
public class SimpleShellCommandLineOptions {
	String[] executeThenQuit = null;
	Map<String, String> extraSystemProperties = new HashMap<String, String>();
	int historySize = 3000;

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
				commands.addAll(FileUtils.readLines(f));
			}
			else if (arg.equals("--histsize")) {
				options.historySize = Integer.parseInt(args[i++]);
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
