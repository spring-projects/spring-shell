/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.commands;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.shell.plugin.PluginUtils;
import org.springframework.stereotype.Component;

/**
 * Essential built-in shell commands.
 *
 * @author Mark Pollack
 * @author Erwin Vervaet
 */
@Component
public class EssentialCommands implements CommandMarker, ApplicationContextAware {

	private ApplicationContext ctx;

	@CliCommand(value = { "exit", "quit" }, help = "Exits the shell")
	public ExitShellRequest quit() {
		return ExitShellRequest.NORMAL_EXIT;
	}

	@CliCommand(value = { "version" }, help = "Displays shell version")
	public String version() {
		return PluginUtils.getHighestPriorityProvider(ctx, BannerProvider.class).getVersion();
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}
}
