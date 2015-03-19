/**
 * 
 */
package org.springframework.shell.converters;

import org.springframework.core.convert.converter.Converter;

/**
 * Spring Type Converter for Spring Shell command results to be converted to
 *
 * @author robin
 */
public interface CliPrinterTypeConverter<S> extends Converter<S, String> {

}
