/**
 * 
 */
package org.springframework.shell.samples.helloworld;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.samples.hiworld.commands.HiWorldCommands;

/**
 * Spring Java-based Configuration
 * 
 * @author Robin Howlett
 *
 */
@Configuration
@ComponentScan(basePackageClasses = HiWorldCommands.class)
public class AppConfig {

}
