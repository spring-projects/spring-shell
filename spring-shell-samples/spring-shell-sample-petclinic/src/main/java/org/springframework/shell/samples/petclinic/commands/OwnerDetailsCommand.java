/*
 * Copyright 2025-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.samples.petclinic.commands;

import java.io.PrintWriter;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.commands.AbstractCommand;
import org.springframework.shell.samples.petclinic.domain.Owner;

/**
 * Spring Shell command to show the details of a Pet clinic owner.
 *
 * @author Mahmoud Ben Hassine
 */
public class OwnerDetailsCommand extends AbstractCommand {

	private final JdbcClient jdbcClient;

	public OwnerDetailsCommand(JdbcClient jdbcClient) {
		super("owner", "Show details of a given owner", "Pet Clinic", "Command to show the details of a given owner");
		this.jdbcClient = jdbcClient;
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) {
		PrintWriter writer = commandContext.terminal().writer();
		if (commandContext.arguments().isEmpty()) {
			writer.println("Owner ID is required");
			writer.println("Usage: owner <ownerId>");
			writer.flush();
			return ExitStatus.USAGE_ERROR;
		}
		String ownerId = commandContext.arguments().get(0).value();
		Owner owner = this.jdbcClient.sql("SELECT * FROM OWNERS where id = " + ownerId)
			.query(new DataClassRowMapper<>(Owner.class))
			.single();
		writer.println(owner);
		writer.flush();
		return ExitStatus.OK;
	}

}
