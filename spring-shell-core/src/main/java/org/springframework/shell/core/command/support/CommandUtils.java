package org.springframework.shell.core.command.support;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CommandUtils {

	public static String toCommand(String[] commands) {
		String delimiter = " ";
		return Arrays.stream(commands)
			.flatMap(cmd -> Stream.of(cmd.split(delimiter)))
			.filter(StringUtils::hasText)
			.map(String::trim)
			.collect(Collectors.joining(delimiter));
	}

}
