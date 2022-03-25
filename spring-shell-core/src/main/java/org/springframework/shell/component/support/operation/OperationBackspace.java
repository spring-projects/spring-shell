package org.springframework.shell.component.support.operation;

import org.jline.keymap.BindingReader;
import org.springframework.shell.component.StringInput;
import org.springframework.util.StringUtils;

public class OperationBackspace implements Operation {

    @Override
    public void read(BindingReader bindingReader, StringInput.StringInputContext context) {
        String input = context.getInput();
        if (StringUtils.hasLength(input)) {
            input = input.length() > 1 ? input.substring(0, input.length() - 1) : null;
        }
        context.setInput(input);
    }
}
