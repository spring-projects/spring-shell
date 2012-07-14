package org.springframework.shell.converters;

import java.util.List;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;

/**
 * {@link Converter} for {@link Long}.
 *
 * @author Stefan Schmidt
 * @since 1.0
 */
public class LongConverter implements Converter<Long> {

	public Long convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		return new Long(value);
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		return false;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return Long.class.isAssignableFrom(requiredType) || long.class.isAssignableFrom(requiredType);
	}
}