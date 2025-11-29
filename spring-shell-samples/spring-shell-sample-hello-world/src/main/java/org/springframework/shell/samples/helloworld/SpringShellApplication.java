package org.springframework.shell.samples.helloworld;

import java.util.List;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Arguments;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.EnableCommand;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.core.commands.AbstractCommand;
import static org.jline.utils.AttributedStyle.*;

@EnableCommand(SpringShellApplication.class)
public class SpringShellApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringShellApplication.class);
		ShellRunner runner = context.getBean(ShellRunner.class);
		runner.run(args);
	}

	@Command(name = "hi", description = "Say hi to a given name", group = "greetings",
			help = "A command that greets the user with 'Hi ${name}!' with a configurable suffix. Example usage: hi -s=! John")
	public void sayHi(
			@Argument(index = 0, description = "the name of the person to greet", defaultValue = "world") String name,
			@Option(shortName = 's', longName = "suffix", description = "the suffix of the greeting message",
					defaultValue = "!") char suffix) {
		System.out.println("Hi " + name + suffix);
	}

	@Command(name = "hey", description = "Say hey to everyone", group = "greetings",
			help = "A command that greets all given names. Example usage: hey John Doe -s=!")
	public void sayHeyToEveryone(@Arguments List<String> names, @Option(shortName = 's', longName = "suffix",
			description = "the suffix of the greeting message", defaultValue = "!") char suffix) {
		System.out.println("Hey " + String.join(",", names) + suffix);
	}

	@Command(name = "yo", description = "Say yo", group = "greetings",
			help = "A command that greets the user with 'Yo there! what's up?'")
	public void sayYo(CommandContext commandContext) {
		Terminal terminal = commandContext.terminal();
		terminal.writer().println("Yo there! what's up?");
	}

	@Bean
	public AbstractCommand sayGoodMorning() {
		return org.springframework.shell.core.command.Command.builder()
			.name("good-morning")
			.description("Say good morning")
			.aliases("greetings")
			.help("A command that greets the user with 'Good morning!'")
			.execute(commandContext -> {
				String ansiString = new AttributedStringBuilder().append("Good morning ")
					.append("Sir", BOLD.foreground(GREEN))
					.append("!")
					.toAnsi();

				Terminal terminal = commandContext.terminal();
				terminal.writer().println(ansiString);
				terminal.flush();
			})
			.build();
	}

	@Bean
	public HelloCommand sayHello() {
		return new HelloCommand();
	}

}