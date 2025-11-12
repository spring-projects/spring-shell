package org.springframework.shell.core.command.support;

import java.util.function.Function;

/**
 * Interface used to modify option long name. Usual use case is i.e. making conversion
 * from a {@code camelCase} to {@code snake-case}.
 */
@FunctionalInterface
public interface OptionNameModifier extends Function<String, String> {

}