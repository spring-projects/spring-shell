package org.springframework.shell.websocket;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.Shell;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class SecureShellHandler extends ShellHandler {

    private String requiredAuthority;

    final PrincipalPromptProvider principalPromptProvider;

    final Shell shell;

    public SecureShellHandler(
        @Autowired PrincipalPromptProvider principalPromptProvider,
        @Autowired Shell shell) {
        
        this.shell = shell;
        this.principalPromptProvider = principalPromptProvider;
    }

    public void setRequiredAuthority(String requiredAuthority) {
        this.requiredAuthority = requiredAuthority;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(requiredAuthority != null && !hasAuthority(auth, requiredAuthority)) {
            //close session
        }

        session.sendMessage(new TextMessage(
            principalPromptProvider.welcome(auth) + "\n" +
            principalPromptProvider.prompt(auth)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Object result = shell.evaluate(() -> message.getPayload());

        if(result instanceof ExitRequest) {
            session.close();
        }
        else {
            session.sendMessage(new TextMessage(
                result.toString() + "\n" +
                principalPromptProvider.prompt(auth)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        
    }

    private static boolean hasAuthority(Authentication auth, String authority) {
        if(auth == null) {
            return false;
        }
        return auth.getAuthorities()
                    .stream()
                    .filter(a -> a.getAuthority().equals(authority))
                    .findFirst().isPresent();
    }

}