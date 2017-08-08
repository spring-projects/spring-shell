package org.springframework.shell.jline;

import org.jline.utils.AttributedString;

/**
 * Called at each REPL cycle to decide what the prompt should be.
 *
 * @author Eric Bottard
 */
public interface PromptProvider {

	AttributedString getPrompt();
}
