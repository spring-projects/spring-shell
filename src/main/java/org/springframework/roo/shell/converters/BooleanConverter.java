package org.springframework.roo.shell.converters;

import java.util.List;

import org.springframework.roo.shell.Completion;
import org.springframework.roo.shell.Converter;
import org.springframework.roo.shell.MethodTarget;

/**
 * {@link Converter} for {@link Boolean}.
 *
 * @author Stefan Schmidt
 * @since 1.0
 */
public class BooleanConverter implements Converter<Boolean> {

	public Boolean convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		if ("true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value)) {
			return true;
		} else if ("false".equalsIgnoreCase(value) || "0".equals(value) || "no".equalsIgnoreCase(value)) {
			return false;
		} else {
			throw new IllegalArgumentException("Cannot convert " + value + " to type Boolean.");
		}
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		completions.add(new Completion("true"));
		completions.add(new Completion("false"));
		completions.add(new Completion("yes"));
		completions.add(new Completion("no"));
		completions.add(new Completion("1"));
		completions.add(new Completion("0"));
		return false;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return Boolean.class.isAssignableFrom(requiredType) || boolean.class.isAssignableFrom(requiredType);
	}
}