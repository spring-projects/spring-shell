package org.springframework.shell.component.support.operation;

import org.jline.keymap.BindingReader;
import org.springframework.shell.component.StringInput;

public class OperationChar implements Operation {

    @Override
    public void read(BindingReader bindingReader, StringInput.StringInputContext context) {
        String lastBinding = bindingReader.getLastBinding();
        String input = context.getInput();
        if (input == null) {
            input = lastBinding;
        }
        else {
            input = input + lastBinding;
        }
        context.setInput(input);
    }
}
