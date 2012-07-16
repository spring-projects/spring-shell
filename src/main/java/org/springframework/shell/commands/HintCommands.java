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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class HintCommands implements CommandMarker {

	// Fields
	@Autowired private HintOperations hintOperations;

	//TODO: figure out how to provide hint
	//@CliCommand(value = "hint", help = "Provides step-by-step hints and context-sensitive guidance")
	public String hint(
		@CliOption(key = { "topic", "" }, mandatory = false, unspecifiedDefaultValue = "", optionContext = "disable-string-converter,topics", help = "The topic for which advice should be provided") final String topic) {

		return hintOperations.hint(topic);
	}
}
