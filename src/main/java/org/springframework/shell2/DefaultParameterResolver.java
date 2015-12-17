package org.springframework.shell2;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

/**
 * Default ParameterResolver implementation that supports the following features:<ul>
 *     <li>named parameters (recognized because they start with some {@link ShellMethod#prefix()}</li>
 *     <li>implicit named parameters (from the actual method parameter name)</li>
 *     <li>positional parameters (in order, for all parameter values that were not resolved via named parameters)</li>
 *     <li>default values (for all remaining parameters)</li>
 * </ul>
 *
 * <p>Method arguments can consume several words of input at once (driven by {@link ShellOption#arity()}, default 1).
 * If several words are consumed, they will be joined together as a comma separated value and passed to the {@link ConversionService}
 * (which will typically return a List or array).</p>
 *
 * <p>Boolean parameters are by default expected to have an arity of 0, allowing invocations in the form {@code rm --force --dir /foo}:
 * the presence of {@code --force} passes {@code true} as a parameter value, while its absence passes {@code false}. Both
 * the default arity of 0 and the default value of {@code false} can be overridden <i>via</i> {@link ShellOption}
 * if needed.</p>
 *
 * @author Eric Bottard
 * @author Florent Biville
 */
class DefaultParameterResolver implements ParameterResolver {

	private final ConversionService conversionService;

	/**
	 * A cache from method+input to String representation of actual parameter values.
	 * Note that the converted result is not cached, to allow dynamic computation to happen at every invocation
	 * if needed (e.g. if a remote service is involved).
	 */
	private final Map<CacheKey, Map<Parameter, String>> parameterCache = new ConcurrentReferenceHashMap<>();

	public DefaultParameterResolver(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public boolean supports(MethodParameter parameter) {
		return true;
	}

	@Override
	public Object resolve(MethodParameter methodParameter, List<String> words) {
		String prefix = methodParameter.getMethod().getAnnotation(ShellMethod.class).prefix();

		CacheKey cacheKey = new CacheKey(methodParameter.getMethod(), words);
		Map<Parameter, String> resolved = parameterCache.computeIfAbsent(cacheKey, (k) -> {

			Map<Parameter, String> result = new HashMap<>();
			Map<String, String> namedParameters = new HashMap<>();
			List<String> positionalValues = new ArrayList<>();

			// First, resolve all parameters passed by-name
			for (int i = 0; i < words.size(); i++) {
				String word = words.get(i);
				if (word.startsWith(prefix)) {
					String key = word.substring(prefix.length());
					Parameter parameter = lookupParameterForKey(methodParameter.getMethod(), key, prefix);
					int arity = getArity(parameter);

					String raw = words.subList(i + 1, i + 1 + arity).stream().collect(Collectors.joining(","));
					Assert.isTrue(!namedParameters.containsKey(key), String.format("Parameter for '%s' has already been specified", word));
					namedParameters.put(key, raw);
					result.put(parameter, raw);
					i += arity;
					if (arity == 0) {
						boolean defaultValue = booleanDefaultValue(parameter);
						// Boolean parameter has been specified. Use the opposite of the default value
						result.put(parameter, String.valueOf(!defaultValue));
					}
				} // store for later processing of positional params
				else {
					positionalValues.add(word);
				}
			}

			// Now have a second pass over params and treat them as positional
			int offset = 0;
			Parameter[] parameters = methodParameter.getMethod().getParameters();
			for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
				Parameter parameter = parameters[i];
				// Compute the intersection between possible keys for the param and what we've already seen for named params
				Collection<String> keys = getKeysForParameter(methodParameter.getMethod(), i);
				Collection<String> copy = new HashSet<>(keys);
				copy.retainAll(namedParameters.keySet());
				if (copy.isEmpty()) { // Was not set via a key (including aliases), must be positional
					int arity = getArity(parameter);
					if (offset < positionalValues.size() && (offset + arity) <= positionalValues.size()) {
						String raw = positionalValues.subList(offset, offset + arity).stream().collect(Collectors.joining(","));
						result.put(parameter, raw);
						offset += arity;
					} // No more input. Try defaultValues
					else {
						Optional<String> defaultValue = Optional.empty();
						ShellOption option = parameter.getAnnotation(ShellOption.class);
						if (option != null && !ShellOption.NULL.equals(option.defaultValue())) {
							defaultValue = Optional.of(option.defaultValue());
						}
						String value = defaultValue.orElseThrow(() -> new RuntimeException(String.format("Ran out of input for " + keys)));
						result.put(parameter, value);
					}
				}
				else if (copy.size() > 1) {
					throw new IllegalArgumentException("Named parameter has been specified multiple times via " + prefix(copy, prefix));
				}
			}

			Assert.isTrue(offset == positionalValues.size(), "Too many arguments: the following could not be mapped to parameters: "
					+ positionalValues.subList(offset, positionalValues.size()).stream().collect(Collectors.joining(" ", "'", "'")));
			return result;
		});

		String s = resolved.get(methodParameter.getMethod().getParameters()[methodParameter.getParameterIndex()]);
		return conversionService.convert(s, TypeDescriptor.valueOf(String.class), new TypeDescriptor(methodParameter));
	}

	/**
	 * Add the command prefix back to the list of keys that was used to invoke the method.
	 */
	private String prefix(Collection<String> keys, String prefix) {
		return keys.stream().map(k -> prefix + k).collect(Collectors.joining(", ", "'", "'"));
	}

	private boolean booleanDefaultValue(Parameter parameter) {
		ShellOption option = parameter.getAnnotation(ShellOption.class);
		if (option != null && !ShellOption.NULL.equals(option.defaultValue())) {
			return Boolean.parseBoolean(option.defaultValue());
		}
		return false;
	}

	/**
	 * Return the arity of a given parameter. The default arity is 1, except for
	 * booleans where arity is 0 (can be overridden back to 1 via an annotation)
	 */
	private int getArity(Parameter parameter) {
		ShellOption option = parameter.getAnnotation(ShellOption.class);
		int inferred = (parameter.getType() == boolean.class || parameter.getType() == Boolean.class) ? 0 : 1;
		return option != null ? option.arity() : inferred;
	}

	/**
	 * Return the key(s) the i-th parameter of the command method, resolved either from the {@link ShellOption}
	 * annotation,
	 * or from the actual parameter name.
	 * @throws IllegalArgumentException if parameter names could not be extracted
	 */
	private Collection<String> getKeysForParameter(Method method, int index) {
		Parameter parameter = method.getParameters()[index];
		ShellOption option = parameter.getAnnotation(ShellOption.class);
		if (option != null && option.value().length > 0) {
			return Arrays.asList(option.value());
		}
		else {
			MethodParameter methodParameter = new MethodParameter(method, index);
			methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
			String parameterName = methodParameter.getParameterName();
			Assert.notNull(parameterName, String.format(
					"Could not discover parameter name at index %d for %s, and option key(s) were not specified via %s annotation",
					index, method, ShellOption.class.getSimpleName()));
			return Collections.singleton(parameterName);
		}
	}

	/**
	 * Return the method parameter that should be bound to the given key.
	 */
	private Parameter lookupParameterForKey(Method method, String key, String prefix) {
		Parameter[] parameters = method.getParameters();
		for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
			Parameter p = parameters[i];
			if (getKeysForParameter(method, i).contains(key)) {
				return p;
			}
		}
		throw new IllegalArgumentException(String.format("Could not look up parameter for '%s%s' in %s", prefix, key, method));
	}

	private static class CacheKey {

		private final Method method;

		private final List<String> words;

		private CacheKey(Method method, List<String> words) {
			this.method = method;
			this.words = words;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			CacheKey cacheKey = (CacheKey) o;
			return Objects.equals(method, cacheKey.method) &&
					Objects.equals(words, cacheKey.words);
		}

		@Override
		public int hashCode() {
			return Objects.hash(method, words);
		}
	}
}
