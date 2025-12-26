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
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.AbstractCommand;
import org.springframework.shell.samples.petclinic.domain.Vet;

/**
 * Spring Shell command to show the details of a Pet clinic veterinarian.
 *
 * @author Mahmoud Ben Hassine
 */
public class VetDetailsCommand extends AbstractCommand {

	private final JdbcClient jdbcClient;

	public VetDetailsCommand(JdbcClient jdbcClient) {
		super("vets info", "Show details of a given veterinarian", "Vets",
				"show the details of a given veterinarian. Usage: vets info --vetId=<id>", false);
		this.jdbcClient = jdbcClient;
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) {
		PrintWriter writer = commandContext.outputWriter();
		if (commandContext.parsedInput().options().isEmpty()) {
			writer.println("Veterinarian ID is required");
			writer.println("Usage: vets info --vetId=<id>");
			writer.flush();
			return ExitStatus.USAGE_ERROR;
		}
		CommandOption commandOption = commandContext.parsedInput().options().get(0);
		String longName = commandOption.longName();
		if (!"vetId".equalsIgnoreCase(longName)) {
			writer.println("Unrecognized option: " + longName);
			writer.println("Usage: vets info --vetId=<id>");
			writer.flush();
			return ExitStatus.USAGE_ERROR;
		}
		String vetId = commandOption.value();
		try {
			Integer.parseInt(vetId);
		}
		catch (NumberFormatException e) {
			writer.println("Invalid veterinarian ID: " + vetId + ". It must be a number.");
			writer.println("Usage: vets info --vetId=<id>");
			writer.flush();
			return ExitStatus.USAGE_ERROR;
		}
		try {
			Vet vet = this.jdbcClient.sql("SELECT * FROM VETS where id = " + vetId)
				.query(new DataClassRowMapper<>(Vet.class))
				.single();
			writer.println(vet);
		}
		catch (EmptyResultDataAccessException exception) {
			writer.println("No veterinarian found with ID: " + vetId);
		}
		finally {
			writer.flush();
		}
		return ExitStatus.OK;
	}

	@Override
	public List<CommandOption> getOptions() {
		CommandOption vetIdOption = CommandOption.with()
			.longName("vetId")
			.description("The veterinarian ID")
			.required(true)
			.build();
		return List.of(vetIdOption);
	}

}
