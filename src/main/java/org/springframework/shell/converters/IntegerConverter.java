package org.springframework.shell.converters;

import java.util.List;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

/**
 * {@link Converter} for {@link Integer}.
 * 
 * @author Stefan Schmidt
 * @since 1.0
 */
@Component
public class IntegerConverter implements Converter<Integer> {

	@Override
	public Integer convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		return new Integer(value);
	}

	@Override
	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType,
			final String existingData, final String optionContext, final MethodTarget target) {
		return false;
	}

	@Override
	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return Integer.class.isAssignableFrom(requiredType) || int.class.isAssignableFrom(requiredType);
	}
}