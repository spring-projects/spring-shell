/**
 * 
 */
package org.springframework.shell.commands.test;

import org.springframework.shell.converters.CliPrinterTypeConverter;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliPrinter;
import org.springframework.stereotype.Component;

/**
 * Commands for customized outputs
 * 
 * @author robin
 */
@Component
public class PrinterCommands implements CommandMarker {

	@CliCommand(value={ "printer-test" })
	public TestPrinterResource printerTest(@CliPrinter CliPrinterTypeConverter<TestPrinterResource> printer) {
		return new TestPrinterResource("12345", "TestName");
	};
	
	public class TestPrinterResource {
		
		private final String id;
		private final String name;
		
		public TestPrinterResource() {
			this(null, null);
		}
		
		public TestPrinterResource(String id, String name) {
			this.id = id;
			this.name = name;			
		}

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "TestPrinterResource [id=" + id + ", name=" + name + "]";
		}
		
	}
	
}
