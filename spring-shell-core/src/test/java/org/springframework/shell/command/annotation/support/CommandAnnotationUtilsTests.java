/*
 * Copyright 2023-2024 the original author or authors.
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
package org.springframework.shell.command.annotation.support;

import org.junit.jupiter.api.Test;

import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.context.InteractionMode;

import static org.assertj.core.api.Assertions.assertThat;

class CommandAnnotationUtilsTests {

	private static MergedAnnotation<Command> hiddenTrue = MergedAnnotations.from(HiddenTrue.class).get(Command.class);
	private static MergedAnnotation<Command> hiddenFalse = MergedAnnotations.from(HiddenFalse.class).get(Command.class);
	private static MergedAnnotation<Command> hiddenDefault = MergedAnnotations.from(HiddenDefault.class)
			.get(Command.class);

	@Command(hidden = true)
	private static class HiddenTrue {
	}

	@Command(hidden = false)
	private static class HiddenFalse {
	}

	@Command()
	private static class HiddenDefault {
	}

	@Test
	void testHidden() {
		assertThat(CommandAnnotationUtils.deduceHidden(hiddenTrue, hiddenDefault)).isTrue();
		assertThat(CommandAnnotationUtils.deduceHidden(hiddenTrue, hiddenFalse)).isTrue();
		assertThat(CommandAnnotationUtils.deduceHidden(hiddenTrue, hiddenTrue)).isTrue();

		assertThat(CommandAnnotationUtils.deduceHidden(hiddenFalse, hiddenDefault)).isFalse();
		assertThat(CommandAnnotationUtils.deduceHidden(hiddenFalse, hiddenFalse)).isFalse();
		assertThat(CommandAnnotationUtils.deduceHidden(hiddenFalse, hiddenTrue)).isTrue();
	}

	private static MergedAnnotation<Command> commandDefault = MergedAnnotations.from(CommandDefault.class)
			.get(Command.class);
	private static MergedAnnotation<Command> commandValues1 = MergedAnnotations.from(CommandValues1.class)
			.get(Command.class);
	private static MergedAnnotation<Command> commandValues2 = MergedAnnotations.from(CommandValues2.class)
			.get(Command.class);
	private static MergedAnnotation<Command> commandValues3 = MergedAnnotations.from(CommandValues3.class)
			.get(Command.class);
	private static MergedAnnotation<Command> commandValues4 = MergedAnnotations.from(CommandValues4.class)
			.get(Command.class);

	@Command
	private static class CommandDefault {
	}

	@Command(command = { "one", "two" })
	private static class CommandValues1 {
	}

	@Command(command = { "three", "four" })
	private static class CommandValues2 {
	}

	@Command(command = { " five", "six ", " seven " })
	private static class CommandValues3 {
	}

	@Command(command = { " eight nine " })
	private static class CommandValues4 {
	}

	@Test
	void testCommand() {
		assertThat(CommandAnnotationUtils.deduceCommand(commandDefault, commandDefault)).isEmpty();
		assertThat(CommandAnnotationUtils.deduceCommand(commandDefault, commandValues1))
				.isEqualTo(new String[] { "one", "two" });
		assertThat(CommandAnnotationUtils.deduceCommand(commandValues1, commandValues2))
				.isEqualTo(new String[] { "one", "two", "three", "four" });
		assertThat(CommandAnnotationUtils.deduceCommand(commandDefault, commandValues3))
				.isEqualTo(new String[] { "five", "six", "seven" });
		assertThat(CommandAnnotationUtils.deduceCommand(commandDefault, commandValues4))
				.isEqualTo(new String[] { "eight", "nine" });
	}

	private static MergedAnnotation<Command> aliasDefault = MergedAnnotations.from(AliasDefault.class)
			.get(Command.class);
	private static MergedAnnotation<Command> aliasValues1 = MergedAnnotations.from(AliasValues1.class)
			.get(Command.class);
	private static MergedAnnotation<Command> aliasValues2 = MergedAnnotations.from(AliasValues2.class)
			.get(Command.class);
	private static MergedAnnotation<Command> aliasValues3 = MergedAnnotations.from(AliasValues3.class)
			.get(Command.class);
	private static MergedAnnotation<Command> aliasValues4 = MergedAnnotations.from(AliasValues4.class)
			.get(Command.class);
	private static MergedAnnotation<Command> aliasValues5 = MergedAnnotations.from(AliasValues5.class)
			.get(Command.class);
	private static MergedAnnotation<Command> aliasValues6 = MergedAnnotations.from(AliasValues6.class)
			.get(Command.class);

	@Command
	private static class AliasDefault {
	}

	@Command(alias = { "one", "two" })
	private static class AliasValues1 {
	}

	@Command(alias = { "three", "four" })
	private static class AliasValues2 {
	}

	@Command(alias = { " five", "six ", " seven " })
	private static class AliasValues3 {
	}

	@Command(alias = { " eight nine " })
	private static class AliasValues4 {
	}

	@Command(alias = { "one" })
	private static class AliasValues5 {
	}

	@Command(alias = { "" })
	private static class AliasValues6 {
	}

	@Test
	void testAlias() {
		assertThat(CommandAnnotationUtils.deduceAlias(aliasDefault, aliasDefault)).isEmpty();
		assertThat(CommandAnnotationUtils.deduceAlias(aliasDefault, aliasValues1))
				.isEqualTo(new String[][] { { "one" }, { "two" } });
		assertThat(CommandAnnotationUtils.deduceAlias(aliasValues1, aliasValues2))
				.isEqualTo(new String[][] { { "one", "two", "three" }, { "one", "two", "four" } });
		assertThat(CommandAnnotationUtils.deduceAlias(aliasDefault, aliasValues3))
				.isEqualTo(new String[][] { { "five" }, { "six" }, { "seven" } });
		assertThat(CommandAnnotationUtils.deduceAlias(aliasDefault, aliasValues4))
				.isEqualTo(new String[][] { { "eight nine" } });
		assertThat(CommandAnnotationUtils.deduceAlias(aliasValues5, aliasValues6))
				.isEqualTo(new String[][] { { "one" } });
	}

	private static MergedAnnotation<Command> groupValue1 = MergedAnnotations.from(GroupValues1.class)
			.get(Command.class);
	private static MergedAnnotation<Command> groupValue2 = MergedAnnotations.from(GroupValues2.class)
			.get(Command.class);
	private static MergedAnnotation<Command> groupDefault = MergedAnnotations.from(GroupDefault.class)
			.get(Command.class);

	@Command(group = "group1")
	private static class GroupValues1 {
	}

	@Command(group = "group2")
	private static class GroupValues2 {
	}

	@Command()
	private static class GroupDefault {
	}

	@Test
	void testGroup() {
		assertThat(CommandAnnotationUtils.deduceGroup(groupDefault, groupDefault)).isEqualTo("");
		assertThat(CommandAnnotationUtils.deduceGroup(groupDefault, groupValue1)).isEqualTo("group1");
		assertThat(CommandAnnotationUtils.deduceGroup(groupValue1, groupDefault)).isEqualTo("group1");
		assertThat(CommandAnnotationUtils.deduceGroup(groupValue1, groupValue2)).isEqualTo("group2");
	}

	private static MergedAnnotation<Command> descriptionValue1 = MergedAnnotations.from(DescriptionValues1.class)
			.get(Command.class);
	private static MergedAnnotation<Command> descriptionValue2 = MergedAnnotations.from(DescriptionValues2.class)
			.get(Command.class);
	private static MergedAnnotation<Command> descriptionDefault = MergedAnnotations.from(DescriptionDefault.class)
			.get(Command.class);

	@Command(description = "description1")
	private static class DescriptionValues1 {
	}

	@Command(description = "description2")
	private static class DescriptionValues2 {
	}

	@Command()
	private static class DescriptionDefault {
	}

	@Test
	void testDescription() {
		assertThat(CommandAnnotationUtils.deduceDescription(descriptionDefault, descriptionDefault)).isEqualTo("");
		assertThat(CommandAnnotationUtils.deduceDescription(descriptionDefault, descriptionValue1))
				.isEqualTo("description1");
		assertThat(CommandAnnotationUtils.deduceDescription(descriptionValue1, descriptionDefault))
				.isEqualTo("description1");
		assertThat(CommandAnnotationUtils.deduceDescription(descriptionValue1, descriptionValue2))
				.isEqualTo("description2");
	}

	private static MergedAnnotation<Command> interactionModeDefault = MergedAnnotations
			.from(InteractionModeDefault.class).get(Command.class);
	private static MergedAnnotation<Command> interactionModeAll = MergedAnnotations.from(InteractionModeAll.class)
			.get(Command.class);
	private static MergedAnnotation<Command> interactionModeInteractive = MergedAnnotations
			.from(InteractionModeInteractive.class).get(Command.class);
	private static MergedAnnotation<Command> interactionModeNoninteractive = MergedAnnotations
			.from(InteractionModeNoninteractive.class).get(Command.class);

	@Command()
	private static class InteractionModeDefault {
	}

	@Command(interactionMode = InteractionMode.ALL)
	private static class InteractionModeAll {
	}

	@Command(interactionMode = InteractionMode.INTERACTIVE)
	private static class InteractionModeInteractive {
	}

	@Command(interactionMode = InteractionMode.NONINTERACTIVE)
	private static class InteractionModeNoninteractive {
	}

	@Test
	void testInteractionMode() {
		assertThat(CommandAnnotationUtils.deduceInteractionMode(interactionModeDefault, interactionModeDefault))
				.isNull();
		assertThat(CommandAnnotationUtils.deduceInteractionMode(interactionModeAll, interactionModeDefault))
				.isEqualTo(InteractionMode.ALL);
		assertThat(CommandAnnotationUtils.deduceInteractionMode(interactionModeInteractive, interactionModeDefault))
				.isEqualTo(InteractionMode.INTERACTIVE);
		assertThat(CommandAnnotationUtils.deduceInteractionMode(interactionModeNoninteractive, interactionModeDefault))
				.isEqualTo(InteractionMode.NONINTERACTIVE);

		assertThat(CommandAnnotationUtils.deduceInteractionMode(interactionModeDefault, interactionModeAll))
				.isEqualTo(InteractionMode.ALL);
		assertThat(CommandAnnotationUtils.deduceInteractionMode(interactionModeDefault, interactionModeInteractive))
				.isEqualTo(InteractionMode.INTERACTIVE);
		assertThat(CommandAnnotationUtils.deduceInteractionMode(interactionModeDefault, interactionModeNoninteractive))
				.isEqualTo(InteractionMode.NONINTERACTIVE);

		assertThat(CommandAnnotationUtils.deduceInteractionMode(interactionModeAll, interactionModeInteractive))
				.isEqualTo(InteractionMode.INTERACTIVE);
		assertThat(CommandAnnotationUtils.deduceInteractionMode(interactionModeAll, interactionModeNoninteractive))
				.isEqualTo(InteractionMode.NONINTERACTIVE);

		assertThat(
				CommandAnnotationUtils.deduceInteractionMode(interactionModeInteractive, interactionModeNoninteractive))
				.isEqualTo(InteractionMode.NONINTERACTIVE);
		assertThat(
				CommandAnnotationUtils.deduceInteractionMode(interactionModeNoninteractive, interactionModeInteractive))
				.isEqualTo(InteractionMode.INTERACTIVE);
	}
}
