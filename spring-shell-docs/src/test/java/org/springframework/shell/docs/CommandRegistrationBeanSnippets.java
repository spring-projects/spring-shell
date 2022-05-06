package org.springframework.shell.docs;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;

public class CommandRegistrationBeanSnippets {

	// tag::snippet1[]
	@Bean
	CommandRegistration commandRegistration() {
		return CommandRegistration.builder()
			.command("mycommand")
			.build();
	}
	// end::snippet1[]
}
