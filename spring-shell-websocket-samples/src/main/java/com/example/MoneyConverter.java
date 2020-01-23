package com.example;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MoneyConverter implements Converter<String, Money> {

    @Override
    public Money convert(String source) {
        return new Money(Integer.parseInt(source.substring(0, source.indexOf("$", 0))));
    }
    
}