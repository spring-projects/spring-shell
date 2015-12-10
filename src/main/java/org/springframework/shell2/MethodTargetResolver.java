package org.springframework.shell2;

import java.util.Map;

import org.springframework.context.ApplicationContext;

/**
 * Created by ericbottard on 09/12/15.
 */
public interface MethodTargetResolver {

	public Map<String, MethodTarget> resolve(ApplicationContext context);

}
