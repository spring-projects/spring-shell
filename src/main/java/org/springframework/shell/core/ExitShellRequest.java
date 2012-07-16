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
package org.springframework.shell.core;

/**
 * An immutable representation of a request to exit the shell.
 *
 * <p>
 * Implementations of the shell are free to handle these requests in whatever
 * way they wish. Callers should not expect an exit request to be completed.
 *
 * @author Ben Alex
 */
public class ExitShellRequest {

	// Constants
	public static final ExitShellRequest NORMAL_EXIT = new ExitShellRequest(0);
	public static final ExitShellRequest FATAL_EXIT = new ExitShellRequest(1);
	public static final ExitShellRequest JVM_TERMINATED_EXIT = new ExitShellRequest(99); // Ensure 99 is maintained in o.s.r.bootstrap.Main as it's the default for a null roo.exit code

	// Fields
	private final int exitCode;

	private ExitShellRequest(final int exitCode) {
		this.exitCode = exitCode;
	}

	public int getExitCode() {
		return exitCode;
	}
}
