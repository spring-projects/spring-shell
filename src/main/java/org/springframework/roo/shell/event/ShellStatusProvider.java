package org.springframework.roo.shell.event;

/**
 * Implemented by shells that support the publication of shell status changes.
 *
 * <p>
 * Implementations are not required to provide any guarantees with respect to the order
 * in which notifications are delivered to listeners.
 *
 * <p>
 * Implementations must permit modification of the listener list, even while delivering
 * event notifications to listeners. However, listeners do not receive any guarantee that
 * their addition or removal from the listener list will be effective or not for any event
 * notification that is currently proceeding.
 *
 * <p>
 * Implementations must ensure that status notifications are only delivered when an actual
 * change has taken place.
 *
 * @author Ben Alex
 * @since 1.0
 */
public interface ShellStatusProvider {

	/**
	 * Registers a new status listener.
	 *
	 * @param shellStatusListener to register (cannot be null)
	 */
	void addShellStatusListener(ShellStatusListener shellStatusListener);

	/**
	 * Removes an existing status listener.
	 *
	 * <p>
	 * If the presented status listener is not found, the method returns without exception.
	 *
	 * @param shellStatusListener to remove (cannot be null)
	 */
	void removeShellStatusListener(ShellStatusListener shellStatusListener);

	/**
	 * Returns the current shell status.
	 *
	 * @return the current status (never null)
	 */
	ShellStatus getShellStatus();
}
