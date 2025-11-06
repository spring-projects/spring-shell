package org.springframework.shell.core.command;

import org.jspecify.annotations.Nullable;
import org.springframework.shell.core.command.Command.AliasSpec;
import org.springframework.shell.core.command.Command.ExitCodeSpec;
import org.springframework.shell.core.command.Command.TargetSpec;
import org.springframework.shell.core.command.DefaultCommand.*;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.support.CommandUtils;
import org.springframework.shell.core.context.InteractionMode;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.springframework.shell.core.command.Command.Builder;
import static org.springframework.shell.core.command.Command.OptionSpec;

class DefaultCommandBuilder implements Builder {

	private @Nullable String command;

	private @Nullable String description;

	private @Nullable String group;

	private @Nullable InteractionMode interactionMode;

	private @Nullable Supplier<Availability> availability;

	private boolean hidden;

	private @Nullable List<DefaultOptionSpec> optionSpecs;

	private @Nullable DefaultHelpOptionsSpec helpOptionsSpec;

	private @Nullable Function<String, String> defaultOptionNameModifier;

	private @Nullable List<DefaultAliasSpec> aliasSpecs;

	private @Nullable DefaultTargetSpec targetSpec;

	private @Nullable DefaultExitCodeSpec exitCodeSpec;

	private @Nullable DefaultErrorHandlingSpec errorHandling;

	@Override
	public Builder command(String... commands) {
		this.command = CommandUtils.toCommand(commands);
		return this;
	}

	@Override
	public Builder description(String description) {
		this.description = description;
		return this;
	}

	@Override
	public Builder group(String group) {
		this.group = group;
		return this;
	}

	@Override
	public Builder interactionMode(@Nullable InteractionMode interactionMode) {
		this.interactionMode = interactionMode;
		return this;
	}

	@Override
	public Builder isInteractive() {
		interactionMode = InteractionMode.INTERACTIVE;
		return this;
	}

	@Override
	public Builder isNonInteractive() {
		interactionMode = InteractionMode.NONINTERACTIVE;
		return this;
	}

	@Override
	public Builder availability(Supplier<Availability> availability) {
		this.availability = availability;
		return this;
	}

	@Override
	public Builder hidden() {
		return hidden(true);
	}

	@Override
	public Builder hidden(boolean hidden) {
		this.hidden = hidden;
		return this;
	}

	@Override
	public Builder withOption(Consumer<OptionSpec> optionConfigurer) {
		DefaultOptionSpec optionSpec = new DefaultOptionSpec();
		optionConfigurer.accept(optionSpec);
		initOptionSpecs().add(optionSpec);
		return this;
	}

	@Override
	public Builder withHelpOptions(Consumer<Command.HelpOptionsSpec> helpOptionsConfigurer) {
		DefaultHelpOptionsSpec defaultHelpOptionsSpec = new DefaultHelpOptionsSpec();
		helpOptionsConfigurer.accept(defaultHelpOptionsSpec);
		this.helpOptionsSpec = defaultHelpOptionsSpec;
		return this;
	}

	private List<DefaultOptionSpec> initOptionSpecs() {
		if (optionSpecs == null) {
			optionSpecs = new ArrayList<>();
		}
		return optionSpecs;
	}

	@Override
	public Builder defaultOptionNameModifier(Function<String, String> defaultOptionNameModifier) {
		this.defaultOptionNameModifier = defaultOptionNameModifier;
		return this;
	}

	@Override
	public Builder withAlias(Consumer<AliasSpec> aliasConfigurer) {
		DefaultAliasSpec aliasSpec = new DefaultAliasSpec();
		aliasConfigurer.accept(aliasSpec);
		initAliasSpecs().add(aliasSpec);
		return this;
	}

	private List<DefaultAliasSpec> initAliasSpecs() {
		if (aliasSpecs == null) {
			aliasSpecs = new ArrayList<>();
		}
		return aliasSpecs;
	}

	@Override
	public Builder withTarget(Consumer<TargetSpec> targetConfigurer) {
		DefaultTargetSpec defaultTargetSpec = new DefaultTargetSpec();
		targetConfigurer.accept(defaultTargetSpec);
		this.targetSpec = defaultTargetSpec;
		return this;
	}

	@Override
	public Builder withExitCode(Consumer<ExitCodeSpec> exitCodeConfigurer) {
		DefaultExitCodeSpec defaultExitCodeSpec = new DefaultExitCodeSpec();
		exitCodeConfigurer.accept(defaultExitCodeSpec);
		this.exitCodeSpec = defaultExitCodeSpec;
		return this;
	}

	@Override
	public Builder withErrorHandling(Consumer<Command.ErrorHandlingSpec> errorHandlingConfigurer) {
		DefaultErrorHandlingSpec defaultErrorHandlingSpec = new DefaultErrorHandlingSpec();
		errorHandlingConfigurer.accept(defaultErrorHandlingSpec);
		this.errorHandling = defaultErrorHandlingSpec;
		return this;
	}

	@Override
	public Command build() {
		Assert.hasText(command, "command cannot be empty");
		Assert.notNull(targetSpec, "target cannot be null");

		InteractionMode interactionMode = this.interactionMode == null ? InteractionMode.ALL : this.interactionMode;
		Supplier<Availability> availability = this.availability == null ? Availability::available : this.availability;
		List<DefaultOptionSpec> defaultOptionSpecs = initOptionSpecs();
		List<DefaultAliasSpec> defaultAliasSpecs = initAliasSpecs();

		return new DefaultCommand(command, interactionMode, group, hidden, description, availability,
				defaultOptionSpecs, targetSpec, defaultAliasSpecs, exitCodeSpec, errorHandling, helpOptionsSpec,
				defaultOptionNameModifier);
	}

}
