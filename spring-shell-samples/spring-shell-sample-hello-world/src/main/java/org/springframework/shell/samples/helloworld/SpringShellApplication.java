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

@EnableCommand({ SpringShellApplication.class, SpringShellApplication.Nested.class,
		SpringShellApplication.Nested.Child.class })
public class SpringShellApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringShellApplication.class,
				SpringShellApplication.Nested.class, SpringShellApplication.Nested.Child.class);
		ShellRunner runner = context.getBean(ShellRunner.class);
		runner.run(args);
	}

	@Command(name = "hi", description = "Say hi", group = "greetings")
	public void sayHi(CommandContext commandContext) {
		Terminal terminal = commandContext.terminal();
		terminal.writer().println("Hi there!");
	}

	@Command(description = "Say hi using method name", group = "greetings")
	public void nameFromMethod(CommandContext commandContext) {
		Terminal terminal = commandContext.terminal();
		terminal.writer().println("Hi from method name!");
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

	// root first
	// root child
	// root child first
	// root child second
	@Command(name = "root")
	public static class Nested {

		@Command(name = "first", description = "Say hi from nested method", group = "greetings")
		public void nestedMethod(CommandContext commandContext) {
			Terminal terminal = commandContext.terminal();
			terminal.writer().println("Hi form nested!");
		}

		@Command(name = "child", description = "Say hi from nested method", group = "greetings")
		public void child(CommandContext commandContext) {
			Terminal terminal = commandContext.terminal();
			terminal.writer().println("Hi form child!");
		}

		@Command(name = "child")
		public static class Child {

			@Command(name = "first", group = "greetings")
			public void firstChild(CommandContext commandContext) {
				Terminal terminal = commandContext.terminal();
				terminal.writer().println("first!");
			}

			@Command(name = "second", group = "greetings")
			public void secondChild(CommandContext commandContext) {
				Terminal terminal = commandContext.terminal();
				terminal.writer().println("second!");
			}

		}

	}

}