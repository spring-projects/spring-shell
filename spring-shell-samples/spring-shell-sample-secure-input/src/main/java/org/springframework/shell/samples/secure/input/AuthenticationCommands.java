/*
 * Copyright 2026-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.samples.secure.input;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.InputReader;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;

@CommandGroup(name = "Authentication Commands", description = "Commands related to user authentication",
		prefix = "auth")
public class AuthenticationCommands {

	private String username;

	private boolean authenticated = false;

	// In-memory user store for demonstration purposes
	private Map<String, String> userStore = new HashMap<>() {
		{
			put("foo", "bar");
		}
	};

	@Command(name = "login", description = "Log in to the system")
	public void login(CommandContext commandContext) {
		InputReader inputReader = commandContext.inputReader();
		PrintWriter outputWriter = commandContext.outputWriter();
		if (this.authenticated) {
			outputWriter.println("Already logged in.");
			return;
		}
		try {
			String username = inputReader.readInput("Username: ");
			String password = new String(inputReader.readPassword("Password: "));
			if (userStore.containsKey(username) && userStore.get(username).equals(password)) {
				outputWriter.println("Login successful.");
				this.authenticated = true;
				this.username = username;
			}
			else {
				outputWriter.println("Invalid credentials.");
			}
		}
		catch (Exception e) {
			outputWriter.println("Failed to login.");
		}
	}

	@Command(name = "logout", description = "Log out of the system")
	public void logout() {
		if (!this.authenticated) {
			System.out.println("Not logged in.");
			return;
		}
		System.out.println("Logging out...");
		this.authenticated = false;
		this.username = null;
	}

	@Command(name = "status", description = "Check authentication status")
	public String status() {
		return authenticated ? "Logged in as " + this.username : "Not logged in.";
	}

	@Command(name = "change-password", description = "Change password", availabilityProvider = "authenticationProvider")
	public void changePassword(CommandContext commandContext) {
		InputReader inputReader = commandContext.inputReader();
		PrintWriter outputWriter = commandContext.outputWriter();
		try {
			char[] currentPassword = inputReader.readPassword("Enter current password: ");
			if (!userStore.containsKey(this.username)
					|| !userStore.get(this.username).equals(new String(currentPassword))) {
				outputWriter.println("Current password is incorrect.");
				return;
			}
			char[] chars = inputReader.readPassword("Enter new password: ");
			String newPassword = new String(chars);
			userStore.put(this.username, newPassword);
			outputWriter.println("Password successfully updated.");
		}
		catch (Exception e) {
			outputWriter.println("Failed to set password.");
		}
	}

	@Bean
	public AvailabilityProvider authenticationProvider() {
		return () -> this.authenticated ? Availability.available()
				: Availability.unavailable("You must be logged in to use this command.");
	}

}
