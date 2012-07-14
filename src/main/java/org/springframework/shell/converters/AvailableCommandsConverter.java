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
