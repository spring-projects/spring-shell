package org.springframework.shell;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.core.JLineShellComponent;

/**
 * @author Jon Brisbin
 */
@Configuration
@ComponentScan(basePackages = {
    "org.springframework.shell.commands",
    "org.springframework.shell.converters",
    "org.springframework.shell.plugin.support"
})
public class ShellConfig {

  @Bean public JLineShellComponent shell() {
    return new JLineShellComponent();
  }

}
