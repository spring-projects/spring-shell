package com.example.test;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import com.example.CalculatorState;

@TestConfiguration
public class TestCalculatorStateConfig {

  @Bean("state")
  public CalculatorState calculatorState() {
    return new CalculatorState();
  }

}
