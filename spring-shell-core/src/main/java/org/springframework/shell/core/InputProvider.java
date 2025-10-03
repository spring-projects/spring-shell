package org.springframework.shell.core;

import org.jspecify.annotations.Nullable;

/**
 * To be implemented by components able to provide a "line" of user input, whether
 * interactively or by batch.
 *
 * @author Eric Bottard
 * @author Piotr Olaszewski
 */
public interface InputProvider {

	/**
	 * Return text entered by user to invoke commands.
	 *
	 * <p>
	 * Returning {@literal null} indicates end of input, requesting shell exit.
	 * </p>
	 */
	@Nullable Input readInput();

}
