/*
 * Copyright 2026-present the original author or authors.
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
package org.springframework.shell.core.command.annotation.support;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.adapter.MethodInvokerCommandAdapter;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.shell.core.command.annotation.Option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author David Pilar
 */
class CommandFactoryBeanTests {

	private static ApplicationContext mockApplicationContext() {
		ApplicationContext context = mock(ApplicationContext.class);
		when(context.getBeansOfType(Converter.class)).thenReturn(Collections.emptyMap());
		when(context.getBeansOfType(GenericConverter.class)).thenReturn(Collections.emptyMap());
		when(context.getBeansOfType(ConverterFactory.class)).thenReturn(Collections.emptyMap());
		return context;
	}

	@Test
	void testOptionNames() {
		// given
		ApplicationContext context = mockApplicationContext();
		when(context.getBean(TestClass.class)).thenReturn(new TestClass());
		CommandFactoryBean commandFactoryBean = new CommandFactoryBean(TestClass.class.getDeclaredMethods()[0]);
		commandFactoryBean.setApplicationContext(context);

		// when
		org.springframework.shell.core.command.Command result = commandFactoryBean.getObject();

		// then
		assertEquals("hello", result.getName());
		List<CommandOption> options = result.getOptions();
		assertEquals(4, options.size());

		assertEquals("myOption1", options.get(0).longName());
		assertEquals(' ', options.get(0).shortName());

		assertEquals("myOption2", options.get(1).longName());
		assertEquals('m', options.get(1).shortName());

		assertEquals("longNameOption3", options.get(2).longName());
		assertEquals('l', options.get(2).shortName());

		assertEquals("longNameOption4", options.get(3).longName());
		assertEquals(' ', options.get(3).shortName());
	}

	@Test
	public void testCommandGroup() {
		// given
		ApplicationContext context = mockApplicationContext();
		when(context.getBean(GreetingCommands.class)).thenReturn(new GreetingCommands());
		Method[] declaredMethods = GreetingCommands.class.getDeclaredMethods();
		Method hiMethod = Arrays.stream(declaredMethods)
			.filter(m -> m.getName().equals("hi"))
			.findFirst()
			.orElseThrow();
		CommandFactoryBean commandFactoryBean = new CommandFactoryBean(hiMethod);
		commandFactoryBean.setApplicationContext(context);

		// when
		org.springframework.shell.core.command.Command result = commandFactoryBean.getObject();

		// then
		assertEquals("greeting hi", result.getName());
		assertEquals("Greeting Commands", result.getGroup());
	}

	@Test
	public void testCommandGroupOverride() {
		// given
		ApplicationContext context = mockApplicationContext();
		when(context.getBean(GreetingCommands.class)).thenReturn(new GreetingCommands());
		Method[] declaredMethods = GreetingCommands.class.getDeclaredMethods();
		Method byeMethod = Arrays.stream(declaredMethods)
			.filter(m -> m.getName().equals("bye"))
			.findFirst()
			.orElseThrow();
		CommandFactoryBean commandFactoryBean = new CommandFactoryBean(byeMethod);
		commandFactoryBean.setApplicationContext(context);

		// when
		org.springframework.shell.core.command.Command result = commandFactoryBean.getObject();

		// then
		assertEquals("greeting bye", result.getName());
		assertEquals("Farewell Commands", result.getGroup());
	}

	@Test
	void converterBeansAreAppliedToConfigurableConversionService() throws Exception {
		ApplicationContext context = mockConverterApplicationContext();
		doReturn(Map.of("messageConverter", new MessageConverter())).when(context).getBeansOfType(Converter.class);

		runConvertCommandAndAssert(context);
	}

	@Test
	void genericConverterBeansAreAppliedToConfigurableConversionService() throws Exception {
		ApplicationContext context = mockConverterApplicationContext();
		doReturn(Map.of("messageGenericConverter", new MessageGenericConverter())).when(context)
			.getBeansOfType(GenericConverter.class);

		runConvertCommandAndAssert(context);
	}

	@Test
	void converterFactoryBeansAreAppliedToConfigurableConversionService() throws Exception {
		ApplicationContext context = mockConverterApplicationContext();
		doReturn(Map.of("messageConverterFactory", new MessageConverterFactory())).when(context)
			.getBeansOfType(ConverterFactory.class);

		runConvertCommandAndAssert(context);
	}

	private static ApplicationContext mockConverterApplicationContext() {
		ApplicationContext context = mockApplicationContext();
		when(context.getBean(ConverterTarget.class)).thenReturn(new ConverterTarget());
		when(context.getBean(ConfigurableConversionService.class))
			.thenThrow(new NoSuchBeanDefinitionException(ConfigurableConversionService.class));
		when(context.getBean(jakarta.validation.Validator.class))
			.thenThrow(new NoSuchBeanDefinitionException(jakarta.validation.Validator.class));
		return context;
	}

	private static void runConvertCommandAndAssert(ApplicationContext context) throws Exception {
		Method method = Arrays.stream(ConverterTarget.class.getDeclaredMethods())
			.filter(m -> m.getName().equals("run"))
			.findFirst()
			.orElseThrow();
		CommandFactoryBean factory = new CommandFactoryBean(method);
		factory.setApplicationContext(context);
		MethodInvokerCommandAdapter command = (MethodInvokerCommandAdapter) factory.getObject();

		ConverterTarget.lastSeen = null;
		CommandContext ctx = mock(CommandContext.class);
		when(ctx.getOptionByLongName("msg")).thenReturn(CommandOption.with().longName("msg").value("hello").build());
		when(ctx.outputWriter()).thenReturn(new java.io.PrintWriter(new java.io.StringWriter()));
		command.doExecute(ctx);

		assertThat(ConverterTarget.lastSeen).isNotNull();
		assertThat(ConverterTarget.lastSeen.text).isEqualTo("hello");
	}

	static class Message {

		String text;

	}

	static class MessageConverter implements Converter<String, Message> {

		@Override
		public Message convert(String source) {
			Message m = new Message();
			m.text = source;
			return m;
		}

	}

	static class MessageGenericConverter implements GenericConverter {

		@Override
		public java.util.Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
			return java.util.Set.of(new GenericConverter.ConvertiblePair(String.class, Message.class));
		}

		@Override
		public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
			Message m = new Message();
			m.text = (String) source;
			return m;
		}

	}

	static class MessageConverterFactory implements ConverterFactory<String, Message> {

		@Override
		public <T extends Message> Converter<String, T> getConverter(Class<T> targetType) {
			return source -> {
				try {
					T m = targetType.getDeclaredConstructor().newInstance();
					m.text = source;
					return m;
				}
				catch (ReflectiveOperationException e) {
					throw new IllegalStateException(e);
				}
			};
		}

	}

	static class ConverterTarget {

		static Message lastSeen;

		@Command(name = "convert")
		public void run(@Option(longName = "msg", required = true) Message msg) {
			lastSeen = msg;
		}

	}

	static class TestClass {

		@Command(name = "hello")
		public void helloMethod(@Option String myOption1, @Option(shortName = 'm') String myOption2,
				@Option(longName = "longNameOption3", shortName = 'l') String myOption3,
				@Option(longName = "longNameOption4") String myOption4) {
			// no-op
		}

	}

	@CommandGroup(name = "Greeting Commands", prefix = "greeting")
	static class GreetingCommands {

		@Command(name = "hi")
		public void hi() {
			// no-op
		}

		@Command(name = "bye", group = "Farewell Commands")
		public void bye() {
			// no-op
		}

	}

}