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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.springframework.shell.support.util.IOUtils;
import org.springframework.util.Assert;

/**
 * Wraps an {@link OutputStream} and automatically passes each line to the {@link Logger}
 * when {@link OutputStream#flush()} or {@link OutputStream#close()} is called.
 *
 * @author Ben Alex
 * @since 1.1
 */
public class LoggingOutputStream extends OutputStream {

	// Constants
	protected static final Logger LOGGER = HandlerUtils.getLogger(LoggingOutputStream.class);

	// Fields
	private final Level level;
	private String sourceClassName = LoggingOutputStream.class.getName();
	private int count;
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();

	/**
	 * Constructor
	 *
	 * @param level the level at which to log (required)
	 */
	public LoggingOutputStream(final Level level) {
		Assert.notNull(level, "A logging level is required");
		this.level = level;
	}

	@Override
	public void write(final int b) throws IOException {
		baos.write(b);
		count++;
	}

	@Override
	public void flush() throws IOException {
		if (count > 0) {
			String msg  = new String(baos.toByteArray());
			LogRecord record = new LogRecord(level, msg);
			record.setSourceClassName(sourceClassName);
			try {
				LOGGER.log(record);
			} finally {
				count = 0;
				IOUtils.closeQuietly(baos);
				baos = new ByteArrayOutputStream();
			}
		}
	}

	@Override
	public void close() throws IOException {
		flush();
	}

	public String getSourceClassName() {
		return sourceClassName;
	}

	public void setSourceClassName(final String sourceClassName) {
		this.sourceClassName = sourceClassName;
	}
}
