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
package org.springframework.shell.support.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.springframework.util.Assert;

/**
 * Defers the publication of JDK {@link LogRecord} instances until a target {@link Handler} is registered.
 *
 * <p>
 * This class is useful if a target {@link Handler} cannot be instantiated before {@link LogRecord} instances are being
 * published. This may be the case if the target {@link Handler} requires the establishment of complex publication
 * infrastructure such as a GUI, message queue, IoC container and the establishment of that infrastructure may produce
 * log messages that should ultimately be delivered to the target {@link Handler}.
 *
 * <p>
 * In recognition that sometimes the target {@link Handler} may never be registered (perhaps due to failures configuring
 * its supporting infrastructure), this class supports a fallback mode. When in fallback mode, a fallback {@link Handler}
 * will receive all previous and future {@link LogRecord} instances. Fallback mode is automatically triggered if a
 * {@link LogRecord} is published at the fallback {@link Level}. Fallback mode is also triggered if the {@link #flush()}
 * or {@link #close()} method is involved and the target {@link Handler} has never been registered.
 *
 * @author Ben Alex
 * @since 1.0
 */
public class DeferredLogHandler extends Handler {

	// Fields
	private final List<LogRecord> logRecords = Collections.synchronizedList(new ArrayList<LogRecord>());
	private final Handler fallbackHandler;
	private final Level fallbackPushLevel;
	private boolean fallbackMode = false;
	private Handler targetHandler;

	/**
	 * Creates an instance that will publish all recorded {@link LogRecord} instances to the specified fallback
	 * {@link Handler} if an event of the specified {@link Level} is received.
	 *
	 * @param fallbackHandler to publish events to (mandatory)
	 * @param fallbackPushLevel the level which will trigger an event publication (mandatory)
	 */
	public DeferredLogHandler(final Handler fallbackHandler, final Level fallbackPushLevel) {
		Assert.notNull(fallbackHandler, "Fallback handler required");
		Assert.notNull(fallbackPushLevel, "Fallback push level required");
		this.fallbackHandler = fallbackHandler;
		this.fallbackPushLevel = fallbackPushLevel;
	}

	@Override
	public void close() throws SecurityException {
		if (targetHandler == null) {
			fallbackMode = true;
		}
		if (fallbackMode) {
			publishLogRecordsTo(fallbackHandler);
			fallbackHandler.close();
			return;
		}
		targetHandler.close();
	}

	@Override
	public void flush() {
		if (targetHandler == null) {
			fallbackMode = true;
		}
		if (fallbackMode) {
			publishLogRecordsTo(fallbackHandler);
			fallbackHandler.flush();
			return;
		}
		targetHandler.flush();
	}

	/**
	 * Stores the log record internally.
	 */
	@Override
	public void publish(final LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}
		if (fallbackMode) {
			fallbackHandler.publish(record);
			return;
		}
		if (targetHandler != null) {
			targetHandler.publish(record);
			return;
		}
		synchronized (logRecords) {
			logRecords.add(record);
		}
		if (!fallbackMode && record.getLevel().intValue() >= fallbackPushLevel.intValue()) {
			fallbackMode = true;
			publishLogRecordsTo(fallbackHandler);
		}
	}

	/**
	 * @return the target {@link Handler}, or null if there is no target {@link Handler} defined so far
	 */
	public Handler getTargetHandler() {
		return targetHandler;
	}

	public void setTargetHandler(final Handler targetHandler) {
		Assert.notNull(targetHandler, "Must specify a target handler");
		this.targetHandler = targetHandler;
		if (!fallbackMode) {
			publishLogRecordsTo(this.targetHandler);
		}
	}

	private void publishLogRecordsTo(final Handler destination) {
		synchronized (logRecords) {
			for (LogRecord record : logRecords) {
				destination.publish(record);
			}
			logRecords.clear();
		}
	}
}
