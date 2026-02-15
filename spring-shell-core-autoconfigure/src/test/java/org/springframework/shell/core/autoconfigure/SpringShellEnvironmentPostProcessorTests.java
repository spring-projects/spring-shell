package org.springframework.shell.core.autoconfigure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.core.env.CommandLinePropertySource.DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME;

/**
 * @author David Pilar
 */
class SpringShellEnvironmentPostProcessorTests {

	private SpringShellEnvironmentPostProcessor postProcessor;

	private ConfigurableEnvironment environment;

	private SpringApplication application;

	@BeforeEach
	void setUp() {
		postProcessor = new SpringShellEnvironmentPostProcessor();
		environment = new StandardEnvironment();
		application = Mockito.mock(SpringApplication.class);
	}

	@Test
	void shouldDisableInteractiveMode_whenNonOptionArgsIsPresent() {
		environment.getPropertySources()
			.addFirst(new MapPropertySource("test", Map.of(DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME, "some-command")));

		postProcessor.postProcessEnvironment(environment, application);

		assertEquals("false", environment.getProperty("spring.shell.interactive.enabled"));
	}

	@Test
	void shouldNotModifyEnvironment_whenNonOptionArgsIsNotPresent() {
		postProcessor.postProcessEnvironment(environment, application);

		assertNull(environment.getProperty("spring.shell.interactive.enabled"));
	}

	@Test
	void shouldNotModifyEnvironment_whenNonOptionArgsIsEmpty() {
		environment.getPropertySources()
			.addFirst(new MapPropertySource("test", Map.of(DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME, "")));

		postProcessor.postProcessEnvironment(environment, application);

		assertNull(environment.getProperty("spring.shell.interactive.enabled"));
	}

	@Test
	void customPropsShouldHaveHighestPriority() {
		environment.getPropertySources()
			.addFirst(new MapPropertySource("existing", Map.of("spring.shell.interactive.enabled", "true",
					DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME, "some-command")));

		postProcessor.postProcessEnvironment(environment, application);

		assertEquals("false", environment.getProperty("spring.shell.interactive.enabled"));
	}

}