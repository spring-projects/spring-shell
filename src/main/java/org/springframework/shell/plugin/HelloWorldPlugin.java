package org.springframework.shell.plugin;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"org.springframework.shell.commands", "org.springframework.shell.converters"})
public class HelloWorldPlugin {
	

	
}
