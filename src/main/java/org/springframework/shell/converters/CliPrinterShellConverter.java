/**
 * 
 */
package org.springframework.shell.converters;

import static org.springframework.util.Assert.notNull;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.shell.core.annotation.CliPrinter;
import org.springframework.stereotype.Component;

/**
 * Spring Shell converter to convert command @{@link CliPrinter}-annotated arguments 
 * to {@link CliPrinterTypeConverter}s
 * 
 * @author robin
 */
@Component
public class CliPrinterShellConverter implements Converter<CliPrinterTypeConverter<?>> {
	
	public static final String CLI_OUTPUT_FLAG = "o";

	// beans must be named "xyzOutputConverter" where "xyz" is the @CliPrinter command argument value
	private static final String OUTPUT_CONVERTER_SUFFIX = "OutputConverter";
	
	/*
	 * Spring will add all beans of type CliPrinterTypeConverter to this map
	 * the key will be like "xyzOutputConverter", matching the bean name
	 */
	@Autowired(required=false)
	private Map<String, CliPrinterTypeConverter<?>> converters;

	/* (non-Javadoc)
	 * @see org.springframework.shell.core.Converter#supports(java.lang.Class, java.lang.String)
	 */
	@Override
	public boolean supports(Class<?> type, String optionContext) {
		return CliPrinterTypeConverter.class.isAssignableFrom(type);
	}

	/* (non-Javadoc)
	 * @see org.springframework.shell.core.Converter#convertFromText(java.lang.String, java.lang.Class, java.lang.String)
	 */
	@Override
	public CliPrinterTypeConverter<?> convertFromText(String format, Class<?> targetType, String optionContext) {
		notNull(format);
		notNull(converters);
		
		String converterName = format.toLowerCase() + OUTPUT_CONVERTER_SUFFIX;
		
		if (converters.containsKey(converterName)) {
			return converters.get(converterName);
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.springframework.shell.core.Converter#getAllPossibleValues(java.util.List, java.lang.Class, java.lang.String, java.lang.String, org.springframework.shell.core.MethodTarget)
	 */
	@Override
	public boolean getAllPossibleValues(List<Completion> completions,
			Class<?> targetType, String existingData, String optionContext,
			MethodTarget target) {
		return false;
	}

}
