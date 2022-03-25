package org.springframework.shell.jline;

import org.springframework.shell.Shell;
import org.springframework.shell.ShellRunner;
import org.springframework.shell.context.ShellContext;

public abstract class AbstractShellRunner implements ShellRunner {

    protected final Shell shell;

    protected AbstractShellRunner(Shell shell) {
        this.shell = shell;
    }

}
