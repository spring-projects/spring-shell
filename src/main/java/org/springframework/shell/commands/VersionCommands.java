package org.springframework.shell.commands;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.shell.core.CommandMarker;
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
public class VersionCommands implements CommandMarker, ApplicationContextAware {

	private ApplicationContext ctx;

	@CliCommand(value = { "version" }, help = "Displays shell version")
	public String version() {
		return PluginUtils.getHighestPriorityProvider(ctx, BannerProvider.class).getVersion();
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = applicationContext;
	}
}