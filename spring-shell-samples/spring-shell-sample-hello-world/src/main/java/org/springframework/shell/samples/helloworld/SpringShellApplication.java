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

	@Command
	public String hello(@Option(defaultValue = "World") String name) {
		return "Hello " + name + "!";
	}

}