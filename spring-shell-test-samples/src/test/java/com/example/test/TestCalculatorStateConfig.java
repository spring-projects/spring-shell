package com.example.test;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import com.example.CalculatorState;

/**
*
* Spring configuration with beans for setting up tests.
*
* @author Sualeh Fatehi <sualeh@hotmail.com>
*
**/
@TestConfiguration
public class TestCalculatorStateConfig {

  @Bean("state")
  public CalculatorState calculatorState() {
    return new CalculatorState();
  }

}
