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
package org.springframework.shell.samples.petclinic;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.shell.core.ShellRunner;
import org.springframework.shell.core.command.annotation.EnableCommand;
import org.springframework.shell.jline.DefaultJLineShellConfiguration;
import org.springframework.shell.samples.petclinic.commands.*;

/**
 * @author Mahmoud Ben Hassine
 */
@EnableCommand({ SpringShellApplication.class, PetCommands.class })
@Import(DefaultJLineShellConfiguration.class)
public class SpringShellApplication {

	public static void main(String[] args) throws Exception {
		Class<?>[] classes = { SpringShellApplication.class, PetCommands.class };
		ApplicationContext context = new AnnotationConfigApplicationContext(classes);
		ShellRunner runner = context.getBean(ShellRunner.class);
		runner.run(args);
	}

	@Bean
	public OwnersListCommand ownersCommand(JdbcClient jdbcClient) {
		return new OwnersListCommand(jdbcClient);
	}

	@Bean
	public OwnerDetailsCommand ownerDetailsCommand(JdbcClient jdbcClient) {
		return new OwnerDetailsCommand(jdbcClient);
	}

	@Bean
	public VetsListCommand vetsCommand(JdbcClient jdbcClient) {
		return new VetsListCommand(jdbcClient);
	}

	@Bean
	public VetDetailsCommand vetDetailsCommand(JdbcClient jdbcClient) {
		return new VetDetailsCommand(jdbcClient);
	}

	@Bean
	public JdbcClient jdbcClient() {
		EmbeddedDatabase database = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
			.addScript("schema.sql")
			.addScript("data.sql")
			.build();
		return JdbcClient.create(database);
	}

}