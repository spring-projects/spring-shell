package org.springframework.roo.shell.event;

/**
 * Implemented by classes that wish to be notified of shell status changes.
 *
 * @author Ben Alex
 * @since 1.0
 */
public interface ShellStatusListener {

	/**
	 * Invoked by the shell to report a new status.
	 *
	 * @param oldStatus the old status
	 * @param newStatus the new status
	 */
	void onShellStatusChange(ShellStatus oldStatus, ShellStatus newStatus);
}
