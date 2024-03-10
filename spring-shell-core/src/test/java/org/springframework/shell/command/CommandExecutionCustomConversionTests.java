/*
 * Copyright 2023-2024 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.core.ResolvableType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.shell.command.CommandRegistration.OptionArity;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandExecutionCustomConversionTests {

	private CommandExecution execution;
	private CommandCatalog commandCatalog;

	@BeforeEach
	public void setupCommandExecutionTests() {
		commandCatalog = CommandCatalog.of();
		DefaultConversionService conversionService = new DefaultConversionService();
		conversionService.addConverter(new StringToMyPojo2Converter());
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
		resolvers.add(new ArgumentHeaderMethodArgumentResolver(conversionService, null));
		resolvers.add(new CommandContextMethodArgumentResolver());
		execution = CommandExecution.of(resolvers, null, null, null, conversionService, commandCatalog);
	}

	@Test
	public void testCustomPojo() {
		Pojo1 pojo1 = new Pojo1();

		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.and()
			.withTarget()
				.method(pojo1, "method1")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "--arg1", "myarg1value" });
		assertThat(pojo1.method1Pojo2).isNotNull();
	}

	@Test
	public void testCustomPojoArray() {
		Pojo1 pojo1 = new Pojo1();

		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.and()
			.withTarget()
				.method(pojo1, "method2")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "--arg1", "a,b" });
		assertThat(pojo1.method2Pojo2).isNotNull();
		assertThat(pojo1.method2Pojo2.length).isEqualTo(2);
		assertThat(pojo1.method2Pojo2[0].arg).isEqualTo("a");
		assertThat(pojo1.method2Pojo2[1].arg).isEqualTo("b");
	}

	@Test
	public void testCustomPojoArrayPositional() {
		Pojo1 pojo1 = new Pojo1();

		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.arity(OptionArity.ONE_OR_MORE)
				.position(0)
				.and()
			.withTarget()
				.method(pojo1, "method2")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "a,b" });
		assertThat(pojo1.method2Pojo2).isNotNull();
		assertThat(pojo1.method2Pojo2.length).isEqualTo(2);
		assertThat(pojo1.method2Pojo2[0].arg).isEqualTo("a");
		assertThat(pojo1.method2Pojo2[1].arg).isEqualTo("b");
	}

	@Test
	public void testCustomPojoList() {
		Pojo1 pojo1 = new Pojo1();

		ResolvableType type = ResolvableType.forClassWithGenerics(List.class, Pojo1.class);

		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.type(type)
				.and()
			.withTarget()
				.method(pojo1, "method3")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "--arg1", "a,b" });
		assertThat(pojo1.method3Pojo2).isNotNull();
		assertThat(pojo1.method3Pojo2.size()).isEqualTo(2);
		assertThat(pojo1.method3Pojo2.get(0)).isInstanceOf(Pojo2.class);
		assertThat(pojo1.method3Pojo2.get(0).arg).isEqualTo("a");
		assertThat(pojo1.method3Pojo2.get(1).arg).isEqualTo("b");
	}

	@Test
	public void testCustomPojoListPositional() {
		Pojo1 pojo1 = new Pojo1();

		ResolvableType type = ResolvableType.forClassWithGenerics(List.class, Pojo1.class);

		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.arity(OptionArity.ONE_OR_MORE)
				.position(0)
				.type(type)
				.and()
			.withTarget()
				.method(pojo1, "method3")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "a,b" });
		assertThat(pojo1.method3Pojo2).isNotNull();
		assertThat(pojo1.method3Pojo2.size()).isEqualTo(2);
		assertThat(pojo1.method3Pojo2.get(0)).isInstanceOf(Pojo2.class);
		assertThat(pojo1.method3Pojo2.get(0).arg).isEqualTo("a");
		assertThat(pojo1.method3Pojo2.get(1).arg).isEqualTo("b");
	}

	@Test
	public void testCustomPojoSet() {
		Pojo1 pojo1 = new Pojo1();

		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.and()
			.withTarget()
				.method(pojo1, "method4")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "--arg1", "a,b" });
		assertThat(pojo1.method4Pojo2).isNotNull();
		assertThat(pojo1.method4Pojo2.size()).isEqualTo(2);
		assertThat(pojo1.method4Pojo2.iterator().next()).isInstanceOf(Pojo2.class);
		assertThat(pojo1.method4Pojo2.stream().map(pojo -> pojo.arg).toList()).containsExactly("a", "b");
	}

	@Test
	public void testCustomPojoSetPositional() {
		Pojo1 pojo1 = new Pojo1();

		CommandRegistration r1 = CommandRegistration.builder()
			.command("command1")
			.description("help")
			.withOption()
				.longNames("arg1")
				.arity(OptionArity.ONE_OR_MORE)
				.position(0)
				.and()
			.withTarget()
				.method(pojo1, "method4")
				.and()
			.build();
		commandCatalog.register(r1);
		execution.evaluate(new String[] { "command1", "a,b" });
		assertThat(pojo1.method4Pojo2).isNotNull();
		assertThat(pojo1.method4Pojo2.size()).isEqualTo(2);
		assertThat(pojo1.method4Pojo2.iterator().next()).isInstanceOf(Pojo2.class);
		assertThat(pojo1.method4Pojo2.stream().map(pojo -> pojo.arg).toList()).containsExactly("a", "b");
	}

	static class StringToMyPojo2Converter implements Converter<String, Pojo2> {

		@Override
		public Pojo2 convert(String from) {
			return new Pojo2(from);
		}
	}

	static class Pojo1 {

		public Pojo2 method1Pojo2;
		public Pojo2[] method2Pojo2;
		public List<Pojo2> method3Pojo2;
		public Set<Pojo2> method4Pojo2;

		public void method1(Pojo2 arg1) {
			method1Pojo2 = arg1;
		}

		public void method2(Pojo2[] arg1) {
			method2Pojo2 = arg1;
		}

		public void method3(List<Pojo2> arg1) {
			method3Pojo2 = arg1;
		}

		public void method4(Set<Pojo2> arg1) {
			method4Pojo2 = arg1;
		}
	}

	static class Pojo2 {

		public String arg;

		public Pojo2() {
		}

		public Pojo2(String arg) {
			this.arg = arg;
		}
	}
}
