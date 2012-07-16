package org.springframework.shell.samples.helloworld.commands;


import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldCommands implements CommandMarker {

	
	@CliAvailabilityIndicator({"hw echo"})
	public boolean isCommandAvailable() {
		return true;
	}
	
	@CliCommand(value = "hw echo", help = "Print a hello world message")
	public void hello(
		@CliOption(key = { "message" }, mandatory = true, help = "The hello world message") final String message,
		@CliOption(key = { "name1"}, mandatory = true, help = "The hello world name1 ") final String name1,
		@CliOption(key = { "name2","name22" }, mandatory = true, help = "The hello world name2 ") final String name2,
		@CliOption(key = { "time" }, mandatory = false, specifiedDefaultValue="now", help = "The hello world time ") final String time,
		@CliOption(key = { "location" }, mandatory = false, help = "The hello world location ") final String location) {		
		System.out.println("Hello world " + message + ", name1: " + name1 + 
				", name2:" + name2 + ". time:" + time + ".location: " + location);
	}
	
	@CliCommand(value = "hw auto", help = "Print a hello world message")
	public void autao(
		@CliOption(key = { "message" }, mandatory = true, help = "The hello world message") final MessageType message){		
		System.out.println("Hello world " + message);
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
