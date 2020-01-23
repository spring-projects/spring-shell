package org.springframework.shell.websocket.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.AuthorizedUrl;
import org.springframework.shell.websocket.PrincipalResolver;

@ConditionalOnProperty( name = "spring.shell.websocket.enabled",
                        havingValue = "true")
@ConditionalOnClass(WebSecurityConfigurerAdapter.class)
@EnableWebSecurity
@Configuration
@EnableConfigurationProperties(ShellWebSocketProperties.class)
public class WebSocketSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public PrincipalResolver principalResolver() {
        return new PrincipalResolver();
    }

    @Autowired
    private ShellWebSocketProperties websocket;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AuthorizedUrl chain = http.authorizeRequests().antMatchers(websocket.getEndpoint().getBasePath());
        
        if(websocket.getEndpoint().isSecured()) {
            if(websocket.getAuth().getRole() != null) {
                chain.hasRole(websocket.getAuth().getRole());
            }
            else {
                chain.authenticated();
            }
        }
        else {
            chain.permitAll();
        }
    }
    
}