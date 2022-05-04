package org.springframework.shell.docs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandCatalogCustomizer;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandResolver;

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
		List<CommandRegistration> registrations = new ArrayList<>();

		CustomCommandResolver() {
			CommandRegistration resolved = CommandRegistration.builder()
				.command("resolve command")
				.build();
			registrations.add(resolved);
		}

		@Override
		public List<CommandRegistration> resolve() {
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
