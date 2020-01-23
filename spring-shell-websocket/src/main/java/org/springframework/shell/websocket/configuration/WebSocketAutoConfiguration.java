package org.springframework.shell.websocket.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.websocket.ShellHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@ConditionalOnProperty( name = "spring.shell.websocket.enabled",
                        havingValue = "true")
@ConditionalOnClass(WebSocketConfigurer.class)
@Configuration
@EnableWebSocket
@AutoConfigureAfter(WebSocketHandlerAutoConfiguration.class)
@EnableConfigurationProperties(ShellWebSocketProperties.class)
public class WebSocketAutoConfiguration implements WebSocketConfigurer {

    @Autowired
    private ShellWebSocketProperties websocket;

    final ShellHandler shellHandler;

    public WebSocketAutoConfiguration(
        @Autowired ShellHandler shellHandler) {
        
        this.shellHandler = shellHandler;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(shellHandler, websocket.getEndpoint().getBasePath())
                .setAllowedOrigins(websocket.getEndpoint().getAllowedOrigins());
    }

}