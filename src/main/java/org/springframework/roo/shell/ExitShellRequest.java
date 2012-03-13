package org.springframework.roo.shell;

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
