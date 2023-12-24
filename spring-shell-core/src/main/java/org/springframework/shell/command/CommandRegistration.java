/*
 * Copyright 2022-2023 the original author or authors.
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
package org.springframework.shell.command;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.shell.Availability;
import org.springframework.shell.completion.CompletionResolver;
import org.springframework.shell.context.InteractionMode;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Interface defining a command registration endpoint.
 *
 * @author Janne Valkealahti
 */
public interface CommandRegistration {

	/**
	 * Gets a command for this registration.
	 *
	 * @return command
	 */
	String getCommand();

	/**
	 * Gets an {@link InteractionMode}.
	 *
	 * @return the interaction mode
	 */
	InteractionMode getInteractionMode();

	/**
	 * Get group for a command.
	 *
	 * @return the group
	 */
	String getGroup();

	/**
	 * Returns if command is hidden.
	 *
	 * @return true if command is hidden
	 */
	boolean isHidden();

	/**
	 * Get description for a command.
	 *
	 * @return the description
	 */
	String getDescription();

	/**
	 * Get {@link Availability} for a command
	 *
	 * @return the availability
	 */
	Availability getAvailability();

	/**
	 * Gets target info.
	 *
	 * @return the target info
	 */
	TargetInfo getTarget();

	/**
	 * Gets an options.
	 *
	 * @return the options
	 */
	List<CommandOption> getOptions();

	/**
	 * Gets an aliases.
	 *
	 * @return the aliases
	 */
	List<CommandAlias> getAliases();

	/**
	 * Gets an exit code.
	 *
	 * @return the exit code
	 */
	CommandExitCode getExitCode();

	/**
	 * Gets an exception resolvers.
	 *
	 * @return the exception resolvers
	 */
	List<CommandExceptionResolver> getExceptionResolvers();

	/**
	 * Gets a help option info.
	 *
	 * @return the help option info
	 */
	HelpOptionInfo getHelpOption();

	/**
	 * Gets a new instance of a {@link Builder}.
	 *
	 * @return a new builder instance
	 */
	public static Builder builder() {
		return new DefaultBuilder();
	}

	/**
	 * Interface used to supply instance of a {@link Builder}. Meant to be a single
	 * point access to centrally configured builder in an application context.
	 */
	@FunctionalInterface
	public interface BuilderSupplier extends Supplier<Builder> {
	}

	/**
	 * Interface used to modify option long name. Usual use case is i.e. making
	 * conversion from a {@code camelCase} to {@code snake-case}.
	 */
	@FunctionalInterface
	public interface OptionNameModifier extends Function<String, String> {
	}

	/**
	 * Spec defining an option.
	 */
	public interface OptionSpec {

		/**
		 * Define long option names.
		 *
		 * @param names the long option names
		 * @return option spec for chaining
		 */
		OptionSpec longNames(String... names);

		/**
		 * Define short option names.
		 *
		 * @param names the long option names
		 * @return option spec for chaining
		 */
		OptionSpec shortNames(Character... names);

		/**
		 * Define a type for an option. This method is a shortcut for
		 * {@link #type(ResolvableType)} which is a preferred way to
		 * define type with generics. Will override one from
		 * {@link #type(ResolvableType)}.
		 *
		 * @param type the type
		 * @return option spec for chaining
		 * @see #type(ResolvableType)
		 */
		OptionSpec type(Type type);

		/**
		 * Define a {@link ResolvableType} for an option. This method is
		 * a preferred way to define type with generics. Will override one
		 * from {@link #type(Type)}.
		 *
		 * @param type the resolvable type
		 * @return option spec for chaining
		 */
		OptionSpec type(ResolvableType type);

		/**
		 * Define a {@code description} for an option.
		 *
		 * @param description the option description
		 * @return option spec for chaining
		 */
		OptionSpec description(String description);

		/**
		 * Define if option is required.
		 *
		 * @param required the required flag
		 * @return option spec for chaining
		 */
		OptionSpec required(boolean required);

		/**
		 * Define option to be required. Syntatic sugar calling
		 * {@link #required(boolean)} with {@code true}.
		 *
		 * @return option spec for chaining
		 */
		OptionSpec required();

		/**
		 * Define a {@code defaultValue} for an option.
		 *
		 * @param defaultValue the option default value
		 * @return option spec for chaining
		 */
		OptionSpec defaultValue(String defaultValue);

		/**
		 * Define an optional hint for possible positional mapping.
		 *
		 * @param position the position
		 * @return option spec for chaining
		 */
		OptionSpec position(Integer position);

		/**
		 * Define an {@code arity} for an option.
		 *
		 * @param min the min arity
		 * @param max the max arity
		 * @return option spec for chaining
		 */
		OptionSpec arity(int min, int max);

		/**
		 * Define an {@code arity} for an option.
		 *
		 * @param arity the arity
		 * @return option spec for chaining
		 */
		OptionSpec arity(OptionArity arity);

		/**
		 * Define a {@code label} for an option.
		 *
		 * @param label the label
		 * @return option spec for chaining
		 */
		OptionSpec label(String label);

		/**
		 * Define a {@code completion function} for an option.
		 *
		 * @param completion the completion function
		 * @return option spec for chaining
		 */
		OptionSpec completion(CompletionResolver completion);

		/**
		 * Define an option name modifier.
		 *
		 * @param modifier the option name modifier function
		 * @return option spec for chaining
		 */
		OptionSpec nameModifier(Function<String, String> modifier);

		/**
		 * Return a builder for chaining.
		 *
		 * @return a builder for chaining
		 */
		Builder and();
	}

	/**
	 * Enumeration of option arity values.
	 */
	public enum OptionArity {

		/**
		 * Used to indicate that arity is not set. Exists as a workaround for a case
		 * where it's not possible to use null values.
		 */
		NONE,

		/**
		 * Define min(0), max(0).
		 */
		ZERO,

		/**
		 * Define min(0), max(1).
		 */
		ZERO_OR_ONE,

		/**
		 * Define min(1), max(1).
		 */
		EXACTLY_ONE,

		/**
		 * Define min(0), max(MAXINTEGER).
		 */
		ZERO_OR_MORE,

		/**
		 * Define min(1), max(MAXINTEGER).
		 */
		ONE_OR_MORE;
	}

	/**
	 * Encapsulates info for {@link TargetSpec}.
	 */
	public interface TargetInfo {

		/**
		 * Get target type
		 *
		 * @return the target type
		 */
		TargetType getTargetType();

		/**
		 * Get the bean.
		 *
		 * @return the bean
		 */
		Object getBean();

		/**
		 * Get the bean method
		 *
		 * @return the bean method
		 */
		Method getMethod();

		/**
		 * Get the function
		 *
		 * @return the function
		 */
		Function<CommandContext, ?> getFunction();

		/**
		 * Get the consumer
		 *
		 * @return the consumer
		 */
		Consumer<CommandContext> getConsumer();

		static TargetInfo of(Object bean, Method method) {
			return new DefaultTargetInfo(TargetType.METHOD, bean, method, null, null);
		}

		static TargetInfo of(Function<CommandContext, ?> function) {
			return new DefaultTargetInfo(TargetType.FUNCTION, null, null, function, null);
		}

		static TargetInfo of(Consumer<CommandContext> consumer) {
			return new DefaultTargetInfo(TargetType.CONSUMER, null, null, null, consumer);
		}

		enum TargetType {
			METHOD, FUNCTION, CONSUMER;
		}

		static class DefaultTargetInfo implements TargetInfo {

			private final TargetType targetType;
			private final Object bean;
			private final Method method;
			private final Function<CommandContext, ?> function;
			private final Consumer<CommandContext> consumer;

			public DefaultTargetInfo(TargetType targetType, Object bean, Method method,
					Function<CommandContext, ?> function, Consumer<CommandContext> consumer) {
				this.targetType = targetType;
				this.bean = bean;
				this.method = method;
				this.function = function;
				this.consumer = consumer;
			}

			@Override
			public TargetType getTargetType() {
				return targetType;
			}

			@Override
			public Object getBean() {
				return bean;
			}

			@Override
			public Method getMethod() {
				return method;
			}

			@Override
			public Function<CommandContext, ?> getFunction() {
				return function;
			}

			@Override
			public Consumer<CommandContext> getConsumer() {
				return consumer;
			}
		}
	}

	/**
	 * Spec defining a target.
	 */
	public interface TargetSpec {

		/**
		 * Register a method target.
		 *
		 * @param bean the bean
		 * @param method the method
		 * @param paramTypes the parameter types
		 * @return a target spec for chaining
		 */
		TargetSpec method(Object bean, String method, @Nullable Class<?>... paramTypes);

		/**
		 * Register a method target.
		 *
		 * @param bean the bean
		 * @param method the method
		 * @return a target spec for chaining
		 */
		TargetSpec method(Object bean, Method method);

		/**
		 * Register a function target.
		 *
		 * @param function the function to register
		 * @return a target spec for chaining
		 */
		TargetSpec function(Function<CommandContext, ?> function);

		/**
		 * Register a consumer target.
		 *
		 * @param consumer the consumer to register
		 * @return a target spec for chaining
		 */
		TargetSpec consumer(Consumer<CommandContext> consumer);

		/**
		 * Return a builder for chaining.
		 *
		 * @return a builder for chaining
		 */
		Builder and();
	}

	/**
	 * Spec defining an alias.
	 */
	public interface AliasSpec {

		/**
		 * Define commands for an alias.
		 *
		 * @param commands the commands
		 * @return a target spec for chaining
		 */
		AliasSpec command(String... commands);

		/**
		 * Define group for an alias.
		 *
		 * @param group the group
		 * @return a target spec for chaining
		 */
		AliasSpec group(String group);

		/**
		 * Return a builder for chaining.
		 *
		 * @return a builder for chaining
		 */
		Builder and();
	}

	/**
	 * Spec defining an exit code.
	 */
	public interface ExitCodeSpec {

		/**
		 * Define mapping from exception to code.
		 *
		 * @param e the exception
		 * @param code the exit code
		 * @return a target spec for chaining
		 */
		ExitCodeSpec map(Class<? extends Throwable> e, int code);

		/**
		 *
		 * @param function
		 * @return
		 */
		ExitCodeSpec map(Function<Throwable, Integer> function);

		/**
		 * Return a builder for chaining.
		 *
		 * @return a builder for chaining
		 */
		Builder and();
	}

	/**
	 * Spec defining an error handling.
	 */
	public interface ErrorHandlingSpec {

		/**
		 * Add {@link CommandExceptionResolver}.
		 *
		 * @param resolver the resolver
		 * @return a error handling for chaining
		 */
		ErrorHandlingSpec resolver(CommandExceptionResolver resolver);

		/**
		 * Return a builder for chaining.
		 *
		 * @return a builder for chaining
		 */
		Builder and();
	}

	public interface HelpOptionInfo {

		/**
		 * Gets whether help options are enabled.
		 *
		 * @return whether help options are enabled
		 */
		boolean isEnabled();

		/**
		 * Gets long names options for help.
		 *
		 * @return long names options for help
		 */
		String[] getLongNames();

		/**
		 * Gets short names options for help.
		 *
		 * @return short names options for help
		 */
		Character[] getShortNames();

		/**
		 * Gets command for help.
		 *
		 * @return command for help
		 */
		String getCommand();

		static HelpOptionInfo of() {
			return of(false, null, null, null);
		}

		static HelpOptionInfo of(boolean enabled, String[] longNames, Character[] shortNames, String command) {
			return new DefaultHelpOptionInfo(enabled, longNames, shortNames, command);
		}

		static class DefaultHelpOptionInfo implements HelpOptionInfo {

			private final String command;
			private final String[] longNames;
			private final Character[] shortNames;
			private final boolean enabled;

			public DefaultHelpOptionInfo(boolean enabled, String[] longNames, Character[] shortNames, String command) {
				this.command = command;
				this.longNames = longNames;
				this.shortNames = shortNames;
				this.enabled = enabled;
			}

			@Override
			public boolean isEnabled() {
				return enabled;
			}

			@Override
			public String[] getLongNames() {
				return longNames;
			}

			@Override
			public Character[] getShortNames() {
				return shortNames;
			}

			@Override
			public String getCommand() {
				return command;
			}
		}
	}

	/**
	 * Spec defining help options.
	 */
	public interface HelpOptionsSpec {

		/**
		 * Whether help options are enabled.
		 *
		 * @param enabled the enabled flag
		 * @return a help option for chaining
		 */
		HelpOptionsSpec enabled(boolean enabled);

		/**
		 * Sets long names options for help.
		 *
		 * @param longNames the long names
		 * @return a help option for chaining
		 */
		HelpOptionsSpec longNames(String... longNames);

		/**
		 * Sets short names options for help.
		 *
		 * @param shortNames the short names
		 * @return a help option for chaining
		 */
		HelpOptionsSpec shortNames(Character... shortNames);

		/**
		 * Sets command used for help.
		 *
		 * @param command the command
		 * @return a help option for chaining
		 */
		HelpOptionsSpec command(String command);

		/**
		 * Return a builder for chaining.
		 *
		 * @return a builder for chaining
		 */
		Builder and();
	}

	/**
	 * Builder interface for {@link CommandRegistration}.
	 */
	public interface Builder {

		/**
		 * Define commands this registration uses. Essentially defines a full set of
		 * main and sub commands. It doesn't matter if full command is defined in one
		 * string or multiple strings as "words" are splitted and trimmed with
		 * whitespaces. You will get result of {@code command subcommand1 subcommand2, ...}.
		 *
		 * @param commands the commands
		 * @return builder for chaining
		 */
		Builder command(String... commands);

		/**
		 * Define {@link InteractionMode} for a command.
		 *
		 * @param mode the interaction mode
		 * @return builder for chaining
		 */
		Builder interactionMode(InteractionMode mode);

		/**
		 * Define a description text for a command.
		 *
		 * @param description the description text
		 * @return builder for chaining
		 */
		Builder description(String description);

		/**
		 * Define an {@link Availability} suppliear for a command.
		 *
		 * @param availability the availability
		 * @return builder for chaining
		 */
		Builder availability(Supplier<Availability> availability);

		/**
		 * Define a group for a command.
		 *
		 * @param group the group
		 * @return builder for chaining
		 */
		Builder group(String group);

		/**
		 * Define a command to be hidden.
		 *
		 * @return builder for chaining
		 * @see #hidden(boolean)
		 */
		Builder hidden();

		/**
		 * Define a command to be hidden by a given flag.
		 *
		 * @param hidden the hidden flag
		 * @return builder for chaining
		 */
		Builder hidden(boolean hidden);

		/**
		 * Provides a global option name modifier. Will be used with all options to
		 * modify long names. Usual use case is to enforce naming convention i.e. to
		 * have {@code snake-case} for all names.
		 *
		 * @param modifier to modifier to change option name
		 * @return builder for chaining
		 */
		Builder defaultOptionNameModifier(Function<String, String> modifier);

		/**
		 * Define an option what this command should user for. Can be used multiple
		 * times.
		 *
		 * @return option spec for chaining
		 */
		OptionSpec withOption();

		/**
		 * Define a target what this command should execute
		 *
		 * @return target spec for chaining
		 */
		TargetSpec withTarget();

		/**
		 * Define an alias what this command should execute
		 *
		 * @return alias spec for chaining
		 */
		AliasSpec withAlias();

		/**
		 * Define an exit code what this command should execute
		 *
		 * @return exit code spec for chaining
		 */
		ExitCodeSpec withExitCode();

		/**
		 * Define an error handling what this command should use
		 *
		 * @return error handling spec for chaining
		 */
		ErrorHandlingSpec withErrorHandling();

		/**
		 * Define help options what this command should use.
		 *
		 * @return help options spec for chaining
		 */
		HelpOptionsSpec withHelpOptions();

		/**
		 * Builds a {@link CommandRegistration}.
		 *
		 * @return a command registration
		 */
		CommandRegistration build();
	}

	static class DefaultOptionSpec implements OptionSpec {

		private BaseBuilder builder;
		private String[] longNames;
		private Character[] shortNames;
		private ResolvableType type;
		private String description;
		private boolean required;
		private String defaultValue;
		private Integer position;
		private Integer arityMin;
		private Integer arityMax;
		private String label;
		private CompletionResolver completion;
		private Function<String, String> optionNameModifier;

		DefaultOptionSpec(BaseBuilder builder) {
			this.builder = builder;
		}

		@Override
		public OptionSpec longNames(String... names) {
			this.longNames = names;
			return this;
		}

		@Override
		public OptionSpec shortNames(Character... names) {
			this.shortNames = names;
			return this;
		}

		@Override
		public OptionSpec type(Type type) {
			this.type = ResolvableType.forType(type);
			return this;
		}

		@Override
		public OptionSpec type(ResolvableType type) {
			this.type = type;
			return this;
		}

		@Override
		public OptionSpec description(String description) {
			this.description = description;
			return this;
		}

		@Override
		public OptionSpec required(boolean required) {
			this.required = required;
			return this;
		}

		@Override
		public OptionSpec required() {
			return required(true);
		}

		@Override
		public OptionSpec defaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		@Override
		public OptionSpec position(Integer position) {
			this.position = position;
			return this;
		}

		@Override
		public OptionSpec arity(int min, int max) {
			Assert.isTrue(min > -1, "arity min must be 0 or more");
			Assert.isTrue(max >= min, "arity max must be equal more than min");
			this.arityMin = min;
			this.arityMax = max;
			return this;
		}

		@Override
		public OptionSpec arity(OptionArity arity) {
			switch (arity) {
				case NONE:
					this.arityMin = null;
					this.arityMax = null;
					break;
				case ZERO:
					this.arityMin = 0;
					this.arityMax = 0;
					break;
				case ZERO_OR_ONE:
					this.arityMin = 0;
					this.arityMax = 1;
					break;
				case EXACTLY_ONE:
					this.arityMin = 1;
					this.arityMax = 1;
					break;
				case ZERO_OR_MORE:
					this.arityMin = 0;
					this.arityMax = Integer.MAX_VALUE;
					break;
				case ONE_OR_MORE:
					this.arityMin = 1;
					this.arityMax = Integer.MAX_VALUE;
					break;
				default:
					this.arityMin = null;
					this.arityMax = null;
					break;
			}
			return this;
		}

		@Override
		public OptionSpec label(String label) {
			this.label = label;
			return this;
		}

		@Override
		public OptionSpec completion(CompletionResolver completion) {
			this.completion = completion;
			return this;
		}

		@Override
		public OptionSpec nameModifier(Function<String, String> modifier) {
			this.optionNameModifier = modifier;
			return this;
		}

		@Override
		public Builder and() {
			return builder;
		}

		public String[] getLongNames() {
			return longNames;
		}

		public Character[] getShortNames() {
			return shortNames;
		}

		public ResolvableType getType() {
			return type;
		}

		public String getDescription() {
			return description;
		}

		public boolean isRequired() {
			return required;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public Integer getPosition() {
			return position;
		}

		public Integer getArityMin() {
			return arityMin;
		}

		public Integer getArityMax() {
			return arityMax;
		}

		public String getLabel() {
			return label;
		}

		public CompletionResolver getCompletion() {
			return completion;
		}

		@Nullable
		public Function<String, String> getOptionNameModifier() {
			if (optionNameModifier != null) {
				return optionNameModifier;
			}
			if (builder.defaultOptionNameModifier != null) {
				return builder.defaultOptionNameModifier;
			}
			return null;
		}
	}

	static class DefaultTargetSpec implements TargetSpec {

		private BaseBuilder builder;
		private Object bean;
		private Method method;
		private Function<CommandContext, ?> function;
		private Consumer<CommandContext> consumer;

		DefaultTargetSpec(BaseBuilder builder) {
			this.builder = builder;
		}

		@Override
		public TargetSpec method(Object bean, Method method) {
			this.bean = bean;
			this.method = method;
			return this;
		}

		@Override
		public TargetSpec method(Object bean, String method, Class<?>... paramTypes) {
			this.bean = bean;
			this.method = ReflectionUtils.findMethod(bean.getClass(), method,
					ObjectUtils.isEmpty(paramTypes) ? null : paramTypes);
			return this;
		}

		@Override
		public TargetSpec function(Function<CommandContext, ?> function) {
			this.function = function;
			return this;
		}

		@Override
		public TargetSpec consumer(Consumer<CommandContext> consumer) {
			this.consumer = consumer;
			return this;
		}

		@Override
		public Builder and() {
			return builder;
		}
	}

	static class DefaultAliasSpec implements AliasSpec {

		private BaseBuilder builder;
		private String[] commands;
		private String group;

		DefaultAliasSpec(BaseBuilder builder) {
			this.builder = builder;
		}

		@Override
		public AliasSpec command(String... commands) {
			Assert.notNull(commands, "commands must be set");
			this.commands = Arrays.asList(commands).stream()
				.flatMap(c -> Stream.of(c.split(" ")))
				.filter(c -> StringUtils.hasText(c))
				.map(c -> c.trim())
				.collect(Collectors.toList())
				.toArray(new String[0]);
			return this;
		}

		@Override
		public AliasSpec group(String group) {
			this.group = group;
			return this;
		}

		@Override
		public Builder and() {
			return builder;
		}
	}

	static class DefaultExitCodeSpec implements ExitCodeSpec {

		private BaseBuilder builder;
		private final List<Function<Throwable, Integer>> functions = new ArrayList<>();

		DefaultExitCodeSpec(BaseBuilder builder) {
			this.builder = builder;
		}

		@Override
		public ExitCodeSpec map(Class<? extends Throwable> e, int code) {
			Function<Throwable, Integer> f = t -> {
				if (t != null && ObjectUtils.nullSafeEquals(t.getClass(), e)) {
					return code;
				}
				return 0;
			};
			this.functions.add(f);
			return this;
		}

		@Override
		public ExitCodeSpec map(Function<Throwable, Integer> function) {
			this.functions.add(function);
			return this;
		}

		@Override
		public Builder and() {
			return builder;
		}
	}

	static class DefaultErrorHandlingSpec implements ErrorHandlingSpec {

		private BaseBuilder builder;
		private final List<CommandExceptionResolver> resolvers = new ArrayList<>();

		DefaultErrorHandlingSpec(BaseBuilder builder) {
			this.builder = builder;
		}

		@Override
		public ErrorHandlingSpec resolver(CommandExceptionResolver resolver) {
			this.resolvers.add(resolver);
			return this;
		}

		@Override
		public Builder and() {
			return builder;
		}
	}

	static class DefaultHelpOptionsSpec implements HelpOptionsSpec {

		private BaseBuilder builder;
		private String command;
		private String[] longNames;
		private Character[] shortNames;
		private boolean enabled = true;

		DefaultHelpOptionsSpec(BaseBuilder builder) {
			this.builder = builder;
		}

		DefaultHelpOptionsSpec(BaseBuilder otherBuilder, DefaultHelpOptionsSpec otherSpec) {
			this.builder = otherBuilder;
			this.builder.helpOptionsSpec = this;
			this.command = otherSpec.command;
			this.longNames = otherSpec.longNames.clone();
			this.shortNames = otherSpec.shortNames.clone();
			this.enabled = otherSpec.enabled;
		}

		@Override
		public HelpOptionsSpec command(String command) {
			this.command = command;
			return this;
		}

		@Override
		public HelpOptionsSpec longNames(String... longNames) {
				this.longNames = longNames;
			return this;
		}

		@Override
		public HelpOptionsSpec shortNames(Character... shortNames) {
			this.shortNames = shortNames;
			return this;
		}

		@Override
		public HelpOptionsSpec enabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		@Override
		public Builder and() {
			return builder;
		}
	}

	static class DefaultCommandRegistration implements CommandRegistration {

		private String command;
		private InteractionMode interactionMode;
		private String group;
		private boolean hidden;
		private String description;
		private Supplier<Availability> availability;
		private List<CommandOption> options;
		private List<DefaultOptionSpec> optionSpecs;
		private DefaultTargetSpec targetSpec;
		private List<DefaultAliasSpec> aliasSpecs;
		private DefaultExitCodeSpec exitCodeSpec;
		private DefaultErrorHandlingSpec errorHandlingSpec;
		private DefaultHelpOptionsSpec helpOptionsSpec;

		public DefaultCommandRegistration(String[] commands, InteractionMode interactionMode, String group,
				boolean hidden,	String description, Supplier<Availability> availability,
				List<DefaultOptionSpec> optionSpecs, DefaultTargetSpec targetSpec, List<DefaultAliasSpec> aliasSpecs,
				DefaultExitCodeSpec exitCodeSpec, DefaultErrorHandlingSpec errorHandlingSpec, DefaultHelpOptionsSpec helpOptionsSpec) {
			this.command = commandArrayToName(commands);
			this.interactionMode = interactionMode;
			this.group = group;
			this.hidden = hidden;
			this.description = description;
			this.availability = availability;
			this.optionSpecs = optionSpecs;
			this.targetSpec = targetSpec;
			this.aliasSpecs = aliasSpecs;
			this.exitCodeSpec = exitCodeSpec;
			this.errorHandlingSpec = errorHandlingSpec;
			this.helpOptionsSpec = helpOptionsSpec;
		}

		@Override
		public String getCommand() {
			return command;
		}

		@Override
		public InteractionMode getInteractionMode() {
			return interactionMode;
		}

		@Override
		public String getGroup() {
			return group;
		}

		@Override
		public boolean isHidden() {
			return hidden;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public Availability getAvailability() {
			return availability != null ? availability.get() : Availability.available();
		}

		@Override
		public List<CommandOption> getOptions() {
			if (options != null) {
				return options;
			}
			options = optionSpecs.stream()
				.map(o -> {
					String[] longNames = o.getLongNames();
					String[] longNamesModified = null;
					Function<String, String> modifier = o.getOptionNameModifier();
					if (modifier != null) {
						longNamesModified = Arrays.copyOf(longNames, longNames.length);
						longNames = Arrays.stream(longNames).map(modifier).toArray(String[]::new);
					}
					return CommandOption.of(longNames, longNamesModified, o.getShortNames(), o.getDescription(), o.getType(),
							o.isRequired(), o.getDefaultValue(), o.getPosition(), o.getArityMin(), o.getArityMax(),
							o.getLabel(), o.getCompletion());
					})
				.collect(Collectors.toList());
			if (helpOptionsSpec != null) {
				String[] longNames = helpOptionsSpec.longNames != null ? helpOptionsSpec.longNames : null;
				Character[] shortNames = helpOptionsSpec.shortNames != null ? helpOptionsSpec.shortNames : null;
				options.add(CommandOption.of(longNames, shortNames, "help for " + command,
						ResolvableType.forType(void.class)));
			}
			return options;
		}

		@Override
		public TargetInfo getTarget() {
			if (targetSpec.bean != null) {
				return TargetInfo.of(targetSpec.bean, targetSpec.method);
			}
			if (targetSpec.function != null) {
				return TargetInfo.of(targetSpec.function);
			}
			if (targetSpec.consumer != null) {
				return TargetInfo.of(targetSpec.consumer);
			}
			throw new IllegalArgumentException("No bean, function or consumer defined");
		}

		@Override
		public List<CommandAlias> getAliases() {
			return this.aliasSpecs.stream()
				.map(spec -> {
					return CommandAlias.of(commandArrayToName(spec.commands), spec.group);
				})
				.collect(Collectors.toList());
		}

		@Override
		public CommandExitCode getExitCode() {
			if (this.exitCodeSpec == null) {
				return CommandExitCode.of();
			}
			else {
				return CommandExitCode.of(exitCodeSpec.functions);
			}
		}

		@Override
		public List<CommandExceptionResolver> getExceptionResolvers() {
			if (this.errorHandlingSpec == null) {
				return Collections.emptyList();
			}
			else {
				return this.errorHandlingSpec.resolvers;
			}
		}

		@Override
		public HelpOptionInfo getHelpOption() {
			if (this.helpOptionsSpec == null) {
				return HelpOptionInfo.of();
			}
			else {
				return HelpOptionInfo.of(helpOptionsSpec.enabled, helpOptionsSpec.longNames, helpOptionsSpec.shortNames,
						helpOptionsSpec.command);
			}
		}

		private static String commandArrayToName(String[] commands) {
			return Arrays.asList(commands).stream()
				.flatMap(c -> Stream.of(c.split(" ")))
				.filter(c -> StringUtils.hasText(c))
				.map(c -> c.trim())
				.collect(Collectors.joining(" "));
		}
	}

	static class DefaultBuilder extends BaseBuilder {
	}

	static abstract class BaseBuilder implements Builder {

		private String[] commands;
		private InteractionMode interactionMode = InteractionMode.ALL;
		private String group;
		private boolean hidden;
		private String description;
		private Supplier<Availability> availability;
		private List<DefaultOptionSpec> optionSpecs = new ArrayList<>();
		private List<DefaultAliasSpec> aliasSpecs = new ArrayList<>();
		private DefaultTargetSpec targetSpec;
		private DefaultExitCodeSpec exitCodeSpec;
		private DefaultErrorHandlingSpec errorHandlingSpec;
		private DefaultHelpOptionsSpec helpOptionsSpec;
		private Function<String, String> defaultOptionNameModifier;

		@Override
		public Builder command(String... commands) {
			Assert.notNull(commands, "commands must be set");
			this.commands = Arrays.asList(commands).stream()
				.flatMap(c -> Stream.of(c.split(" ")))
				.filter(c -> StringUtils.hasText(c))
				.map(c -> c.trim())
				.collect(Collectors.toList())
				.toArray(new String[0]);
			return this;
		}

		@Override
		public Builder interactionMode(InteractionMode mode) {
			this.interactionMode = mode != null ? mode : InteractionMode.ALL;
			return this;
		}

		@Override
		public Builder description(String description) {
			this.description = description;
			return this;
		}

		@Override
		public Builder group(String group) {
			this.group = group;
			return this;
		}

		@Override
		public Builder hidden() {
			return hidden(true);
		}

		@Override
		public Builder hidden(boolean hidden) {
			this.hidden = hidden;
			return this;
		}

		@Override
		public Builder availability(Supplier<Availability> availability) {
			this.availability = availability;
			return this;
		}

		@Override
		public Builder defaultOptionNameModifier(Function<String,String> modifier) {
			this.defaultOptionNameModifier = modifier;
			return this;
		}

		@Override
		public OptionSpec withOption() {
			DefaultOptionSpec spec = new DefaultOptionSpec(this);
			optionSpecs.add(spec);
			return spec;
		}

		@Override
		public TargetSpec withTarget() {
			DefaultTargetSpec spec = new DefaultTargetSpec(this);
			targetSpec = spec;
			return spec;
		}

		@Override
		public AliasSpec withAlias() {
			DefaultAliasSpec spec = new DefaultAliasSpec(this);
			this.aliasSpecs.add(spec);
			return spec;
		}

		@Override
		public ExitCodeSpec withExitCode() {
			DefaultExitCodeSpec spec = new DefaultExitCodeSpec(this);
			this.exitCodeSpec = spec;
			return spec;
		}

		@Override
		public ErrorHandlingSpec withErrorHandling() {
			DefaultErrorHandlingSpec spec = new DefaultErrorHandlingSpec(this);
			this.errorHandlingSpec = spec;
			return spec;
		}

		@Override
		public HelpOptionsSpec withHelpOptions() {
			if (this.helpOptionsSpec == null) {
				this.helpOptionsSpec = new DefaultHelpOptionsSpec(this);
			}
			return this.helpOptionsSpec;
		}

		@Override
		public CommandRegistration build() {
			Assert.notNull(commands, "command cannot be empty");
			Assert.notNull(targetSpec, "target cannot be empty");
			Assert.state(!(targetSpec.bean != null && targetSpec.function != null), "only one target can exist");
			return new DefaultCommandRegistration(commands, interactionMode, group, hidden, description, availability,
					optionSpecs, targetSpec, aliasSpecs, exitCodeSpec, errorHandlingSpec, helpOptionsSpec);
		}
	}
}
