package org.springframework.shell.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @author nmeierling
 */
@Configuration
@PropertySource("classpath:application.properties")
public class JLineConfig {
    @Autowired
    Environment env;

    public boolean isHandleUserInterrupt() {
        return Boolean.parseBoolean(env.getProperty("jline.handleuserinterrupt"));
    }
}
