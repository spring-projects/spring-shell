package org.springframework.shell2.legacy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell2.MethodTarget;
import org.springframework.shell2.MethodTargetResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * Created by ericbottard on 09/12/15.
 */
@Component
public class LegacyMethodTargetResolver implements MethodTargetResolver {

	@Override
	public Map<String, MethodTarget> resolve(ApplicationContext context) {
		Map<String, MethodTarget> methodTargets = new HashMap<>();
		Map<String, CommandMarker> beansOfType = context.getBeansOfType(CommandMarker.class);
		for (Object bean : beansOfType.values()) {
			Class<?> clazz = bean.getClass();
			ReflectionUtils.doWithMethods(clazz, method -> {
				CliCommand shellMapping = method.getAnnotation(CliCommand.class);
				for (String key : shellMapping.value()) {
					methodTargets.put(key, new MethodTarget(method, bean, shellMapping.help()));
				}
			}, method -> method.getAnnotation(CliCommand.class) != null);
		}
		return methodTargets;
	}
}
