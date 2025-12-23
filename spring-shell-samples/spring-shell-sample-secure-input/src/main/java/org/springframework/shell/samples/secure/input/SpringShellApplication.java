package org.springframework.shell.samples.secure.input;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.annotation.Command;

@SpringBootApplication
public class SpringShellApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringShellApplication.class, args);
	}

	@Command(name = "change-password", description = "Change password", group = "User Management",
			help = "A command that changes the user password by securely reading it from the standard input.")
	public String changePassword(CommandContext commandContext) {
		try {
			char[] chars = commandContext.inputReader().readPassword("Enter new password: ");
			// In a real application, you would update the password securely here
			return "Password successfully updated.";
		}
		catch (Exception e) {
			commandContext.outputWriter().println("Error reading password: " + e.getMessage());
			return "Failed to set password.";
		}
	}

}