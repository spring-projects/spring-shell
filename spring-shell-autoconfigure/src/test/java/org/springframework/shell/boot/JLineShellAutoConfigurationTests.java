package org.springframework.shell.boot;

import org.jline.reader.Parser;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JLineShellAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JLineShellAutoConfiguration.class));


    @Nested
    class JLineShell {
        @Test
        void enabledByDefault() {
            contextRunner.run(context -> {
                assertThat(context).hasSingleBean(Terminal.class);
                assertThat(context).hasSingleBean(PromptProvider.class);
                assertThat(context).hasSingleBean(Parser.class);
            });
        }

        @Test
        void disabledWhenPropertySet() {
            contextRunner.withPropertyValues("spring.shell.jline-shell.enabled:false")
                    .run(context -> {
                        assertThat(context).doesNotHaveBean(Terminal.class);
                        assertThat(context).doesNotHaveBean(PromptProvider.class);
                        assertThat(context).doesNotHaveBean(Parser.class);
                    });
        }

        @Test
        void disabledWhenCustomBeanSet() {
            contextRunner.withUserConfiguration(MockConfiguration.class)
                    .run(context -> {
                        assertThat(context).hasSingleBean(Terminal.class);
                        final Terminal terminal = context.getBean(Terminal.class);
                        assertThat(terminal.getBufferSize()).isSameAs(MockConfiguration.MOCK_BUFFER_SIZE);

                        assertThat(context).hasSingleBean(Parser.class);
                        final Parser parser = context.getBean(Parser.class);
                        assertThat(parser.getCommand("cmd")).isEqualTo(MockConfiguration.MOCK_COMMAND_LINE);

                        assertThat(context).hasSingleBean(PromptProvider.class);
                        final PromptProvider promptProvider = context.getBean(PromptProvider.class);
                        assertThat(promptProvider.getPrompt()).isEqualTo(MockConfiguration.MOCK_ATTR_STRING);
                    });
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class MockConfiguration {
        public static final Size MOCK_BUFFER_SIZE = mock(Size.class);
        public static final String MOCK_COMMAND_LINE = "mock command line";
        public static final AttributedString MOCK_ATTR_STRING = mock(AttributedString.class);

        @Bean
        Terminal mockTerminal() {
            Terminal terminal = mock(Terminal.class);
            when(terminal.getBufferSize()).thenReturn(MOCK_BUFFER_SIZE);
            return terminal;
        }

        @Bean
        Parser mockParser() {
            final Parser parser = mock(Parser.class);
            when(parser.getCommand(anyString())).thenReturn(MOCK_COMMAND_LINE);
            return parser;
        }

        @Bean
        PromptProvider mocPromptProvider() {
            final PromptProvider promptProvider = mock(PromptProvider.class);
            when(promptProvider.getPrompt()).thenReturn(MOCK_ATTR_STRING);
            return promptProvider;
        }
    }
}