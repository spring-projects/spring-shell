package org.springframework.shell.samples.helloworld.commands;


import java.util.logging.Logger;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldCommands implements CommandMarker {

	
	protected final Logger LOG = Logger.getLogger(getClass().getName());
	
	@CliAvailabilityIndicator({"hw echo"})
	public boolean isCommandAvailable() {
		return true;
	}
	
	@CliCommand(value = "hw complex", help = "Print a complex hello world message")
	public void hello(
		@CliOption(key = { "message" }, mandatory = true, help = "The hello world message") final String message,
		@CliOption(key = { "name1"}, mandatory = true, help = "Say hello to the first name") final String name1,
		@CliOption(key = { "name2" }, mandatory = true, help = "Say hello to a second name") final String name2,
		@CliOption(key = { "time" }, mandatory = false, specifiedDefaultValue="now", help = "When you are saying hello") final String time,
		@CliOption(key = { "location" }, mandatory = false, help = "Where you are saying hello") final String location) {		
		LOG.info("Hello " + name1 + " and " + name2 + ". Your special message is "  + message + ". time=[" + time + "] location=[" + location + "]");
	}
	
	@CliCommand(value = "hw enum", help = "Print a simple hello world message from an enumerated value")
	public void eenum(
		@CliOption(key = { "message" }, mandatory = true, help = "The hello world message") final MessageType message){		
		LOG.info("Hello.  You special enumerated message is " + message);
	}
	
	enum MessageType {		
		Type1("type1"),
		Type2("type2"),
		Type3("type3");
		
		private String type;
		
		private MessageType(String type){
			this.type = type;
		}
		
		public String getType(){
			return type;
		}
	}
}
