/*
 * Copyright 2024-present the original author or authors.
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

import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.AbstractCommand;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;
import org.springframework.stereotype.Component;

class CommandAvailabilitySnippets {

	class Dump1 {

		// tag::availability-method-annotation[]
		@Component
		class MyCommands {

			private boolean connected;

			@Command(name = "connect")
			public void connect(String user, String password) {
				connected = true;
			}

			@Command(name = "download", availabilityProvider = "downloadAvailability")
			public void download() {
				// do something
			}

			@Bean
			public AvailabilityProvider downloadAvailability() {
				return () -> connected ? Availability.available() : Availability.unavailable("you are not connected");
			}

		}
		// end::availability-method-annotation[]

	}

	class Dump2 {

		// tag::availability-method-programmatic[]
		private boolean connected;

		@Bean
		public AbstractCommand connect() {
			return org.springframework.shell.core.command.Command.builder().name("connect").execute(ctx -> {
				CommandOption connectedOption = ctx.getOptionByName("connected");
				this.connected = Boolean.parseBoolean(connectedOption.value());
			});
		}

		@Bean
		public AbstractCommand download() {
			return org.springframework.shell.core.command.Command.builder()
				.name("download")
				.availabilityProvider(
						() -> connected ? Availability.available() : Availability.unavailable("you are not connected"))
				.execute(ctx -> {
					// do something
				});
		}
		// end::availability-method-programmatic[]

	}

}
