package org.springframework.shell.result;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.shell.CommandNotFound;
import org.springframework.shell.ResultHandler;
import org.springframework.shell.result.CommandNotFoundMessageProvider.ProviderContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link ResultHandler} for {@link CommandNotFound} using
 * {@link CommandNotFoundMessageProvider} to provide an error message.
 * Default internal provider simply provides message from a {@link CommandNotFound}
 * with a red color. Provider can be defined by providing a custom
 * {@link CommandNotFoundMessageProvider} bean.
 *
 * @author Janne Valkealahti
 */
public final class CommandNotFoundResultHandler extends TerminalAwareResultHandler<CommandNotFound> {

	private CommandNotFoundMessageProvider provider;

	public CommandNotFoundResultHandler(Terminal terminal, ObjectProvider<CommandNotFoundMessageProvider> provider) {
		super(terminal);
		Assert.notNull(provider, "provider cannot be null");
		this.provider = provider.getIfAvailable(() -> new DefaultProvider());
	}

	@Override
	protected void doHandleResult(CommandNotFound result) {
		ProviderContext context = CommandNotFoundMessageProvider.contextOf(result, result.getWords(),
				result.getRegistrations(), result.getText());
		String message = provider.apply(context);
		if (StringUtils.hasText(message)) {
			terminal.writer().println(message);
		}
	}

	private static class DefaultProvider implements CommandNotFoundMessageProvider {

		@Override
		public String apply(ProviderContext context) {
			String message = new AttributedString(context.error().getMessage(),
					AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi();
			return message;
		}
	}
}
