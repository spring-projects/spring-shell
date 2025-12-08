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

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandOption;
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
		super("owners info", "Show details of a given owner", "Owners", "show the details of a given owner");
		this.jdbcClient = jdbcClient;
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) {
		PrintWriter writer = commandContext.outputWriter();
		if (commandContext.parsedInput().options().isEmpty()) {
			writer.println("Owner ID is required");
			writer.println("Usage: owners info --ownerId=<id>");
			writer.flush();
			return ExitStatus.USAGE_ERROR;
		}
		CommandOption commandOption = commandContext.parsedInput().options().get(0);
		String longName = commandOption.longName();
		if (!"ownerId".equalsIgnoreCase(longName)) {
			writer.println("Unrecognized option: " + longName);
			writer.println("Usage: owners info --ownerId=<id>");
			writer.flush();
			return ExitStatus.USAGE_ERROR;
		}
		String ownerId = commandOption.value();
		try {
			Integer.parseInt(ownerId);
		}
		catch (NumberFormatException e) {
			writer.println("Invalid owner ID: " + ownerId + ". It must be a number.");
			writer.println("Usage: owners info --ownerId=<id>");
			writer.flush();
			return ExitStatus.USAGE_ERROR;
		}
		try {
			Owner owner = this.jdbcClient.sql("SELECT * FROM OWNERS where id = " + ownerId)
				.query(new DataClassRowMapper<>(Owner.class))
				.single();
			writer.println(owner);
		}
		catch (EmptyResultDataAccessException exception) {
			writer.println("No owner found with ID: " + ownerId);
		}
		finally {
			writer.flush();
		}
		return ExitStatus.OK;
	}

}
