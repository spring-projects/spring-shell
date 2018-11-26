package com.example;

/**
 *
 * Program state for the illustrative Spring Shell Calculator application.
 *
 * @author Sualeh Fatehi <sualeh@hotmail.com>
 *
 **/
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
