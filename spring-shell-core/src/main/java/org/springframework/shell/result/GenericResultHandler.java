package org.springframework.shell.result;

import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;

public interface GenericResultHandler {

    Set<Class<?>> getHandlerTypes();

    void handle(Object result, TypeDescriptor resultType);

    boolean matches(TypeDescriptor resultType);
}
