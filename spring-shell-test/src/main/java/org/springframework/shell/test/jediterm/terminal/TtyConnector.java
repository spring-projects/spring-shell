/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.test.jediterm.terminal;

import java.io.IOException;

/**
 * Interface to tty.
 *
 * @author jediterm authors
 */
public interface TtyConnector {

	boolean init();

	void close();

	default void resize(int width, int height) {
		// support old implementations not overriding this method
		resize(width, height);
		// StackOverflowError is only possible if both resize(Dimension) and resize(Dimension,Dimension) are not overridden.
	}

	// /**
	//  * @deprecated use {@link #resize(Dimension)} instead
	//  */
	// @SuppressWarnings("unused")
	// @Deprecated
	// default void resize(int width, int height, int pixelSizeWidth, int pixelSizeHeight) {
	// 	// support old code that calls this method on new implementations (not overriding this deprecated method)
	// 	resize(width, height);
	// }

	String getName();

	int read(char[] buf, int offset, int length) throws IOException;

	void write(byte[] bytes) throws IOException;

	boolean isConnected();

	void write(String string) throws IOException;

	int waitFor() throws InterruptedException;

	boolean ready() throws IOException;
}
