package org.springframework.shell.websocket.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.shell.websocket")
public class ShellWebSocketProperties {

    private boolean enabled = false;

    private EndpointProperties endpoint = new EndpointProperties();

    private AuthProperties auth = new AuthProperties();

    public static class EndpointProperties {

        private String basePath = "/cli";
    
        private String[] allowedOrigins = {};
    
        private boolean secured = true;
    
        public String getBasePath() {
            return basePath;
        }
    
        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
    
        public String[] getAllowedOrigins() {
            return allowedOrigins;
        }
    
        public void setAllowedOrigins(String[] allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    
        public boolean isSecured() {
            return secured;
        }
    
        public void setSecured(boolean secured) {
            this.secured = secured;
        }
    
    }

    public static class AuthProperties {

        private boolean allowAnonymous = false;
    
        private String anonymousName = "anonymous";
    
        private String role = null;
    
        private boolean repromptUser = false;
    
        private int maxAttempts = 3;
    
        private String welcomeFormat = "Hello %s, welcome to Spring Shell!";
    
        private String usernamePrompt = "Username: ";
    
        private String passwordPrompt = "Password: ";
    
        public boolean isAllowAnonymous() {
            return allowAnonymous;
        }
    
        public void setAllowAnonymous(boolean allowAnonymous) {
            this.allowAnonymous = allowAnonymous;
        }
    
        public String getAnonymousName() {
            return anonymousName;
        }
    
        public void setAnonymousName(String anonymousName) {
            this.anonymousName = anonymousName;
        }
    
        public String getRole() {
            return role;
        }
    
        public void setRole(String role) {
            this.role = role;
        }
    
        public boolean isRepromptUser() {
            return repromptUser;
        }
    
        public void setRepromptUser(boolean repromptUser) {
            this.repromptUser = repromptUser;
        }
    
        public int getMaxAttempts() {
            return maxAttempts;
        }
    
        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }
    
        public String getWelcomeFormat() {
            return welcomeFormat;
        }
    
        public void setWelcomeFormat(String welcomeFormat) {
            this.welcomeFormat = welcomeFormat;
        }
    
        public String getUsernamePrompt() {
            return usernamePrompt;
        }
    
        public void setUsernamePrompt(String usernamePrompt) {
            this.usernamePrompt = usernamePrompt;
        }
    
        public String getPasswordPrompt() {
            return passwordPrompt;
        }
    
        public void setPasswordPrompt(String passwordPrompt) {
            this.passwordPrompt = passwordPrompt;
        }
    
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public EndpointProperties getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointProperties endpoint) {
        this.endpoint = endpoint;
    }

    public AuthProperties getAuth() {
        return auth;
    }

    public void setAuth(AuthProperties auth) {
        this.auth = auth;
    }
}