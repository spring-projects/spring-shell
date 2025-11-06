/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.docs;

import org.springframework.shell.core.command.Command;

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
		Command.builder()
			.command("command")
			.withTarget(targetSpec -> targetSpec.method(pojo, "command"))
			.withOption(optionSpec -> optionSpec.longNames("arg"))
			.build();
		// end::snippet12[]
	}

	void dump2() {
		// tag::snippet2[]
		Command.builder().command("command").withTarget(targetSpec -> targetSpec.function(ctx -> {
			String arg = ctx.getOptionValue("arg");
			return String.format("hi, arg value is '%s'", arg);
		})).withOption(optionSpec -> optionSpec.longNames("arg")).build();
		// end::snippet2[]
	}

	void dump3() {
		// tag::snippet3[]
		Command.builder().command("command").withTarget(targetSpec -> targetSpec.consumer(ctx -> {
			String arg = ctx.getOptionValue("arg");
			ctx.getTerminal().writer().println(String.format("hi, arg value is '%s'", arg));
		})).withOption(optionSpec -> optionSpec.longNames("arg")).build();
		// end::snippet3[]
	}

}
