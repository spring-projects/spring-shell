package org.springframework.shell.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class StandardShellHandler extends ShellHandler {

    final PromptProvider promptProvider;

    final Shell shell;

    public StandardShellHandler(
        @Autowired PromptProvider promptProvider,
        @Autowired Shell shell) {

        this.promptProvider = promptProvider;
        this.shell = shell;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage(promptProvider.getPrompt()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String result = shell.evaluate(() -> message.getPayload()).toString();
        
        session.sendMessage(new TextMessage(result));

        session.sendMessage(new TextMessage(promptProvider.getPrompt()));
    }
    
}