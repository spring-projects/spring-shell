package org.springframework.shell.websocket;

import java.security.Principal;

import org.jline.utils.AttributedString;

public interface PrincipalPromptProvider {

    public AttributedString prompt(Principal principal);

    public AttributedString welcome(Principal principal);
}