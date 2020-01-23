package org.springframework.shell.websocket;

import org.jline.utils.AttributedString;

public interface AuthenticationPromptProvider {
    
    public AttributedString promptPassword();

    public AttributedString promptUsername();
}