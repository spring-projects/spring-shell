package org.springframework.roo.shell.converters;

import java.util.List;

import org.springframework.roo.shell.Completion;
import org.springframework.roo.shell.Converter;
import org.springframework.roo.shell.MethodTarget;

/**
 * {@link Converter} for {@link Short}.
 *
 * @author Stefan Schmidt
 * @since 1.0
 */
public class ShortConverter implements Converter<Short> {

	public Short convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		return new Short(value);
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		return false;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return Short.class.isAssignableFrom(requiredType) || short.class.isAssignableFrom(requiredType);
	}
}