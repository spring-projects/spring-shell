package org.springframework.shell.boot;

import org.springframework.shell.jline.NonInteractiveShellRunner;

/**
 * Callback interface that can be implemented by beans wishing to customize the
 * auto-configured {@link NonInteractiveShellRunner}.
 *
 * @author Chris Bono
 * @since 2.1.0
 */
@FunctionalInterface
public interface NonInteractiveShellRunnerCustomizer {
	/**
	 * Customize the {@link NonInteractiveShellRunner}.
	 * @param shellRunner the non-interactive shell runner to customize
	 */
	void customize(NonInteractiveShellRunner shellRunner);

}
