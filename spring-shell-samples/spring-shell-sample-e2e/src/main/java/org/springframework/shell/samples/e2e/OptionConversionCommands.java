/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.samples.e2e;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;

public class OptionConversionCommands {

	@ShellComponent
	public static class LegacyAnnotation extends BaseE2ECommands {

		@ShellMethod(key = LEGACY_ANNO + "option-conversion-integer", group = GROUP)
		public String optionConversionIntegerAnnotation(
			@ShellOption Integer arg1
		) {
			return "Hello " + arg1;
		}

		@ShellMethod(key = LEGACY_ANNO + "option-conversion-custom", group = GROUP)
		public String optionConversionCustomAnnotation(
			@ShellOption MyPojo arg1
		) {
			return "Hello " + arg1;
		}

		@ShellMethod(key = LEGACY_ANNO + "option-conversion-customset", group = GROUP)
		public String optionConversionCustomSetAnnotation(
			@ShellOption Set<MyPojo> arg1
		) {
			return "Hello " + arg1;
		}

		@ShellMethod(key = LEGACY_ANNO + "option-conversion-customlist", group = GROUP)
		public String optionConversionCustomListAnnotation(
			@ShellOption List<MyPojo> arg1
		) {
			return "Hello " + arg1;
		}

		@ShellMethod(key = LEGACY_ANNO + "option-conversion-customarray", group = GROUP)
		public String optionConversionCustomArrayAnnotation(
			@ShellOption MyPojo[] arg1
		) {
			return "Hello " + Arrays.asList(arg1);
		}
	}

	@Command(command = BaseE2ECommands.ANNO, group = BaseE2ECommands.GROUP)
	public static class Annotation extends BaseE2ECommands {

		@Command(command = "option-conversion-integer")
		public String optionConversionIntegerAnnotation(
			@Option(longNames = "arg1")
			Integer arg1
		) {
			return "Hello " + arg1;
		}

		@Command(command = "option-conversion-custom")
		public String optionConversionCustomAnnotation(
			@Option(longNames = "arg1")
			MyPojo arg1
		) {
			return "Hello " + arg1;
		}

		@Command(command = "option-conversion-customset")
		public String optionConversionCustomSetAnnotation(
			@Option(longNames = "arg1")
			Set<MyPojo> arg1
		) {
			return "Hello " + arg1;
		}

		@Command(command = "option-conversion-customarray")
		public String optionConversionCustomArrayAnnotation(
			@Option(longNames = "arg1")
			MyPojo[] arg1
		) {
			return "Hello " + Arrays.asList(arg1);
		}
	}

	@Component
	public static class Registration extends BaseE2ECommands {

		@Bean
		public CommandRegistration optionConversionIntegerRegistration() {
			return getBuilder()
				.command(REG, "option-conversion-integer")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(Integer.class)
					.and()
				.withTarget()
					.function(ctx -> {
						Integer arg1 = ctx.getOptionValue("arg1");
						return "Hello " + arg1;
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration optionConversionCustomRegistration() {
			return getBuilder()
				.command(REG, "option-conversion-custom")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(MyPojo.class)
					.and()
				.withTarget()
					.function(ctx -> {
						MyPojo arg1 = ctx.getOptionValue("arg1");
						return "Hello " + arg1;
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration optionConversionCustomSetRegistration() {
			ResolvableType rtype = ResolvableType.forClassWithGenerics(Set.class, MyPojo.class);
			return CommandRegistration.builder()
				.command(REG, "option-conversion-customset")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(rtype)
					.and()
				.withTarget()
					.function(ctx -> {
						Set<MyPojo> arg1 = ctx.getOptionValue("arg1");
						return "Hello " + arg1;
					})
					.and()
				.build();
		}

		@Bean
		public CommandRegistration optionConversionCustomArrayRegistration() {
			return getBuilder()
				.command(REG, "option-conversion-customarray")
				.group(GROUP)
				.withOption()
					.longNames("arg1")
					.type(MyPojo[].class)
					.and()
				.withTarget()
					.function(ctx -> {
						MyPojo[] arg1 = ctx.getOptionValue("arg1");
						return "Hello " + Arrays.asList(arg1);
					})
					.and()
				.build();
		}
	}

	@Configuration(proxyBeanMethods = false)
	public static class CommonConfiguration {

		@Bean
		public Converter<String, MyPojo> stringToMyPojoConverter() {
			return new StringToMyPojoConverter();
		}
	}

	public static class MyPojo {
		private String arg;

		public MyPojo(String arg) {
			this.arg = arg;
		}

		public String getArg() {
			return arg;
		}

		public void setArg(String arg) {
			this.arg = arg;
		}

		@Override
		public String toString() {
			return "MyPojo [arg=" + arg + "]";
		}
	}

	static class StringToMyPojoConverter implements Converter<String, MyPojo> {

		@Override
		public MyPojo convert(String from) {
			return new MyPojo(from);
		}
	}
}
