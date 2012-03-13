package org.springframework.roo.shell.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.roo.shell.Completion;
import org.springframework.roo.shell.Converter;
import org.springframework.roo.shell.MethodTarget;

/**
 * {@link Converter} for {@link Date}.
 *
 * @author Stefan Schmidt
 * @since 1.0
 */
public class DateConverter implements Converter<Date> {

	// Fields
	private final DateFormat dateFormat;

	public DateConverter() {
		this.dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
	}

	public DateConverter(final DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Date convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		try {
			return dateFormat.parse(value);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Could not parse date: " + e.getMessage());
		}
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		return false;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return Date.class.isAssignableFrom(requiredType);
	}
}