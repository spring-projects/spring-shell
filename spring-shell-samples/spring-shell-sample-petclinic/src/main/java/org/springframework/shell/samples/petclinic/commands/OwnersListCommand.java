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

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.AbstractCommand;
import org.springframework.shell.samples.petclinic.domain.Owner;

/**
 * Spring Shell command to list Pet clinic owners.
 *
 * @author Mahmoud Ben Hassine
 */
public class OwnersListCommand extends AbstractCommand {

	private final JdbcClient jdbcClient;

	public OwnersListCommand(JdbcClient jdbcClient) {
		super("owners list", "List owners", "Owners", "Command to list owners. Usage: owners list", false);
		this.jdbcClient = jdbcClient;
	}

	@Override
	public ExitStatus doExecute(CommandContext commandContext) {
		List<@Nullable Owner> owners = this.jdbcClient.sql("SELECT id, first_name, last_name FROM OWNERS")
			.query(new DataClassRowMapper<>(Owner.class))
			.list();
		PrintWriter writer = commandContext.outputWriter();
		for (Owner owner : owners) {
			writer.println(owner);
		}
		writer.flush();
		return ExitStatus.OK;
	}

}
