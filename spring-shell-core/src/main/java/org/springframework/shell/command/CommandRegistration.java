/*
 * Copyright 2022 the original author or authors.
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
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.shell.Availability;
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
	 * Gets an array of commands for this registration.
	 *
	 * @return array of commands
	 */
	String[] getCommands();

	/**
	 * Gets an {@link InteractionMode}.
	 *
	 * @return the interaction mode
	 */
	InteractionMode getInteractionMode();

	/**
	 * Get help for a command.
	 *
	 * @return the help
	 */
	String getHelp();

	/**
	 * Get group for a command.
	 *
	 * @return the group
	 */
	String getGroup();

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
	 * Gets a new instance of a {@link Buidler}.
	 *
	 * @return a new builder instance
	 */
	public static Builder builder() {
		return new DefaultBuilder();
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
		 * Define a type for an option.
		 *
		 * @param type the type
		 * @return option spec for chaining
		 */
		OptionSpec type(Type type);

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
		 * Return a builder for chaining.
		 *
		 * @return a builder for chaining
		 */
		Builder and();
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
		 * Define a simple help text for a command.
		 *
		 * @param help the help text
		 * @return builder for chaining
		 */
		Builder help(String help);

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

	static class DefaultCommandRegistration implements CommandRegistration {

		private String[] commands;
		private InteractionMode interactionMode;
		private String help;
		private String group;
		private String description;
		private Supplier<Availability> availability;
		private List<DefaultOptionSpec> optionSpecs;
		private DefaultTargetSpec targetSpec;

		public DefaultCommandRegistration(String[] commands, InteractionMode interactionMode, String help,
				String group, String description, Supplier<Availability> availability,
				List<DefaultOptionSpec> optionSpecs, DefaultTargetSpec targetSpec) {
			this.commands = commands;
			this.interactionMode = interactionMode;
			this.help = help;
			this.group = group;
			this.description = description;
			this.availability = availability;
			this.optionSpecs = optionSpecs;
			this.targetSpec = targetSpec;
		}

		@Override
		public String[] getCommands() {
			return commands;
		}

		@Override
		public InteractionMode getInteractionMode() {
			return interactionMode;
		}

		@Override
		public String getHelp() {
			return help;
		}

		@Override
		public String getGroup() {
			return group;
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
			return optionSpecs.stream()
				.map(o -> CommandOption.of(o.getLongNames(), o.getShortNames(), o.getDescription(), o.getType(),
						o.isRequired(), o.getDefaultValue(), o.getPosition()))
				.collect(Collectors.toList());
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
	}

	static class DefaultBuilder extends BaseBuilder {

	}

	static class BaseBuilder implements Builder {

		private String[] commands;
		private InteractionMode interactionMode = InteractionMode.ALL;
		private String help;
		private String group;
		private String description;
		private Supplier<Availability> availability;
		private List<DefaultOptionSpec> optionSpecs = new ArrayList<>();
		private DefaultTargetSpec targetSpec;

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
		public Builder help(String help) {
			this.help = help;
			return this;
		}

		@Override
		public Builder group(String group) {
			this.group = group;
			return this;
		}

		@Override
		public Builder availability(Supplier<Availability> availability) {
			this.availability = availability;
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
		public CommandRegistration build() {
			Assert.notNull(commands, "command cannot be empty");
			Assert.notNull(targetSpec, "target cannot be empty");
			Assert.state(!(targetSpec.bean != null && targetSpec.function != null), "only one target can exist");
			return new DefaultCommandRegistration(commands, interactionMode, help, group, description, availability,
					optionSpecs, targetSpec);
		}
	}
}
