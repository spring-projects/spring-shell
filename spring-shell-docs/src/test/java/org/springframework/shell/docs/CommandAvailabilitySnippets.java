/*
 * Copyright 2024 the original author or authors.
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

import java.util.Calendar;

import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;
import org.springframework.shell.core.command.CommandRegistration;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandAvailability;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.SUNDAY;

class CommandAvailabilitySnippets {

	class Dump1 {
		// tag::availability-method-in-shellcomponent[]
		@Command
		public class MyCommands {

			private boolean connected;

			@Command(description = "Connect to the server.")
			public void connect(String user, String password) {
				// do something
				connected = true;
			}

			@Command(description = "Download the nuclear codes.")
			public void download() {
				// do something
			}

			public Availability downloadAvailability() {
				return connected
					? Availability.available()
					: Availability.unavailable("you are not connected");
			}
		}
		// end::availability-method-in-shellcomponent[]
	}

	class Dump2 {
		boolean connected;

		// tag::availability-method-name-in-shellcomponent[]
		@Command(description = "Download the nuclear codes.")
		//@CommandAvailability("availabilityCheck") // <1>
		public void download() {
		}

		public Availability availabilityCheck() { // <1>
			return connected
				? Availability.available()
				: Availability.unavailable("you are not connected");
		}
		// end::availability-method-name-in-shellcomponent[]
	}

	class Dump3 {
		boolean connected;
		// tag::availability-method-name-multi-in-shellcomponent[]
		@Command(description = "Download the nuclear codes.")
		public void download() {
		}

		@Command(description = "Disconnect from the server.")
		public void disconnect() {
		}

		//@CommandAvailability({"download", "disconnect"})
		public Availability availabilityCheck() {
			return connected
				? Availability.available()
				: Availability.unavailable("you are not connected");
		}

		// end::availability-method-name-multi-in-shellcomponent[]
	}

	// tag::availability-method-default-value-in-shellcomponent[]
	@Command
	public class Toggles {

		//@CommandAvailability
		public Availability availabilityOnWeekdays() {
			return Calendar.getInstance().get(DAY_OF_WEEK) == SUNDAY
				? Availability.available()
				: Availability.unavailable("today is not Sunday");
		}

		@Command
		public void foo() {}

		@Command
		public void bar() {}
	}
	// end::availability-method-default-value-in-shellcomponent[]

	class Dump4 {
	// tag::availability-method-annotation[]
	@Command
	class MyCommands {

		private boolean connected;

		@Command(command = "connect")
		public void connect(String user, String password) {
			connected = true;
		}


		@Command(command = "download")
		@CommandAvailability(provider = "downloadAvailability")
		public void download(
		) {
			// do something
		}

		@Bean
		public AvailabilityProvider downloadAvailability() {
			return () -> connected
				? Availability.available()
				: Availability.unavailable("you are not connected");
		}
	}

	// end::availability-method-annotation[]
	}


	class Dump5 {

		// tag::availability-method-programmatic[]
		private boolean connected;

		@Bean
		public CommandRegistration connect(
				CommandRegistration.BuilderSupplier builder) {
			return builder.get()
				.command("connect")
				.withOption()
					.longNames("connected")
					.required()
					.type(boolean.class)
					.and()
				.withTarget()
					.consumer(ctx -> {
						boolean connected = ctx.getOptionValue("connected");
						this.connected = connected;
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration download(
				CommandRegistration.BuilderSupplier builder) {
			return builder.get()
				.command("download")
				.availability(() -> {
					return connected
						? Availability.available()
						: Availability.unavailable("you are not connected");
				})
				.withTarget()
					.consumer(ctx -> {
						// do something
					})
					.and()
				.build();
		}
		// end::availability-method-programmatic[]

	}

}
