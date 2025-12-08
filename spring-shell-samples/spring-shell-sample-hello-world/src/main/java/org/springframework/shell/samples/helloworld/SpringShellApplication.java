package org.springframework.shell.samples.helloworld;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.EnableCommand;
import org.springframework.shell.core.command.annotation.Option;

@EnableCommand(SpringShellApplication.class)
public class SpringShellApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringShellApplication.class);
		ShellRunner runner = context.getBean(ShellRunner.class);
		runner.run(args);
	}

	@Command(name = "hello", description = "Say hello to a given name", group = "Greetings",
			help = "A command that greets the user with 'Hello ${name}!'. Usage: hello [-n | --name]=<name>")
	public void sayHello(@Option(shortName = 'n', longName = "name", description = "the name of the person to greet",
			defaultValue = "World") String name) {
		System.out.println("Hello " + name + "!");
	}

}