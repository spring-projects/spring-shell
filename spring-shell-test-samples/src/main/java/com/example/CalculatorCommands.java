package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 *
 * Basic commands for the illustrative Spring Shell Calculator application.
 *
 * @author Sualeh Fatehi
 */
@ShellComponent
@ShellCommandGroup("Calculator Commands")
public class CalculatorCommands {

	@Autowired
	private CalculatorState state;

	@ShellMethod(value = "Add two integers")
	public int add(final int a, final int b) {
		return a + b;
	}

	@ShellMethod(value = "Add an integer to the value in memory")
	public int addToMemory(final int b) {
		final int sum = state.getMemory() + b;
		state.setMemory(sum);
		return sum;
	}

}
