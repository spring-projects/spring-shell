package org.springframework.shell.converters;

import java.util.List;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;

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
