package org.springframework.shell.websocket.configuration;

import java.security.Principal;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.websocket.AuthenticationPromptProvider;
import org.springframework.shell.websocket.PrincipalPromptProvider;

@Configuration
@ConfigurationProperties(prefix = "spring.shell.websocket")
@EnableConfigurationProperties(ShellWebSocketProperties.class)
public class ShellAutoConfiguration {

    @Autowired
    private ShellWebSocketProperties websocket;

    @Autowired
    private PromptProvider promptProvider;

    @ConditionalOnMissingBean
    @Bean
    public AuthenticationPromptProvider authenticationPromptProvider() {
        return new AuthenticationPromptProvider(){
        
            @Override
            public AttributedString promptUsername() {
                return new AttributedString(websocket.getAuth().getUsernamePrompt(), AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
            }
        
            @Override
            public AttributedString promptPassword() {
                return new AttributedString(websocket.getAuth().getPasswordPrompt(), AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
            }
        };
    }

    @ConditionalOnMissingBean
    @Bean
    public PrincipalPromptProvider principalPromptProvider() {
        return new PrincipalPromptProvider(){
        
            @Override
            public AttributedString welcome(Principal principal) {
                return new AttributedString(String.format(websocket.getAuth().getWelcomeFormat(), getPrincipalName(principal)));
            }
        
            @Override
            public AttributedString prompt(Principal principal) {
                return new AttributedString(getPrincipalName(principal) + "@" + promptProvider.getPrompt());
            }
        };
    }

    private String getPrincipalName(Principal principal) {
        return (principal != null ? principal.getName() : websocket.getAuth().getAnonymousName());
    }

}