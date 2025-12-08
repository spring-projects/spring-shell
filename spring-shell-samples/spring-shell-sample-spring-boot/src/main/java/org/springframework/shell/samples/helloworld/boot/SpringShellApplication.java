package org.springframework.shell.samples.helloworld.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

@SpringBootApplication
public class SpringShellApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringShellApplication.class, args);
	}

	@Command(name = "hello", description = "Say hello to a given name", group = "Greetings",
			help = "A command that greets the user with 'Hello ${name}!'. Usage: hello [-n | --name]=<name>")
	public void sayHello(@Option(shortName = 'n', longName = "name", description = "the name of the person to greet",
			defaultValue = "World") String name) {
		System.out.println("Hello " + name + "!");
	}

}