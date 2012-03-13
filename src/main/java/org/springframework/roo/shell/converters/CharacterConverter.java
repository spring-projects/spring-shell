package org.springframework.roo.shell.converters;

import java.util.List;

import org.springframework.roo.shell.Completion;
import org.springframework.roo.shell.Converter;
import org.springframework.roo.shell.MethodTarget;

/**
 * {@link Converter} for {@link Character}.
 *
 * @author Stefan Schmidt
 * @since 1.0
 */
public class CharacterConverter implements Converter<Character> {

	public Character convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		return value.charAt(0);
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		return false;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return Character.class.isAssignableFrom(requiredType) || char.class.isAssignableFrom(requiredType);
	}
}