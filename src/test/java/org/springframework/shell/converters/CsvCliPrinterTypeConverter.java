/**
 * 
 */
package org.springframework.shell.converters;

import org.springframework.shell.commands.test.PrinterCommands;
import org.springframework.shell.commands.test.PrinterCommands.TestPrinterResource;
import org.springframework.shell.converters.CliPrinterTypeConverter;

/**
 * Spring Type Converter to convert a {@link TestPrinterResource} to a csv String 
 * 
 * @author robin
 */
public class CsvCliPrinterTypeConverter implements CliPrinterTypeConverter<PrinterCommands.TestPrinterResource> {

	@Override
	public String convert(TestPrinterResource source) {
		return source.getId() + "," + source.getName();
	}

}
