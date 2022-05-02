package org.springframework.shell.docs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandCatalog.CommandCatalogCustomizer;
import org.springframework.shell.command.CommandCatalog.CommandResolver;
import org.springframework.shell.command.CommandRegistration;

public class CommandCatalogSnippets {

	CommandCatalog catalog = CommandCatalog.of();

	void dump1() {
		// tag::snippet1[]
		CommandRegistration registration = CommandRegistration.builder().build();
		catalog.register(registration);
		// end::snippet1[]
	}

	// tag::snippet2[]
	static class CustomCommandResolver implements CommandResolver {
		Map<String, CommandRegistration> registrations = new HashMap<>();

		CustomCommandResolver() {
			CommandRegistration resolved = CommandRegistration.builder()
				.command("resolve command")
				.build();
			registrations.put("resolve command", resolved);
		}

		@Override
		public Map<String, CommandRegistration> resolve() {
			return registrations;
		}
	}
	// end::snippet2[]

	// tag::snippet3[]
	static class CustomCommandCatalogCustomizer implements CommandCatalogCustomizer {

		@Override
		public void customize(CommandCatalog commandCatalog) {
			CommandRegistration registration = CommandRegistration.builder()
				.command("resolve command")
				.build();
			commandCatalog.register(registration);
		}
	}
	// end::snippet3[]
}
