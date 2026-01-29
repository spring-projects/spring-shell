package org.springframework.shell.core.command.annotation.support;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author David Pilar
 */
class CommandFactoryBeanTests {

	@Test
	void testOptionNames() {
		// given
		ApplicationContext context = mock(ApplicationContext.class);
		when(context.getBean(TestClass.class)).thenReturn(new TestClass());
		CommandFactoryBean commandFactoryBean = new CommandFactoryBean(TestClass.class.getDeclaredMethods()[0]);
		commandFactoryBean.setApplicationContext(context);

		// when
		org.springframework.shell.core.command.Command result = commandFactoryBean.getObject();

		// then
		assertEquals("hello", result.getName());
		assertEquals(4, result.getOptions().size());

		assertEquals("myOption1", result.getOptions().get(0).longName());
		assertEquals(' ', result.getOptions().get(0).shortName());

		assertEquals("myOption2", result.getOptions().get(1).longName());
		assertEquals('m', result.getOptions().get(1).shortName());

		assertEquals("longNameOption3", result.getOptions().get(2).longName());
		assertEquals('l', result.getOptions().get(2).shortName());

		assertEquals("longNameOption4", result.getOptions().get(3).longName());
		assertEquals(' ', result.getOptions().get(3).shortName());
	}

	static class TestClass {

		@Command(name = "hello")
		public void helloMethod(@Option String myOption1, @Option(shortName = 'm') String myOption2,
				@Option(longName = "longNameOption3", shortName = 'l') String myOption3,
				@Option(longName = "longNameOption4") String myOption4) {
			// no-op
		}

	}

}