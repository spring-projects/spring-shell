/**
 * 
 */
package org.springframework.shell.core;

import static org.springframework.util.Assert.notNull;

import java.io.Serializable;

import org.springframework.shell.converters.CliPrinterTypeConverter;

/**
 * Decorator around command results that may type convert the result to a custom String format using the 
 * provided {@link CliPrinterTypeConverter}
 * 
 * @author robin
 */
public class CliPrinterResult<T extends Object> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final T result;
	private final CliPrinterTypeConverter<T> printer;
	
	private String output;
	
	public CliPrinterResult(final T result) {
		this(result, null);
	}
	
	public CliPrinterResult(final T result, CliPrinterTypeConverter<T> printer) {
		notNull(result, "A non-null result instance must be provided");
		
		this.result = result;
		this.printer = printer;
	}
	
	@Override
	public String toString() {
		if (getPrinter() != null) {
			if (output == null) {
				output = getPrinter().convert(result);
			}
			
			return output;
		}
		
		return result.toString();
	}

	/**
	 * @return the output
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(String output) {
		this.output = output;
	}

	/**
	 * @return the result
	 */
	public T getResult() {
		return result;
	}

	/**
	 * @return the printer
	 */
	public CliPrinterTypeConverter<T> getPrinter() {
		return printer;
	}
	
}
