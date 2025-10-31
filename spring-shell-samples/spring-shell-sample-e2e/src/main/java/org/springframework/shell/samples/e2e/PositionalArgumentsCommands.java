/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.samples.e2e;

import org.springframework.shell.core.command.CommandRegistration.OptionArity;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

public class PositionalArgumentsCommands {

	@Command(command = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	public static class Annotation extends BaseE2ECommands {

		@Command(command = "positional-args-1")
		public String testPositionalArgs1(
				@Option(arity = OptionArity.EXACTLY_ONE) String arg1,
				@Option(arity = OptionArity.EXACTLY_ONE) String arg2,
				@Option(arity = OptionArity.EXACTLY_ONE) String arg3
		) {
				return String.format("Hi arg1='%s' arg2='%s' arg3='%s'", arg1, arg2, arg3);
		}

		@Command(command = "positional-args-2")
		public String testPositionalArgs2(
				@Option(arity = OptionArity.EXACTLY_ONE, defaultValue = "defaultArg1") String arg1,
				@Option(arity = OptionArity.EXACTLY_ONE, defaultValue = "defaultArg2") String arg2,
				@Option(arity = OptionArity.EXACTLY_ONE) String arg3
		) {
				return String.format("Hi arg1='%s' arg2='%s' arg3='%s'", arg1, arg2, arg3);
		}
	}

}
