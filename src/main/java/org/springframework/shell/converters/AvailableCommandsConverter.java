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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

/**
 * Available commands converter.
 * 
 * @author Ben Alex
 * @author Eric Bottard
 * @since 1.0
 */
@Component
public class AvailableCommandsConverter implements Converter<String> {

	@Autowired
	private JLineShellComponent shell;

	@Override
	public String convertFromText(final String text, final Class<?> requiredType, final String optionContext) {
		return text;
	}

	@Override
	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return String.class.isAssignableFrom(requiredType) && optionContext.contains("availableCommands");
	}

	@Override
	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType,
			final String existingData, final String optionContext, final MethodTarget target) {

		for (String s : shell.getSimpleParser().getEveryCommand()) {
			completions.add(new Completion(s));
		}
		return true;
	}
}
