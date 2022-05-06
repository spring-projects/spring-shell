package org.springframework.shell.docs;

import org.springframework.shell.command.CommandRegistration;

public class CommandRegistrationSnippets {

	void dump1() {
		// tag::snippet1[]
		CommandRegistration.builder()
			.withOption()
				.longNames("myopt")
				.and()
			.build();
		// end::snippet1[]
	}

	void dump2() {
		// tag::snippet2[]
		CommandRegistration.builder()
			.withOption()
				.shortNames('s')
				.and()
			.build();
		// end::snippet2[]
	}
}
