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

import static org.springframework.shell.support.util.OsUtils.LINE_SEPARATOR;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Commands related to system properties 
 *
 */
@Component
public class SystemPropertyCommands implements CommandMarker { 

	@CliCommand(value = { "system properties" }, help = "Shows the shell's properties")
	public String props() {
		final Set<String> data = new TreeSet<String>(); // For repeatability
		for (final Entry<Object, Object> entry : System.getProperties().entrySet()) {
			data.add(entry.getKey() + " = " + entry.getValue());
		}

		return StringUtils.collectionToDelimitedString(data, LINE_SEPARATOR) + LINE_SEPARATOR;
	}

}
