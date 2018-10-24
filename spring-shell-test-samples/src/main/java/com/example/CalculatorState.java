package com.example;


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
