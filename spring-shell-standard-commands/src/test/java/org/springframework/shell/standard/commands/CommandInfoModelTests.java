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

import org.junit.jupiter.api.Test;

import org.springframework.shell.command.CommandRegistration;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandInfoModelTests {

	@Test
	void hasGivenName() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("main1")
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		CommandInfoModel cim = CommandInfoModel.of("main1", r1);
		assertThat(cim.getName()).isEqualTo("main1");
	}

	@Test
	void hasGivenNames() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("main1")
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		CommandInfoModel cim = CommandInfoModel.of("main1", r1);
		assertThat(cim.getNames()).containsExactly("main1");

		r1 = CommandRegistration.builder()
			.command("main1 sub1")
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();

		cim = CommandInfoModel.of("main1 sub1", r1);
		assertThat(cim.getNames()).containsExactly("main1", "sub1");
	}

	@Test
	void hasGivenDescription() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("main1")
			.description("desc1")
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		CommandInfoModel cim = CommandInfoModel.of("main1", r1);
		assertThat(cim.getDescription()).isEqualTo("desc1");
	}

	@Test
	void hasNoParameters() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("main1")
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		CommandInfoModel cim = CommandInfoModel.of("main1", r1);
		assertThat(cim.getParameters()).isEmpty();
	}

	@Test
	void hasExpectedDefaultAvailability() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("main1")
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		CommandInfoModel cim = CommandInfoModel.of("main1", r1);
		assertThat(cim.getAvailability()).isNotNull();
		assertThat(cim.getAvailability().getAvailable()).isTrue();
		assertThat(cim.getAvailability().getReason()).isNull();
	}

	@Test
	void hasNoAliases() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("main1")
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		CommandInfoModel cim = CommandInfoModel.of("main1", r1);
		assertThat(cim.getAliases()).isEmpty();
	}

	@Test
	void hasAliases() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("main1")
			.withAlias()
				.command("alias1")
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		CommandInfoModel cim = CommandInfoModel.of("main1", r1);
		assertThat(cim.getAliases()).containsExactly("alias1");
	}

	@Test
	void voidTypeUsesEmptyStringAsName() {
		CommandRegistration r1 = CommandRegistration.builder()
			.command("main1")
			.withOption()
				.longNames("arg1")
				.type(void.class)
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		CommandInfoModel cim1 = CommandInfoModel.of("main1", r1);
		assertThat(cim1.getParameters()).hasSize(1);
		assertThat(cim1.getParameters().get(0).getArguments()).containsExactly("--arg1");
		assertThat(cim1.getParameters().get(0).getType()).isEmpty();

		CommandRegistration r2 = CommandRegistration.builder()
			.command("main1")
			.withOption()
				.longNames("arg1")
				.type(Void.class)
				.and()
			.withTarget()
				.consumer(ctx -> {})
				.and()
			.build();
		CommandInfoModel cim2 = CommandInfoModel.of("main1", r2);
		assertThat(cim2.getParameters()).hasSize(1);
		assertThat(cim2.getParameters().get(0).getArguments()).containsExactly("--arg1");
		assertThat(cim2.getParameters().get(0).getType()).isEmpty();
	}
}
