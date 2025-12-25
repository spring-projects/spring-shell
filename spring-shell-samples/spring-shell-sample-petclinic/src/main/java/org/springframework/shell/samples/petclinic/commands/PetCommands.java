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

import org.jspecify.annotations.Nullable;

import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.core.command.completion.CompletionProposal;
import org.springframework.shell.core.command.completion.CompletionProvider;
import org.springframework.shell.samples.petclinic.domain.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetCommands {

	private final JdbcClient jdbcClient;

	public PetCommands(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	@Command(name = { "pets", "list" }, description = "List pets", group = "Pets", help = "List pets in Pet Clinic")
	public void listPets(CommandContext commandContext) {
		List<@Nullable Pet> pets = jdbcClient.sql("SELECT id, name FROM PETS")
			.query(new DataClassRowMapper<>(Pet.class))
			.list();
		PrintWriter writer = commandContext.outputWriter();
		for (Pet pet : pets) {
			writer.println(pet);
		}
		writer.flush();
	}

	@Command(name = { "pets", "info" }, description = "Show detail about a given pet", group = "Pets",
			help = "Show the details about a given pet", completionProvider = "petsInfoCompletionProvider")
	public void showPet(@Option(longName = "petId", description = "The pet ID", required = true) int id,
			CommandContext commandContext) {
		try {
			Pet pet = this.jdbcClient.sql("SELECT * FROM PETS where id = " + id)
				.query(new DataClassRowMapper<>(Pet.class))
				.single();
			commandContext.outputWriter().println(pet);
		}
		catch (EmptyResultDataAccessException exception) {
			commandContext.outputWriter().println("No pet found with ID: " + id);
		}
		finally {
			commandContext.outputWriter().flush();
		}
	}

	@Bean
	public CompletionProvider petsInfoCompletionProvider() {
		return completionContext -> List.of(new CompletionProposal("--petId="));
	}

}