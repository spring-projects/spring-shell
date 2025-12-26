package org.springframework.shell.core.command.completion;

import java.util.Arrays;
import java.util.List;

/**
 * A completion provider that knows how to complete values from enumerations.
 * <p>
 * This provider can also add a prefix before each enum constant, useful when enum values
 * are expected to be provided as key=value pairs for options.
 *
 * @author Eric Bottard
 * @author Mahmoud Ben Hassine (adapted from EnumValueProvider in v3)
 * @since 4.0.0
 */
public class EnumCompletionProvider implements CompletionProvider {

	private final Class<?> enumType;

	private String prefix = "";

	/**
	 * Create a new {@link EnumCompletionProvider} for the given enum type.
	 * @param enumType the enum type
	 */
	public EnumCompletionProvider(Class<?> enumType) {
		this.enumType = enumType;
	}

	/**
	 * Create a new {@link EnumCompletionProvider} for the given enum type with a prefix.
	 * @param enumType the enum type
	 * @param prefix the prefix to be added before each enum constant
	 */
	public EnumCompletionProvider(Class<?> enumType, String prefix) {
		this.enumType = enumType;
		this.prefix = prefix;
	}

	@Override
	public List<CompletionProposal> apply(CompletionContext completionContext) {
		Object[] enumConstants = enumType.getEnumConstants();
		return Arrays.stream(enumConstants)
			.map(enumConstant -> prefix.isEmpty() ? enumConstant.toString() : prefix + "=" + enumConstant)
			.map(CompletionProposal::new)
			.toList();
	}

}
