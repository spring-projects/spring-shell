package org.springframework.shell.docs;

import javax.validation.constraints.Null;

import org.springframework.shell.command.CommandContext;

@SuppressWarnings("unused")
public class CommandContextSnippets {

	CommandContext ctx = CommandContext.of(null, null, null);

	void dump1() {
		// tag::snippet1[]
		String arg = ctx.getOptionValue("arg");
		// end::snippet1[]
	}

	void dump2() {
		// tag::snippet2[]
		ctx.getTerminal().writer().println("hi");
		// end::snippet2[]
	}

}
