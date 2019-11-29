package com.example;

import org.springframework.stereotype.Component;

/**
 *
 * Program state for the illustrative Spring Shell Calculator application.
 *
 * @author Sualeh Fatehi
 */
@Component
public class CalculatorState {

	private int memory;

	public void clear() {
		memory = 0;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(final int memory) {
		this.memory = memory;
	}

}
