package org.springframework.shell2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * Created by ericbottard on 09/12/15.
 */
@Component
public class DefaultMethodTargetResolver implements MethodTargetResolver {

	@Override
	public Map<String, MethodTarget> resolve(ApplicationContext applicationContext) {
		Map<String,MethodTarget> methodTargets = new HashMap<>();
		Map<String, Object> commandBeans = applicationContext.getBeansWithAnnotation(ShellComponent.class);
		for (Object bean : commandBeans.values()) {
			Class<?> clazz = bean.getClass();
			ReflectionUtils.doWithMethods(clazz, method -> {
				ShellMehtod shellMapping = method.getAnnotation(ShellMehtod.class);
				String[] keys = shellMapping.value();
				if (keys.length == 1 && "".equals(keys[0])) {
					keys[0] = method.getName();
				}
				for (String key : keys) {
					methodTargets.put(key, new MethodTarget(method, bean, shellMapping.help()));
				}
			}, method -> method.getAnnotation(ShellMehtod.class) != null);
		}
		return methodTargets;
	}
}
