package com.example;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 *
 * Test commands to demonstrate Spring Shell WebSocket capabilities.
 *
 * @author Balazs Eszes
 */
@ShellComponent
@ShellCommandGroup("System commands")
public class ShellCommands {

    /*
	@ShellMethod(value = "Greet users")
	public String greet(Optional<String> name, Principal principal) {
        if(!name.isPresent()) {
            return "Hello, " + principal.getName();
        }
        return "Hello, " + name.get();
    }
    */

    @ShellMethod(value = "Testing nullable")
    public String testNullable(@Nullable String value) {
        if(value == null) {
            return "Value: not present";
        }
        return "Value: " + value;
    }

    @ShellMethod(value = "Testing optional")
    public String testOptional(Optional<Money> value) {
        if(!value.isPresent()) {
            return "Money: not present";
        }
        return "Money: " + value.get().value;
    }

    @ShellMethod(value = "Testing enum completion")
    public String testEnum(Fruit fruit) {
        return "Fruit: " + fruit;
    }

    @ShellMethod(value = "Testing nullable completion")
    public String testNullableComplete(@Nullable Fruit fruit) {
        return "Fruit: " + fruit;
    }

    @ShellMethod(value = "Testing optional completion")
    public String testOptionalComplete(Optional<Fruit> fruit) {
        if(!fruit.isPresent()) {
            return "No fruit";
        }
        return "Fruit: " + fruit;
    }

    @ShellMethod(value = "Testing arity")
    public String testArity(String[] names) {
        return "Success";
    }
    
    @PreAuthorize("hasRole(#role)")
    @ShellMethod(value = "Test role")
    public String testRole(String role) {
        return "You have role: " + role;
    }

    @PreAuthorize("hasAuthority(#authority)")
    @ShellMethod(value = "Test authority")
    public String testAuthority(String authority) {
        return "You have authority: " + authority;
    }
}
