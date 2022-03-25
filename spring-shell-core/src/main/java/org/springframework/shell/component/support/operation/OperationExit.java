package org.springframework.shell.component.support.operation;

import org.jline.keymap.BindingReader;
import org.springframework.shell.component.StringInput;
import org.springframework.util.StringUtils;

public class OperationExit implements Operation {

    @Override
    public void read(BindingReader bindingReader, StringInput.StringInputContext context) {
        if (StringUtils.hasText(context.getInput())) {
            context.setResultValue(context.getInput());
        }
        else if (context.getDefaultValue() != null) {
            context.setResultValue(context.getDefaultValue());
        }
    }
}
