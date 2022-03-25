package org.springframework.shell.component.support.operation;

import org.jline.keymap.BindingReader;
import org.springframework.shell.component.ConfirmationInput;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.context.ComponentContext;

public interface Operation {

    public void read(BindingReader bindingReader, StringInput.StringInputContext context);
}
