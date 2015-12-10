package org.springframework.shell2;

import java.lang.reflect.Method;

/**
 * Created by ericbottard on 27/11/15.
 */
public class MethodTarget {

	private final Method method;

	private final Object bean;

	private final String help;

	public MethodTarget(Method method, Object bean, String help) {
		this.method = method;
		this.bean = bean;
		this.help = help;
	}

	public Method getMethod() {
		return method;
	}

	public Object getBean() {
		return bean;
	}

	public String getHelp() {
		return help;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MethodTarget that = (MethodTarget) o;

		if (!method.equals(that.method)) return false;
		if (!bean.equals(that.bean)) return false;
		return help.equals(that.help);

	}

	@Override
	public int hashCode() {
		int result = method.hashCode();
		result = 31 * result + bean.hashCode();
		result = 31 * result + help.hashCode();
		return result;
	}
}
