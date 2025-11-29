package org.springframework.shell.core.command;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

import org.springframework.shell.core.commands.AbstractCommand;
import org.springframework.shell.core.commands.adapter.ConsumerCommandAdapter;
import org.springframework.util.Assert;
import static org.springframework.shell.core.command.Command.*;

/**
 * Default implementation of {@link Builder}.
 *
 * @author Piotr Olaszewski
 */
class DefaultCommandBuilder implements Builder {

	private @Nullable String name;

	private @Nullable String description;

	private String group = "";

	private String help = "";

	private @Nullable List<String> aliases;

	private @Nullable Consumer<CommandContext> commandContextConsumer;

	@Override
	public Builder name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public Builder description(String description) {
		this.description = description;
		return this;
	}

	@Override
	public Builder help(String help) {
		this.help = help;
		return this;
	}

	@Override
	public Builder group(String group) {
		this.group = group;
		return this;
	}

	@Override
	public Builder aliases(String... aliases) {
		this.aliases = Arrays.asList(aliases);
		return this;
	}

	@Override
	public Builder aliases(List<String> aliases) {
		this.aliases = aliases;
		return this;
	}

	@Override
	public Builder execute(Consumer<CommandContext> commandExecutor) {
		this.commandContextConsumer = commandExecutor;
		return this;
	}

	@Override
	public AbstractCommand build() {
		ConsumerCommandAdapter abstractCommand = initCommand();

		if (aliases != null) {
			abstractCommand.setAliases(aliases);
		}

		return abstractCommand;
	}

	private ConsumerCommandAdapter initCommand() {
		Assert.hasText(name, "'name' must be specified");
		Assert.hasText(description, "description");
		Assert.notNull(commandContextConsumer, "'commandExecutor' must not be null");

		return new ConsumerCommandAdapter(name, description, group, help, commandContextConsumer);
	}

}
