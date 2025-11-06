package org.springframework.shell.core.command;

import java.util.function.Supplier;

/**
 * Interface used to supply instance of a {@link Command.Builder}. Meant to be a single
 * point access to centrally configured builder in an application context.
 */
@FunctionalInterface
public interface BuilderSupplier extends Supplier<Command.Builder> {

}
