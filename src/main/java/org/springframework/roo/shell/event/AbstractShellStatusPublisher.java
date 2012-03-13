package org.springframework.roo.shell.event;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.roo.shell.ParseResult;
import org.springframework.roo.shell.event.ShellStatus.Status;
import org.springframework.roo.support.util.Assert;

/**
 * Provides a convenience superclass for those shells wishing to publish status messages.
 *
 * @author Ben Alex
 * @since 1.0
 */
public abstract class AbstractShellStatusPublisher implements ShellStatusProvider {

	// Fields
	protected Set<ShellStatusListener> shellStatusListeners = new CopyOnWriteArraySet<ShellStatusListener>();
	protected ShellStatus shellStatus = new ShellStatus(Status.STARTING);

	public final void addShellStatusListener(final ShellStatusListener shellStatusListener) {
		Assert.notNull(shellStatusListener, "Status listener required");
		synchronized (shellStatus) {
			shellStatusListeners.add(shellStatusListener);
		}
	}

	public final void removeShellStatusListener(final ShellStatusListener shellStatusListener) {
		Assert.notNull(shellStatusListener, "Status listener required");
		synchronized (shellStatus) {
			shellStatusListeners.remove(shellStatusListener);
		}
	}

	public final ShellStatus getShellStatus() {
		synchronized (shellStatus) {
			return shellStatus;
		}
	}

	protected void setShellStatus(final Status shellStatus) {
		setShellStatus(shellStatus, null, null);
	}

	protected void setShellStatus(final Status shellStatus, final String msg, final ParseResult parseResult) {
		Assert.notNull(shellStatus, "Shell status required");

		synchronized (this.shellStatus) {
			ShellStatus st;
			if (msg == null || msg.length() == 0) {
				st = new ShellStatus(shellStatus);
			} else {
				st = new ShellStatus(shellStatus, msg, parseResult);
			}

			if (this.shellStatus.equals(st)) {
				return;
			}

			for (ShellStatusListener listener : shellStatusListeners) {
				listener.onShellStatusChange(this.shellStatus, st);
			}
			this.shellStatus = st;
		}
	}
}
