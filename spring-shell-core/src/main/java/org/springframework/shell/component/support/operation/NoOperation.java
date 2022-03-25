package org.springframework.shell.component.support.operation;

import org.jline.keymap.BindingReader;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.support.operation.Operation;

public class NoOperation implements Operation {

    @Override
    public void read(BindingReader bindingReader, StringInput.StringInputContext context) {
        // This is in-place of null operation instead of null check in the implementation.
    }
}
