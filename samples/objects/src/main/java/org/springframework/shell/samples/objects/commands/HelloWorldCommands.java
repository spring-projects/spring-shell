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
package org.springframework.shell.samples.objects.commands;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import org.springframework.shell.samples.objects.data.Foo;

import java.util.Arrays;

@Component
public class HelloWorldCommands implements CommandMarker {
	
	@CliCommand(value = "hw object", help = "Print a simple hello world message from a converted object")
	public String object(
		@CliOption(key = { "message" }, mandatory = true, help = "The hello world message") final Foo message){		
		return "Hello.  Your special converted message is " + message;
	}

	@CliCommand(value = "hw array", help = "Print a simple hello world message from a converted array")
	public String array(
		@CliOption(key = { "message" }, mandatory = true, help = "The hello world message") final Foo[] array){
		return "Hello.  Your special converted message is " + Arrays.asList(array);
	}
}