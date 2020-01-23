package org.springframework.shell.websocket;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.MethodParameter;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.ParameterDescription;
import org.springframework.shell.ParameterResolver;
import org.springframework.shell.ValueResult;
import org.springframework.stereotype.Component;

public class PrincipalResolver implements ParameterResolver {

    @Override
    public boolean supports(MethodParameter parameter) {
        return parameter.getParameterType().equals(Principal.class);
    }

    @Override
    public ValueResult resolve(MethodParameter methodParameter, List<String> words) {
        return new ValueResult(methodParameter, SecurityContextHolder.getContext().getAuthentication());
    }

    @Override
    public Stream<ParameterDescription> describe(MethodParameter parameter) {
        return Stream.of(ParameterDescription.outOf(parameter));
    }

    @Override
    public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext context) {
        return Collections.singletonList(new CompletionProposal("").dontQuote(true));
    }

    
}