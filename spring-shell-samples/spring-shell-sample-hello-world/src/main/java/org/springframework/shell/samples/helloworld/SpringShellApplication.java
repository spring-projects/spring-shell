package org.springframework.shell.samples.helloworld;

import org.jline.terminal.Terminal;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.EnableCommand;
import org.springframework.shell.core.commands.AbstractCommand;

@EnableCommand(SpringShellApplication.class)
public class SpringShellApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringShellApplication.class);
		ShellRunner runner = context.getBean(ShellRunner.class);
		runner.run(args);
	}

	@Command(name = "hi", description = "Say hi", group = "greetings")
	public void sayHi(CommandContext commandContext) {
		Terminal terminal = commandContext.terminal();
		terminal.writer().println("Hi there!");
	}

	@Bean
	public AbstractCommand sayHello() {
		return new AbstractCommand("hello", "Say hello", "greetings") {
			@Override
			public void execute(CommandContext commandContext) {
				Terminal terminal = commandContext.terminal();
				terminal.writer().println("Hello there!");
				terminal.flush();
			}
		};
	}

}