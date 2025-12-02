package org.springframework.shell.samples.helloworld.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

@SpringBootApplication
public class SpringShellApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringShellApplication.class, args);
	}

	@Command(name = "hi", description = "Say hi to a given name", group = "greetings",
			help = "A command that greets the user with 'Hi ${name}!' with a configurable suffix. Example usage: hi -s=! John")
	public void sayHi(
			@Argument(index = 0, description = "the name of the person to greet", defaultValue = "world") String name,
			@Option(shortName = 's', longName = "suffix", description = "the suffix of the greeting message",
					defaultValue = "!") char suffix) {
		System.out.println("Hi " + name + suffix);
	}

	@Bean
	public HelloCommand sayHello() {
		return new HelloCommand();
	}

}