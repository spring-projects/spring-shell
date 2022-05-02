package org.springframework.shell.docs;

import org.springframework.shell.command.CommandRegistration;

public class CommandTargetSnippets {

	// tag::snippet11[]
	public static class CommandPojo {

		String command(String arg) {
			return arg;
		}
	}
	// end::snippet11[]

	void dump1() {
		// tag::snippet12[]
		CommandPojo pojo = new CommandPojo();
		CommandRegistration.builder()
			.command("command")
			.withTarget()
				.method(pojo, "command")
				.and()
			.withOption()
				.longNames("arg")
				.and()
			.build();
		// end::snippet12[]
	}

	void dump2() {
		// tag::snippet2[]
		CommandRegistration.builder()
			.command("command")
			.withTarget()
				.function(ctx -> {
					String arg = ctx.getOptionValue("arg");
					return String.format("hi, arg value is '%s'", arg);
				})
				.and()
			.withOption()
				.longNames("arg")
				.and()
			.build();
		// end::snippet2[]
	}

	void dump3() {
		// tag::snippet3[]
		CommandRegistration.builder()
			.command("command")
			.withTarget()
				.consumer(ctx -> {
					String arg = ctx.getOptionValue("arg");
					ctx.getTerminal().writer()
						.println(String.format("hi, arg value is '%s'", arg));
				})
			.and()
			.withOption()
				.longNames("arg")
				.and()
			.build();
		// end::snippet3[]
	}
}
