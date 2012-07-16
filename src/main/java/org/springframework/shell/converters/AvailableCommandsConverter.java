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
package org.springframework.shell.converters;

import java.util.List;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.shell.core.SimpleParser;

/**
 * Available commands converter.
 *
 * @author Ben Alex
 * @since 1.0
 */
public class AvailableCommandsConverter implements Converter<String> {

	public String convertFromText(final String text, final Class<?> requiredType, final String optionContext) {
		return text;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return String.class.isAssignableFrom(requiredType) && "availableCommands".equals(optionContext);
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		if (target.getTarget() instanceof SimpleParser) {
			SimpleParser cmd = (SimpleParser) target.getTarget();

			// Only include the first word of each command
			for (String s : cmd.getEveryCommand()) {
				if (s.contains(" ")) {
					completions.add(new Completion(s.substring(0, s.indexOf(" "))));
				} else {
					completions.add(new Completion(s));
				}
			}
		}
		return true;
	}
}
