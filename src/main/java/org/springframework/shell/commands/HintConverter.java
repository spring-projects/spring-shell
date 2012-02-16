package org.springframework.shell.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.shell.Completion;
import org.springframework.roo.shell.Converter;
import org.springframework.roo.shell.MethodTarget;
import org.springframework.stereotype.Component;

/**
 * {@link Converter} for {@link String} that understands the "topics" option context.
 *
 * @author Ben Alex
 * @since 1.1
 */
@Component
public class HintConverter implements Converter<String> {

	// Fields
	@Autowired private HintOperations hintOperations;

	public String convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		return value;
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		for (String currentTopic : hintOperations.getCurrentTopics()) {
			completions.add(new Completion(currentTopic));
		}
		return false;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return String.class.isAssignableFrom(requiredType) && optionContext.contains("topics");
	}
}
