/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell.standard;

import static org.springframework.shell.Utils.unCamelify;

import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.ParameterDescription;
import org.springframework.shell.ParameterMissingResolutionException;
import org.springframework.shell.ParameterResolver;
import org.springframework.shell.UnfinishedParameterResolutionException;
import org.springframework.shell.Utils;
import org.springframework.shell.ValueResult;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;

import javax.validation.*;
import javax.validation.metadata.MethodDescriptor;
import javax.validation.metadata.ParameterDescriptor;

/**
 * Default ParameterResolver implementation that supports the following features:
 * <ul>
 * <li>named parameters (recognized because they start with some
 * {@link ShellMethod#prefix()})</li>
 * <li>implicit named parameters (from the actual method parameter name)</li>
 * <li>positional parameters (in order, for all parameter values that were not resolved
 * <i>via</i> named parameters)</li>
 * <li>default values (for all remaining parameters)</li>
 * </ul>
 *
 * <p>
 * Method arguments can consume several words of input at once (driven by
 * {@link ShellOption#arity()}, default 1). If several words are consumed, they will be
 * joined together as a comma separated value and passed to the {@link ConversionService}
 * (which will typically return a List or array).
 * </p>
 *
 * <p>
 * Boolean parameters are by default expected to have an arity of 0, allowing invocations
 * in the form {@code rm
 * --force --dir /foo}: the presence of {@code --force} passes {@code true} as a parameter
 * value, while its absence passes {@code false}. Both the default arity of 0 and the
 * default value of {@code false} can be overridden <i>via</i> {@link ShellOption} if
 * needed.
 * </p>
 * @author Eric Bottard
 * @author Florent Biville
 * @author Camilo Gonzalez
 */
public class StandardParameterResolver implements ParameterResolver {

	private final ConversionService conversionService;

	private Collection<ValueProvider> valueProviders = new HashSet<>();

	/**
	 * A cache from method+input to String representation of actual parameter values. Note
	 * that the converted result is not cached, to allow dynamic computation to happen at
	 * every invocation if needed (e.g. if a remote service is involved).
	 */
	private final Map<CacheKey, Map<Parameter, ParameterRawValue>> parameterCache = new ConcurrentReferenceHashMap<>();

	@Autowired
	public StandardParameterResolver(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Autowired(required = false)
	public void setValueProviders(Collection<ValueProvider> valueProviders) {
		this.valueProviders = valueProviders;
	}

	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired(required = false)
	public void setValidatorFactory(ValidatorFactory validatorFactory) {
		this.validator = validatorFactory.getValidator();
	}


	@Override
	public boolean supports(MethodParameter parameter) {
		boolean optOut = parameter.hasParameterAnnotation(ShellOption.class)
				&& parameter.getParameterAnnotation(ShellOption.class).optOut();
		return !optOut && parameter.getMethodAnnotation(ShellMethod.class) != null;
	}

	@Override
	public ValueResult resolve(MethodParameter methodParameter, List<String> wordsBuffer) {

		List<String> words = wordsBuffer.stream().filter(w -> w != null).collect(Collectors.toList());

		CacheKey cacheKey = new CacheKey(methodParameter.getMethod(), wordsBuffer);
		parameterCache.clear();
		Map<Parameter, ParameterRawValue> resolved = parameterCache.computeIfAbsent(cacheKey, (k) -> {

			Map<Parameter, ParameterRawValue> result = new HashMap<>();
			Map<String, String> namedParameters = new HashMap<>();

			// index of words that haven't yet been used to resolve parameter values
			List<Integer> unusedWords = new ArrayList<>();

			Set<String> possibleKeys = gatherAllPossibleKeys(methodParameter.getMethod());

			// First, resolve all parameters passed by-name
			for (int i = 0; i < words.size(); i++) {
				int from = i;
				String word = words.get(i);
				if (possibleKeys.contains(word)) {
					String key = word;
					Parameter parameter = lookupParameterForKey(methodParameter.getMethod(), key);
					int arity = getArity(parameter);

					if (i + 1 + arity > words.size()) {
						String input = words.subList(i, words.size()).stream().collect(Collectors.joining(" "));
						throw new UnfinishedParameterResolutionException(
								describe(Utils.createMethodParameter(parameter)).findFirst().get(), input);
					}
					Assert.isTrue(i + 1 + arity <= words.size(),
							String.format("Not enough input for parameter '%s'", word));
					String raw = words.subList(i + 1, i + 1 + arity).stream().collect(Collectors.joining(","));
					Assert.isTrue(!namedParameters.containsKey(key),
							String.format("Parameter for '%s' has already been specified", word));
					namedParameters.put(key, raw);
					if (arity == 0) {
						boolean defaultValue = booleanDefaultValue(parameter);
						// Boolean parameter has been specified. Use the opposite of the default value
						result.put(parameter,
								ParameterRawValue.explicit(String.valueOf(!defaultValue), key, from, from));
					}
					else {
						i += arity;
						result.put(parameter, ParameterRawValue.explicit(raw, key, from, i));
					}
				} // store for later processing of positional params
				else {
					unusedWords.add(i);
				}
			}

			// Now have a second pass over params and treat them as positional
			int offset = 0;
			Parameter[] parameters = methodParameter.getMethod().getParameters();
			for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
				Parameter parameter = parameters[i];
				// Compute the intersection between possible keys for the param and what we've already
				// seen for named params
				Collection<String> keys = getKeysForParameter(methodParameter.getMethod(), i)
						.collect(Collectors.toSet());
				Collection<String> copy = new HashSet<>(keys);
				copy.retainAll(namedParameters.keySet());
				if (copy.isEmpty()) { // Was not set via a key (including aliases), must be positional
					int arity = getArity(parameter);
					if (arity > 0 && (offset + arity) <= unusedWords.size()) {
						String raw = unusedWords.subList(offset, offset + arity).stream()
								.map(index -> words.get(index))
								.collect(Collectors.joining(","));
						int from = unusedWords.get(offset);
						int to = from + arity - 1;
						result.put(parameter, ParameterRawValue.explicit(raw, null, from, to));
						offset += arity;
					} // No more input. Try defaultValues
					else {
						Optional<String> defaultValue = defaultValueFor(parameter);
						defaultValue.ifPresent(
								value -> result.put(parameter, ParameterRawValue.implicit(value, null, null, null)));
					}
				}
				else if (copy.size() > 1) {
					throw new IllegalArgumentException(
							"Named parameter has been specified multiple times via " + quote(copy));
				}
			}

			Assert.isTrue(offset == unusedWords.size(),
					"Too many arguments: the following could not be mapped to parameters: "
							+ unusedWords.subList(offset, unusedWords.size()).stream()
									.map(index -> words.get(index)).collect(Collectors.joining(" ", "'", "'")));
			return result;
		});

		Parameter param = methodParameter.getMethod().getParameters()[methodParameter.getParameterIndex()];
		if (!resolved.containsKey(param)) {
			throw new ParameterMissingResolutionException(describe(methodParameter).findFirst().get());
		}
		ParameterRawValue parameterRawValue = resolved.get(param);
		Object value = convertRawValue(parameterRawValue, methodParameter);
		BitSet wordsUsed = getWordsUsed(parameterRawValue);
		BitSet wordsUsedForValue = getWordsUsedForValue(parameterRawValue);
		return new ValueResult(methodParameter, value, wordsUsed, wordsUsedForValue);
	}

	private BitSet getWordsUsed(ParameterRawValue parameterRawValue) {
		if (parameterRawValue.from != null) {
			BitSet wordsUsed = new BitSet();
			wordsUsed.set(parameterRawValue.from, parameterRawValue.to + 1);
			return wordsUsed;
		}
		return null;
	}

	private BitSet getWordsUsedForValue(ParameterRawValue parameterRawValue) {
		if (parameterRawValue.from != null) {
			BitSet wordsUsedForValue = new BitSet();
			wordsUsedForValue.set(parameterRawValue.from, parameterRawValue.to + 1);
			if (parameterRawValue.key != null) {
				wordsUsedForValue.clear(parameterRawValue.from);
			}
			return wordsUsedForValue;
		}
		return null;
	}

	private Object convertRawValue(ParameterRawValue parameterRawValue, MethodParameter methodParameter) {
		String s = parameterRawValue.value;
		if (ShellOption.NULL.equals(s)) {
			return null;
		}
		else {
			return conversionService.convert(s, TypeDescriptor.valueOf(String.class),
					new TypeDescriptor(methodParameter));
		}
	}

	private Set<String> gatherAllPossibleKeys(Method method) {
		return Arrays.stream(method.getParameters())
				.flatMap(this::getKeysForParameter)
				.collect(Collectors.toSet());
	}

	private String prefixForMethod(Executable method) {
		return method.getAnnotation(ShellMethod.class).prefix();
	}

	private Optional<String> defaultValueFor(Parameter parameter) {
		ShellOption option = parameter.getAnnotation(ShellOption.class);
		if (option != null && !ShellOption.NONE.equals(option.defaultValue())) {
			return Optional.of(option.defaultValue());
		}
		else if (getArity(parameter) == 0) {
			return Optional.of("false");
		}
		return Optional.empty();
	}

	private boolean booleanDefaultValue(Parameter parameter) {
		ShellOption option = parameter.getAnnotation(ShellOption.class);
		if (option != null && !ShellOption.NONE.equals(option.defaultValue())) {
			return Boolean.parseBoolean(option.defaultValue());
		}
		return false;
	}

	@Override
	public Stream<ParameterDescription> describe(MethodParameter parameter) {
		Parameter jlrParameter = parameter.getMethod().getParameters()[parameter.getParameterIndex()];
		int arity = getArity(jlrParameter);
		Class<?> type = parameter.getParameterType();
		ShellOption option = jlrParameter.getAnnotation(ShellOption.class);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arity; i++) {
			if (i > 0) {
				sb.append(" ");
			}
			sb.append(arity > 1 ? unCamelify(removeMultiplicityFromType(parameter).getSimpleName())
					: unCamelify(type.getSimpleName()));
		}
		ParameterDescription result = ParameterDescription.outOf(parameter);
		result.formal(sb.toString());
		if (option != null) {
			result.help(option.help());
			Optional<String> defaultValue = defaultValueFor(jlrParameter);
			if (defaultValue.isPresent()) {
				result.defaultValue(defaultValue.map(dv -> dv.equals(ShellOption.NULL) ? "<none>" : dv).get());
			}
		}
		result
				.keys(getKeysForParameter(parameter.getMethod(), parameter.getParameterIndex())
						.collect(Collectors.toList()))
				.mandatoryKey(false);

		MethodDescriptor constraintsForMethod = validator.getConstraintsForClass(parameter.getDeclaringClass())
				.getConstraintsForMethod(parameter.getMethod().getName(), parameter.getMethod().getParameterTypes());
		if (constraintsForMethod != null) {
			ParameterDescriptor constraintsDescriptor = constraintsForMethod
					.getParameterDescriptors().get(parameter.getParameterIndex());
			result.elementDescriptor(constraintsDescriptor);
		}

		return Stream.of(result);
	}

	@Override
	public List<CompletionProposal> complete(MethodParameter methodParameter, CompletionContext context) {
		boolean set;
		Exception unfinished = null;
		// First try to see if this parameter has been set, even to some unfinished value
		ParameterRawValue parameterRawValue = null;
		int arity = 1;
		try {
			resolve(methodParameter, context.getWords());
			CacheKey cacheKey = new CacheKey(methodParameter.getMethod(), context.getWords());
			Parameter parameter = methodParameter.getMethod().getParameters()[methodParameter.getParameterIndex()];
			arity = getArity(parameter);
			parameterRawValue = parameterCache.get(cacheKey).get(parameter);
			set = parameterRawValue.explicit;
		}
		catch (ParameterMissingResolutionException e) {
			set = false;
		}
		catch (UnfinishedParameterResolutionException e) {
			if (e.getParameterDescription().parameter().equals(methodParameter)) {
				unfinished = e;
				set = false;
			}
			else {
				return Collections.emptyList();
			}
		}
		catch (Exception e) {
			// Most likely what is already typed would fail resolution (eg type conversion failure)
			return argumentKeysThatStartWithContextPrefix(methodParameter, context);
		}

		// There are 4 possible cases:
		// 1) parameter not set at all
		// 2) parameter set via its key, not enough input to consume a value
		// 3) parameter set with multiple values, enough to cover arity. We're done
		// 4) parameter set, and some value bound. But maybe that value is just a prefix to what
		// the user actually wants
		// 4.1) or maybe that value was resolved by position, but is a prefix of an actual valid
		// key

		if (!set) {
			if (unfinished == null) {
				// case 1 above
				return argumentKeysThatStartWithContextPrefix(methodParameter, context);
			} // case 2
			else {
				return valueCompletions(methodParameter, context);
			}
		}
		else {
			List<CompletionProposal> result = new ArrayList<>();

			Object value = convertRawValue(parameterRawValue, methodParameter);
			if (value instanceof Collection && ((Collection) value).size() == arity
					|| (ObjectUtils.isArray(value) && Array.getLength(value) == arity)) {
				// We're done already
				return result;
			}
			if (!context.currentWord().equals("")) {
				// Case 4
				result.addAll(valueCompletions(methodParameter, context));
			}

			if (parameterRawValue.positional()) {
				// Case 4.1: There exists "--command foo" and user has typed "--comm" which (wrongly) got
				// resolved as a positional param
				result.addAll(argumentKeysThatStartWithContextPrefix(methodParameter, context));
			}
			return result;
		}
	}

	private List<CompletionProposal> valueCompletions(MethodParameter methodParameter,
			CompletionContext completionContext) {
		return valueProviders.stream()
				.filter(vp -> vp.supports(methodParameter, completionContext))
				.map(vp -> vp.complete(methodParameter, completionContext, null))
				.findFirst().orElseGet(() -> Collections.emptyList());
	}

	private List<CompletionProposal> argumentKeysThatStartWithContextPrefix(MethodParameter methodParameter,
			CompletionContext context) {
		String prefix = context.currentWordUpToCursor() != null ? context.currentWordUpToCursor() : "";
		return describe(methodParameter).flatMap(pd -> pd.keys().stream())
				.filter(k -> k.startsWith(prefix))
				.map(CompletionProposal::new)
				.collect(Collectors.toList());
	}

	/**
	 * In case of {@code foo[] or Collection<Foo>} and arity > 1, return the element type.
	 */
	private Class<?> removeMultiplicityFromType(MethodParameter parameter) {
		Class<?> parameterType = parameter.getParameterType();
		if (parameterType.isArray()) {
			return parameterType.getComponentType();
		}
		else if (Collection.class.isAssignableFrom(parameterType)) {
			return parameter.getNestedParameterType();
		}
		else {
			throw new RuntimeException("For " + parameter + " (with arity > 1) expected an array/collection type");
		}
	}

	/**
	 * Surrounds the parameter keys with quotes.
	 */
	private String quote(Collection<String> keys) {
		return keys.stream().collect(Collectors.joining(", ", "'", "'"));
	}

	/**
	 * Return the arity of a given parameter. The default arity is 1, except for booleans
	 * where arity is 0 (can be overridden back to 1 via an annotation)
	 */
	private int getArity(Parameter parameter) {
		ShellOption option = parameter.getAnnotation(ShellOption.class);
		int inferred = (parameter.getType() == boolean.class || parameter.getType() == Boolean.class) ? 0 : 1;
		return option != null && option.arity() != ShellOption.ARITY_USE_HEURISTICS ? option.arity() : inferred;
	}

	/**
	 * Return the key(s) for the i-th parameter of the command method, resolved either from
	 * the {@link ShellOption} annotation, or from the actual parameter name.
	 */
	private Stream<String> getKeysForParameter(Method method, int index) {
		Parameter p = method.getParameters()[index];
		return getKeysForParameter(p);
	}

	private Stream<String> getKeysForParameter(Parameter p) {
		Executable method = p.getDeclaringExecutable();
		String prefix = prefixForMethod(method);
		ShellOption option = p.getAnnotation(ShellOption.class);
		if (option != null && option.value().length > 0) {
			return Arrays.stream(option.value());
		}
		else {
			return Stream.of(prefix + Utils.unCamelify(Utils.createMethodParameter(p).getParameterName()));
		}
	}

	/**
	 * Return the method parameter that should be bound to the given key.
	 */
	private Parameter lookupParameterForKey(Method method, String key) {
		Parameter[] parameters = method.getParameters();
		for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
			Parameter p = parameters[i];
			if (getKeysForParameter(method, i).anyMatch(k -> k.equals(key))) {
				return p;
			}
		}
		throw new IllegalArgumentException(String.format("Could not look up parameter for '%s' in %s", key, method));
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
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			CacheKey cacheKey = (CacheKey) o;
			return Objects.equals(method, cacheKey.method) &&
					Objects.equals(words, cacheKey.words);
		}

		@Override
		public int hashCode() {
			return Objects.hash(method, words);
		}

		@Override
		public String toString() {
			return method.getName() + " " + words;
		}
	}

	private static class ParameterRawValue {

		private CompletionContext context;

		private Integer from;

		private Integer to;

		private Integer keyIndex;

		/**
		 * The raw String value that got bound to a parameter.
		 */
		private final String value;

		/**
		 * If false, the value resolved is the result of applying defaults.
		 */
		private final boolean explicit;

		/**
		 * The key that was used to set the parameter, or null if resolution happened by position.
		 */
		private final String key;

		private ParameterRawValue(String value, boolean explicit, String key, Integer from, Integer to) {
			this.value = value;
			this.explicit = explicit;
			this.key = key;
			this.from = from;
			this.to = to;
		}

		public static ParameterRawValue explicit(String value, String key, Integer from, Integer to) {
			return new ParameterRawValue(value, true, key, from, to);
		}

		public static ParameterRawValue implicit(String value, String key, Integer from, Integer to) {
			return new ParameterRawValue(value, false, key, from, to);
		}

		public boolean positional() {
			return key == null;
		}

		@Override
		public String toString() {
			return "ParameterRawValue{" +
					"value='" + value + '\'' +
					", explicit=" + explicit +
					", key='" + key + '\'' +
					", from=" + from +
					", to=" + to +
					'}';
		}
	}

}
