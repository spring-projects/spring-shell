/*
 * Copyright 2017-2023 the original author or authors.
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jline.utils.AttributedString;

import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.shell.core.Utils;
import org.springframework.shell.core.command.CommandRegistration;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.standard.AbstractCommand;
import org.springframework.shell.tui.style.TemplateExecutor;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

/**
 * A command to display help about all available commands.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
public class Help extends AbstractCommand {

	/**
	 * Marker interface for beans providing {@literal help} functionality to the shell.
	 *
	 * <p>
	 * To override the help command, simply register your own bean implementing that interface
	 * and the standard implementation will back off.
	 * </p>
	 *
	 * <p>
	 * To disable the {@literal help} command entirely, set the
	 * {@literal spring.shell.command.help.enabled=false} property in the environment.
	 * </p>
	 *
	 * @author Eric Bottard
	 */
	public interface Command {
	}

	private boolean showGroups = true;
	private TemplateExecutor templateExecutor;
	private @Nullable String commandTemplate;
	private @Nullable String commandsTemplate;


	public Help(TemplateExecutor templateExecutor) {
		this.templateExecutor = templateExecutor;
	}

	@org.springframework.shell.core.command.annotation.Command(command = "Display help about available commands")
	public AttributedString help(
			@Option(defaultValue = Option.NULL, shortNames = { 'C'}, longNames = {"--command" }, description = "The command to obtain help for.", arity = CommandRegistration.OptionArity.ONE_OR_MORE)
			// FIXME @OptionValues(provider = CommandValueProvider.class) // How to migrate valueProvider?
			String @Nullable [] command)

			throws IOException {
		if (command == null) {
			return renderCommands();
		}
		else {
			String commandStr = Stream.of(command)
				.map(c -> c.trim())
				.collect(Collectors.joining(" "));
			return renderCommand(commandStr);
		}
	}

	/**
	 * Sets a location for a template rendering command help.
	 *
	 * @param commandTemplate the command template location
	 */
	public void setCommandTemplate(String commandTemplate) {
		this.commandTemplate = commandTemplate;
	}

	/**
	 * Sets a location for a template rendering commands help.
	 *
	 * @param commandsTemplate the commands template location
	 */
	public void setCommandsTemplate(String commandsTemplate) {
		this.commandsTemplate = commandsTemplate;
	}

	/**
	 * Sets if groups should be shown in a listing, defaults to true. If not enabled
	 * a simple list is shown without groups.
	 *
	 * @param showGroups the flag to show groups
	 */
	public void setShowGroups(boolean showGroups) {
		this.showGroups = showGroups;
	}

	private AttributedString renderCommands() {
		Map<String, CommandRegistration> registrations = Utils
				.removeHiddenCommands(getCommandRegistry().getRegistrations());

		boolean isStg = commandTemplate != null && commandTemplate.endsWith(".stg");

		Map<String, Object> model = new HashMap<>();
		model.put("model", GroupsInfoModel.of(this.showGroups, registrations));

		Assert.notNull(commandsTemplate, "'commandsTemplate' must not be null");
		String templateResource = resourceAsString(getResourceLoader().getResource(commandsTemplate));
		return isStg ? this.templateExecutor.renderGroup(templateResource, model)
				: this.templateExecutor.render(templateResource, model);
	}

	private AttributedString renderCommand(String command) {
		Map<String, CommandRegistration> registrations = Utils
				.removeHiddenCommands(getCommandRegistry().getRegistrations());
		CommandRegistration registration = registrations.get(command);
		if (registration == null) {
			throw new IllegalArgumentException("Unknown command '" + command + "'");
		}

		boolean isStg = commandTemplate != null && commandTemplate.endsWith(".stg");

		Map<String, Object> model = new HashMap<>();
		model.put("model", CommandInfoModel.of(command, registration));

		Assert.notNull(commandTemplate, "'commandsTemplate' must not be null");
		String templateResource = resourceAsString(getResourceLoader().getResource(commandTemplate));
		return isStg ? this.templateExecutor.renderGroup(templateResource, model)
				: this.templateExecutor.render(templateResource, model);
	}

	private static String resourceAsString(Resource resource) {
		try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
			return FileCopyUtils.copyToString(reader);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
