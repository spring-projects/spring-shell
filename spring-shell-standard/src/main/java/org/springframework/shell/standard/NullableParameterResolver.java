package org.springframework.shell.standard;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.ParameterDescription;
import org.springframework.shell.ParameterResolver;
import org.springframework.shell.ValueResult;
import org.springframework.stereotype.Component;

@Component
public class NullableParameterResolver implements ParameterResolver {

    @Override
    public boolean supports(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Nullable.class);
    }

    @Override
    public ValueResult resolve(MethodParameter methodParameter, List<String> words) {
        return new ValueResult(methodParameter, null);
    }

    @Override
    public Stream<ParameterDescription> describe(MethodParameter parameter) {
        return Stream.of(ParameterDescription.outOf(parameter));
    }

    @Override
    public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext context) {
        return Collections.emptyList();
    }

}