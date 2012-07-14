package org.springframework.shell.converters;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;

/**
 * {@link Converter} for {@link BigDecimal}.
 *
 * @author Stefan Schmidt
 * @since 1.0
 */
public class BigDecimalConverter implements Converter<BigDecimal> {

	public BigDecimal convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		return new BigDecimal(value);
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		return false;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return BigDecimal.class.isAssignableFrom(requiredType);
	}
}