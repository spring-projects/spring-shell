package org.springframework.shell.core.autoconfigure;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

import static org.springframework.core.env.CommandLinePropertySource.DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME;

/**
 * This class disables the interactive shell mode by setting
 * {@code spring.shell.interactive.enabled} to {@code false} when certain command-line
 * arguments are passed to the application.
 *
 * @author David Pilar
 */
public class SpringShellEnvironmentPostProcessor implements EnvironmentPostProcessor {

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String[] args = environment.getProperty(DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME, String[].class, new String[] {});
		if (args.length > 0) {
			environment.getPropertySources()
				.addFirst(new MapPropertySource("interactiveDisabledProps",
						Map.of("spring.shell.interactive.enabled", "false")));
		}
	}

}
