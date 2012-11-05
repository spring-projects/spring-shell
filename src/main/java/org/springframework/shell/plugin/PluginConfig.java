package org.springframework.shell.plugin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Jon Brisbin
 */
@Configuration
@ImportResource("classpath*:/META-INF/spring/spring-shell-plugin.xml")
public class PluginConfig {


  @Bean public CommandMarkerPostProcessor commandMarkerPostProcessor() {
    return new CommandMarkerPostProcessor();
  }

  @Bean public ConverterPostProcessor converterPostProcessor() {
    return new ConverterPostProcessor();
  }

}
