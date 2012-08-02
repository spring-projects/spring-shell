/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.event;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.shell.event.ShellStatus.Status;
import org.springframework.util.Assert;

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
