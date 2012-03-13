package org.springframework.roo.shell.converters;

import java.util.List;

import org.springframework.roo.shell.Completion;
import org.springframework.roo.shell.Converter;
import org.springframework.roo.shell.MethodTarget;

/**
 * {@link Converter} for {@link String}.
 *
 * @author Ben Alex
 * @since 1.0
 */
public class StringConverter implements Converter<String> {

	public String convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		return value;
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		return false;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return String.class.isAssignableFrom(requiredType) && (optionContext == null || !optionContext.contains("disable-string-converter"));
	}
}
