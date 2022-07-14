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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupsInfoModelTests {

	private CommandCatalog commandCatalog;

	@BeforeEach
	void setup() {
		this.commandCatalog = CommandCatalog.of();
	}

	@Test
	void showGroupsIsSet() {
		GroupsInfoModel gim = buildGIM(true);
		assertThat(gim.getShowGroups()).isTrue();

		gim = buildGIM(false);
		assertThat(gim.getShowGroups()).isFalse();
	}

	@Test
	void simpleCommandsAreSeparated() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("main1")
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		CommandRegistration r2 = CommandRegistration.builder()
			.command("main2")
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		this.commandCatalog.register(r1, r2);
		GroupsInfoModel gim = buildGIM();

		assertThat(gim.getCommands()).hasSize(2);
		assertThat(gim.getGroups()).hasSize(1);
	}

	@Test
	void aliasNotAddedToTopModel() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("main1")
			.withAlias()
				.command("alias1")
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		this.commandCatalog.register(r1);
		GroupsInfoModel gim = buildGIM();

		assertThat(gim.getCommands()).hasSize(1);
	}

	private GroupsInfoModel buildGIM(boolean showGroups) {
		return GroupsInfoModel.of(showGroups, this.commandCatalog.getRegistrations());
	}

	private GroupsInfoModel buildGIM() {
		return buildGIM(true);
	}
}
