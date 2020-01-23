package org.springframework.shell.websocket.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.websocket.AuthenticationPromptProvider;
import org.springframework.shell.websocket.AuthenticationShellHandler;
import org.springframework.shell.websocket.PrincipalPromptProvider;
import org.springframework.shell.websocket.SecureShellHandler;
import org.springframework.shell.websocket.ShellHandler;
import org.springframework.shell.websocket.StandardShellHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;

@ConditionalOnClass(WebSocketConfigurer.class)
@ConditionalOnProperty( name = "spring.shell.websocket.enabled",
                        havingValue = "true")
@Configuration
@EnableConfigurationProperties(ShellWebSocketProperties.class)
public class WebSocketHandlerAutoConfiguration {

    @Autowired
    private ShellWebSocketProperties websocket;

    @Bean
    @ConditionalOnClass(WebSecurityConfigurerAdapter.class)
    public SecureShellHandler secureShell(
        @Autowired PrincipalPromptProvider promptProvider,
        @Autowired Shell shell) {

        SecureShellHandler handler = new SecureShellHandler(promptProvider, shell);
        handler.setRequiredAuthority(websocket.getAuth().getRole());

        return handler;
    }

    @Bean
    @Primary
    @ConditionalOnClass(WebSecurityConfigurerAdapter.class)
    @ConditionalOnProperty(name = "spring.shell.websocket.endpoint.secured",
                            havingValue = "false")
    public AuthenticationShellHandler authShell(
        @Autowired AuthenticationManager authManager,
        @Autowired AuthenticationPromptProvider promptProvider,
        @Autowired SecureShellHandler secureShell) {
            
        AuthenticationShellHandler handler = new AuthenticationShellHandler(authManager, promptProvider, secureShell);
        handler.setMaxLoginAttempts(websocket.getAuth().getMaxAttempts());
        handler.setRepromptUsernameOnFail(websocket.getAuth().isRepromptUser());

        return handler;
    }

    @Bean
    @ConditionalOnMissingBean(ShellHandler.class)
    public StandardShellHandler standardShell(
        @Autowired PromptProvider promptProvider,
        @Autowired Shell shell) {

        return new StandardShellHandler(promptProvider, shell);
    }
}