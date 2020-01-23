package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * Spring Shell over WebSocket sample application
 *
 * @author Balazs Eszes
 */
@SpringBootApplication
public class WebShellApplication {

	public static void main(final String[] args) {
        SpringApplication.run(WebShellApplication.class, args);
	}

}
