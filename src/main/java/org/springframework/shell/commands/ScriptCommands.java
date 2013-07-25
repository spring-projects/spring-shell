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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.shell.support.util.IOUtils;
import org.springframework.shell.support.util.MathUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ScriptCommands implements CommandMarker {
    protected final Logger logger = HandlerUtils.getLogger(getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JLineShellComponent shell;
	@CliCommand(value = { "script" }, help = "Parses the specified resource file and executes its commands")
	public void script(
		@CliOption(key = { "", "file" }, help = "The file to locate and execute", mandatory = true) final File script,
		@CliOption(key = "lineNumbers", mandatory = false, specifiedDefaultValue = "true", unspecifiedDefaultValue = "false", help = "Display line numbers when executing the script") final boolean lineNumbers) {

		Assert.notNull(script, "Script file to parse is required");
		double startedNanoseconds = System.nanoTime();
		final InputStream inputStream = openScript(script);

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			int i = 0;
			while ((line = in.readLine()) != null) {
				i++;
				if (lineNumbers) {
					logger.fine("Line " + i + ": " + line);
				} else {
					logger.fine(line);
				}
				if (!"".equals(line.trim())) {
					boolean success = shell.executeScriptLine(line);
					if (success && ((line.trim().startsWith("q") || line.trim().startsWith("ex")))) {
						break;
					} else if (!success) {
						// Abort script processing, given something went wrong
						throw new IllegalStateException("Script execution aborted");
					}
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			IOUtils.closeQuietly(inputStream, in);
			double executionDurationInSeconds = (System.nanoTime() - startedNanoseconds) / 1000000000D;
			logger.fine("Script required " + MathUtils.round(executionDurationInSeconds, 3) + " seconds to execute");
		}
	}

	/**
	 * Opens the given script for reading
	 *
	 * @param script the script to read (required)
	 * @return a non-<code>null</code> input stream
	 */
	private InputStream openScript(final File script) {
		try {
			return new BufferedInputStream(new FileInputStream(script));
		} catch (final FileNotFoundException fnfe) {
			// Try to find the script via the classloader
			final Collection<URL> urls = findResources(script.getName());

			// Handle search failure
			Assert.notNull(urls, "Unexpected error looking for '" + script.getName() + "'");

			// Handle the search being OK but the file simply not being present
			Assert.notEmpty(urls, "Script '" + script + "' not found on disk or in classpath");
			Assert.isTrue(urls.size() == 1, "More than one '" + script + "' was found in the classpath; unable to continue");
			try {
				return urls.iterator().next().openStream();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}
	
	protected Collection<URL> findResources(final String path) {
		try {
			Resource[] resources = applicationContext.getResources(path);
			Collection<URL> list = new ArrayList<URL>(resources.length);
			for (Resource resource : resources) {
				list.add(resource.getURL());
			}
			return list;
		} catch (IOException ex) {
			logger.fine("Cannot find path " + path);
			// return Collections.emptyList();
			throw new RuntimeException(ex);
		}
	}

}
