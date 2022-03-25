package org.springframework.shell;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class MethodTargetFactory {
    /**
     * Construct a MethodTarget for the unique method named {@literal name} on the given object. Fails with an exception
     * in case of overloaded method.
     */
    public static MethodTarget createForUniqueMethodWithAvailabilityIndicator(String name, Object bean, Command.Help help, Supplier<Availability> availabilityIndicator) {
        Set<Method> found = new HashSet<>();
        ReflectionUtils.doWithMethods(bean.getClass(), found::add, m -> m.getName().equals(name));
        if (found.size() != 1) {
            throw new IllegalArgumentException(String.format("Could not find unique method named '%s' on object of class %s. Found %s",
                name, bean.getClass(), found));
        }
        return createForAllMethodsWithAvailabilityIndicator(found.iterator().next(), bean, help, availabilityIndicator);
    }

    /**
     * Construct a MethodTarget for the unique method named {@literal name} on the given object. Fails with an exception
     * in case of overloaded method.
     */
    public static MethodTarget createForUniqueMethodWithoutAvailabilityIndicator(String name, Object bean, Command.Help help) {
        return createForUniqueMethodWithAvailabilityIndicator(name, bean, help, null);
    }

    /**
     * Construct a MethodTarget for the unique method named {@literal name} on the given object. Fails with an exception
     * in case of overloaded method.
     */
    public static MethodTarget createForUniqueMethodWithoutHelpCommand(String name, Object bean, String description, String group) {
        return createForUniqueMethodWithoutAvailabilityIndicator(name, bean, new Command.Help(description, group));
    }

    public static MethodTarget createForAllMethodsWithAvailabilityIndicator (Method method, Object bean, Command.Help help, Supplier<Availability> availabilityIndicator) {
        return new MethodTarget(method, bean, help, availabilityIndicator, null);
    }

    public static MethodTarget createForAllMethodsWithHelpString (Method method, Object bean, String help) {
        return createForAllMethodsWithAvailabilityIndicator(method, bean, new Command.Help(help, null), null);
    }

    public static MethodTarget createForAllMethodsWithHelpStringAndAvailabilityIndicator (Method method, Object bean, String help, Supplier<Availability> availabilityIndicator) {
        return createForAllMethodsWithAvailabilityIndicator(method, bean, new Command.Help(help, null), availabilityIndicator);
    }
}
