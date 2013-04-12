/*
 * Copyright 2011-2013 the original author or authors.
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
package org.springframework.shell.test.commands;

import static org.springframework.test.shell.commands.CustomCommandConstants.REVERSE_STRING_COMMAND;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;


/**
 * Provides a listing of commands known to the shell.
 * 
 * @author David Winterfeldt
 */
@Component
public class ReverseStringCommand implements CommandMarker {

	@CliCommand(value = REVERSE_STRING_COMMAND, help = "Reverse String input.")
	public String reverse(@CliOption(key = { "value" }, mandatory=true, help = "Value to reverse") String value) {
		return new StringBuilder(value).reverse().toString();
	}

}
