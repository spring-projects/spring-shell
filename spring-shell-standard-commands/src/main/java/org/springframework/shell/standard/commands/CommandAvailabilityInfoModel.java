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
package org.springframework.shell.standard.commands;

/**
 * Model encapsulating info about {@code command availability}.
 *
 * @author Janne Valkealahti
 */
class CommandAvailabilityInfoModel {

	private boolean available;
	private String reason;

	CommandAvailabilityInfoModel(boolean available, String reason) {
		this.available = available;
		this.reason = reason;
	}

	/**
	 * Builds {@link CommandAvailabilityInfoModel}.
	 *
	 * @param available the available flag
	 * @param reason the reason
	 * @return a command parameter availability model
	 */
	static CommandAvailabilityInfoModel of(boolean available, String reason) {
		return new CommandAvailabilityInfoModel(available, reason);
	}

	public boolean getAvailable() {
		return available;
	}

	public String getReason() {
		return reason;
	}
}
