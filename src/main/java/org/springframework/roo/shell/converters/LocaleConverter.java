package org.springframework.roo.shell.converters;

import java.util.List;
import java.util.Locale;

import org.springframework.roo.shell.Completion;
import org.springframework.roo.shell.Converter;
import org.springframework.roo.shell.MethodTarget;

/**
 * {@link Converter} for {@link Locale}. Supports locales
 * with ISO-639 (ie 'en') or a combination of ISO-639 and
 * ISO-3166 (ie 'en_AU').
 *
 * @author Stefan Schmidt
 * @since 1.1
 */
public class LocaleConverter implements Converter<Locale> {

	public Locale convertFromText(final String value, final Class<?> requiredType, final String optionContext) {
		if (value.length() == 2) {
			// In case only a simpele ISO-639 code is provided we use that code also for the country (ie 'de_DE')
			return new Locale(value, value.toUpperCase());
		} else if (value.length() == 5) {
			String[] split = value.split("_");
			return new Locale(split[0], split[1]);
		} else {
			return null;
		}
	}

	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> requiredType, final String existingData, final String optionContext, final MethodTarget target) {
		return false;
	}

	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return Locale.class.isAssignableFrom(requiredType);
	}
}