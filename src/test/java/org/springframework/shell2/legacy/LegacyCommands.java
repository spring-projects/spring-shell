package org.springframework.shell2.legacy;

import java.lang.reflect.Method;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.util.ReflectionUtils;

/**
 * Created by ericbottard on 09/12/15.
 */
public class LegacyCommands implements CommandMarker {

	public static final Method REGISTER_METHOD = ReflectionUtils.findMethod(LegacyCommands.class, "register", String.class, ArtifactType.class, String.class, boolean.class);
	public static final Method SUM_METHOD = ReflectionUtils.findMethod(LegacyCommands.class, "sum", int.class, int.class);

	@CliCommand(value = "register module", help = "Register a new module")
	public String register(
			@CliOption(mandatory = true,
					key = {"", "name"},
					help = "the name for the registered module")
			String name,
			@CliOption(mandatory = true,
					key = {"type"},
					help = "the type for the registered module")
			ArtifactType type,
			@CliOption(mandatory = true,
					key = {"coordinates", "coords"},
					help = "coordinates to the module archive")
			String coordinates,
			@CliOption(key = "force",
					help = "force update if module already exists (only if not in use)",
					specifiedDefaultValue = "true",
					unspecifiedDefaultValue = "false")
			boolean force) {
		return String.format(("Successfully registered module '%s:%s'"), type, name);
	}

	@CliCommand("sum")
	public int sum(@CliOption(key = "v1", unspecifiedDefaultValue = "38") int a, @CliOption(key = "v2", specifiedDefaultValue = "42") int b) {
		return a + b;
	}

}
