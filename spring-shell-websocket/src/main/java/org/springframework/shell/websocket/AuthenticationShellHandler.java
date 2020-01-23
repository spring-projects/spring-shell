package org.springframework.shell.websocket;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class AuthenticationShellHandler extends ShellHandler {

	private boolean repromptUsernameOnFail;

	private int maxLoginAttempts;

	private transient Map<WebSocketSession, SocketState> sessions;

	final AuthenticationManager authManager;

	final AuthenticationPromptProvider promptProvider;

	final SecureShellHandler shellHandler;

	public AuthenticationShellHandler(
		@Autowired AuthenticationManager authManager,
		@Autowired AuthenticationPromptProvider promptProvider,
		@Autowired SecureShellHandler shellHandler) {

		this.authManager = authManager;
		this.promptProvider = promptProvider;
		this.shellHandler = shellHandler;
        this.sessions = new HashMap<>();
	}
	
	/**
	 * @param maxLoginAttempts the maxLoginAttempts to set
	 */
	public void setMaxLoginAttempts(int maxLoginAttempts) {
		this.maxLoginAttempts = maxLoginAttempts;
	}

	/**
	 * @param repromptUsernameOnFail the repromptUsernameOnFail to set
	 */
	public void setRepromptUsernameOnFail(boolean repromptUsernameOnFail) {
		this.repromptUsernameOnFail = repromptUsernameOnFail;
	}

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if(!isAuthenticated(session)) {
            sessions.put(session, new SocketState());

            TextMessage loginPrompt = new TextMessage(promptProvider.promptUsername());
            session.sendMessage(loginPrompt);
		}
		else {
			shellHandler.afterConnectionEstablished(session);
		}
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if(!isAuthenticated(session)) {
            SocketState authenticationState = sessions.get(session);
            switch(authenticationState.getState()) {
                case USERNAME: {
					authenticationState.setUsername(message.getPayload());
					authenticationState.setState(State.PASSWORD);

					session.sendMessage(new TextMessage(promptProvider.promptPassword()));					
                }; break;
                case PASSWORD: {
					try {
						UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(authenticationState.getUsername(), message.getPayload());
						Authentication authentication = authManager.authenticate(request);
						SecurityContextHolder.getContext().setAuthentication(authentication);
						
						authenticationState.setAuthentication(authentication);
						authenticationState.setState(State.COMMAND);

						shellHandler.afterConnectionEstablished(session);
					}
					catch(AuthenticationException e) {
						authenticationState.setState(repromptUsernameOnFail ? State.USERNAME : State.PASSWORD);
						if(authenticationState.attempt() >= maxLoginAttempts) {
							session.close(CloseStatus.GOING_AWAY);
						}
					}
                }; break;
                case COMMAND: {
					SecurityContextHolder.getContext().setAuthentication(sessions.get(session).getAuthentication());
					shellHandler.handleTextMessage(session, message);
                }; break;
            }
        }
        else {
			shellHandler.handleTextMessage(session, message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		shellHandler.afterConnectionClosed(session, status);
		
		sessions.remove(session);
    }

    private boolean isAuthenticated(WebSocketSession session) {
        return session.getPrincipal() != null;
    }

    private final static class SocketState {

        private Authentication authentication;

		private String username;
		
		private int attempts;

		private State state = State.USERNAME;

		public void setAuthentication(Authentication authentication) {
			this.authentication = authentication;
		}

		public Authentication getAuthentication() {
			return this.authentication;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getUsername() {
			return this.username;
		}

		public int attempt() {
			return ++this.attempts;
		}

		public void setState(State state) {
			this.state = state;
		}

        public State getState() {
            return this.state;
		}

    }  
    
    private static enum State {
        USERNAME, PASSWORD, COMMAND;
    }

}