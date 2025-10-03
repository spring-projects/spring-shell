/*
 * Copyright 2017-2022 the original author or authors.
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
package org.springframework.shell;

/**
 * Indicates that a command exists but is currently not invokable.
 *
 * @author Eric Bottard
 */
public class CommandNotCurrentlyAvailable extends RuntimeException {

	private final String command;

	private final Availability availability;

	public CommandNotCurrentlyAvailable(String command, Availability availability) {
		super(String.format("Command '%s' exists but is not currently available because %s", command,
				availability.getReason()));
		this.command = command;
		this.availability = availability;
	}

	public String getCommand() {
		return command;
	}

	public Availability getAvailability() {
		return availability;
	}

}
