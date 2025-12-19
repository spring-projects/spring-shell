/*
 * Copyright 2021-present the original author or authors.
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
package org.springframework.shell.core.command;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jakarta.validation.Path;

import org.jspecify.annotations.Nullable;

import org.springframework.shell.core.ParameterValidationException;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;
import org.springframework.shell.core.command.exit.ExitStatusExceptionMapper;
import org.springframework.shell.core.command.completion.CompletionProvider;

/**
 * Base class helping to build shell commands.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 * @author Mahmoud Ben Hassine
 */
public abstract class AbstractCommand implements Command {

	private final String name;

	private final String description;

	private final String help;

	private final String group;

	private final boolean hidden;

	private AvailabilityProvider availabilityProvider = AvailabilityProvider.alwaysAvailable();

	@Nullable private ExitStatusExceptionMapper exitStatusExceptionMapper;

	private CompletionProvider completionProvider = context -> Collections.emptyList();

	private List<String> aliases = new ArrayList<>();

	public AbstractCommand(String name, String description) {
		this(name, description, "", "", false);
	}

	public AbstractCommand(String name, String description, String group) {
		this(name, description, group, "", false);
	}

	public AbstractCommand(String name, String description, String group, String help, boolean hidden) {
		this.name = name;
		this.description = description;
		this.group = group;
		this.help = help;
		this.hidden = hidden;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getHelp() {
		return this.help;
	}

	@Override
	public boolean isHidden() {
		return this.hidden;
	}

	@Override
	public List<String> getAliases() {
		return this.aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	@Override
	public AvailabilityProvider getAvailabilityProvider() {
		return availabilityProvider;
	}

	public void setAvailabilityProvider(AvailabilityProvider availabilityProvider) {
		this.availabilityProvider = availabilityProvider;
	}

	@Nullable public ExitStatusExceptionMapper getExitStatusExceptionMapper() {
		return exitStatusExceptionMapper;
	}

	public void setExitStatusExceptionMapper(ExitStatusExceptionMapper exitStatusExceptionMapper) {
		this.exitStatusExceptionMapper = exitStatusExceptionMapper;
	}

	@Override
	public CompletionProvider getCompletionProvider() {
		return completionProvider;
	}

	public void setCompletionProvider(CompletionProvider completionProvider) {
		this.completionProvider = completionProvider;
	}

	@Override
	public ExitStatus execute(CommandContext commandContext) throws Exception {
		Availability availability = getAvailabilityProvider().get();
		if (!availability.isAvailable()) {
			println("Command '" + getName() + "' exists but is not currently available because "
					+ availability.reason(), commandContext);
			return ExitStatus.AVAILABILITY_ERROR;
		}
		List<CommandOption> options = commandContext.parsedInput().options();
		if (options.size() == 1 && isHelp(options.get(0))) {
			println(getHelp(), commandContext);
			return ExitStatus.OK;
		}
		try {
			return doExecute(commandContext);
		}
		catch (ParameterValidationException parameterValidationException) {
			PrintWriter outputWriter = commandContext.outputWriter();
			outputWriter.println("The following constraints were not met:");
			parameterValidationException.getConstraintViolations().forEach(violation -> {
				Path propertyPath = violation.getPropertyPath();
				String violationMessage = violation.getMessage();
				String errorMessage = String.format("\t--%s: %s", extractPropertyName(propertyPath), violationMessage);
				outputWriter.println(errorMessage);
			});
			return ExitStatus.USAGE_ERROR;
		}
		catch (Exception e) {
			if (getExitStatusExceptionMapper() != null) {
				return getExitStatusExceptionMapper().apply(e);
			}
			else {
				throw e;
			}
		}
	}

	private String extractPropertyName(Path propertyPath) {
		String path = propertyPath.toString();
		int lastIndexOfDot = path.lastIndexOf(".");
		return lastIndexOfDot == -1 ? path : path.substring(lastIndexOfDot + 1);
	}

	protected void println(String message, CommandContext commandContext) {
		PrintWriter outputWriter = commandContext.outputWriter();
		outputWriter.println(message);
		outputWriter.flush();

	}

	protected boolean isHelp(CommandOption option) {
		return option.longName().equalsIgnoreCase("help") || option.shortName() == 'h';
	}

	public abstract ExitStatus doExecute(CommandContext commandContext) throws Exception;

	// Commands are uniquely identified by their name in a command registry
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AbstractCommand that))
			return false;
		return Objects.equals(getName(), that.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getName());
	}

}
