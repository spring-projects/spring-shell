package org.springframework.shell;

/**
 * To be implemented by command result objects that can adapt to the terminal size when they are being rendered.
 *
 * <p>An object which does not implement this interface will simply be rendered by invoking its {@link #toString()}
 * method.</p>
 *
 * @author Eric Bottard
 */
public interface TerminalSizeAware {

	CharSequence render(int terminalWidth);
}
