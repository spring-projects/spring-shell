package org.springframework.shell.converters;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

/**
 * A converter that knows how to use other converters to create arrays of supported types.
 *
 * @author Eric Bottard
 */
@Component
public class ArrayConverter implements Converter<Object[]>{

	private Set<Converter<?>> converters;

	@Autowired
	public void setConverters(Set<Converter<?>> converters) {
		this.converters = converters;
	}

	@Override
	public boolean supports(Class<?> type, String optionContext) {
		return findComponentConverter(type, optionContext) != null && !optionContext.contains("disable-array-converter");

	}

	private Converter<?> findComponentConverter(Class<?> targetType, String optionContext) {
		if (!targetType.isArray()) {
			return null;
		}
		Class<?> componentType = targetType.getComponentType();
		for (Converter<?> converter : converters) {
			if (converter.supports(componentType, optionContext)) {
				return converter;
			}
		}
		return null;
	}

	@Override
	public Object[] convertFromText(String value, Class<?> targetType, String optionContext) {
		Class<?> componentType = targetType.getComponentType();

		String splittingRegex = inferSplittingRegex(targetType, optionContext);
		String[] splits = value.split(splittingRegex);
		Object[] result = (Object[]) Array.newInstance(componentType, splits.length);
		Converter<?> converter = findComponentConverter(targetType, optionContext);

		for (int i = 0; i < splits.length; i++) {
			result[i] = converter.convertFromText(splits[i], componentType, optionContext);
		}
		return result;
	}

	/**
	 * Return a regex used to split the string representation of items.
	 * <p>The default delimiter is a comma, unless we're dealing with Files, in which case
	 * {@link java.io.File.pathSeparator} is used.</p>
	 * <p>Delimiters can be protected by an escape character, which is '\' by default.</p>
	 * <p>Command methods may override bot the delimiter and the escape through the {@code splittingRegex} option context
	 * string.</p>
	 */
	private String inferSplittingRegex(Class<?> targetType, String optionContext) {
		String regex = extract(optionContext, "splittingRegex");
		if (regex == null) {
			// Default for files is to use system separator with no way to escape
			if (File[].class.isAssignableFrom(targetType)) {
				regex = File.pathSeparator;
			} else {
				String delimiter = ",";
				String escape = "\\";
				regex = String.format("(?<!\\Q%s\\E)\\Q%s\\E", escape, delimiter);
			}
		}
		return regex;
	}

	@Override
	public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData, String optionContext, MethodTarget target) {
		Class<?> componentType = targetType.getComponentType();

		String splittingRegex = inferSplittingRegex(targetType, optionContext);
		String[] splits = existingData.split(splittingRegex);
		Converter<?> converter = findComponentConverter(targetType, optionContext);

		// Search for completions with the last part only, prefixing the results by everything that was
		// before the delimiter
		String last = splits[splits.length - 1];
		int end = existingData.lastIndexOf(last);
		String prefix = existingData.substring(0, end);
		List<Completion> ours = new ArrayList<Completion>();

		// Passing our method target below, as we can't do better. Obviously, method sig will be wrong
		boolean result = converter.getAllPossibleValues(ours, componentType, last, optionContext, target);
		for (Completion completion : ours) {
			completions.add(new Completion(prefix + completion.getValue(), completion.getValue(), null, 0));
		}

		return result;
	}

	private String extract(String optionContext, String key) {
		String[] splits = optionContext.split(" ");
		String prefix = key + "=";
		for (String split : splits) {
			if (split.startsWith(prefix)) {
				return split.substring(prefix.length());
			}
		}
		return null;
	}
}
