package org.springframework.shell;

import static org.springframework.roo.support.util.StringUtils.LINE_SEPARATOR;

import java.net.URL;
import java.util.Collection;

import org.springframework.context.Lifecycle;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.ExecutionStrategy;
import org.springframework.roo.shell.Parser;
import org.springframework.roo.shell.SimpleParser;

/**
 * Launcher for {@link JLineShell}.
 *
 * @author Ben Alex
 * @since 1.1
 */
public class JLineShellComponent extends JLineShell implements Lifecycle {

	private volatile boolean running = false;
		 
	// Fields
	private ExecutionStrategy executionStrategy = new SimpleExecutionStrategy(); //ProcessManagerHostedExecutionStrategy is not what i think we need outside of Roo.		
	private SimpleParser parser = new SimpleParser();
	
	// Dont' need this, used to get twitter status.
	//@Reference private UrlInputStreamService urlInputStreamService;
	//

	
	public SimpleParser getSimpleParser() {
		return parser;
	}
	
	
	public void start() {		
		running=true;
	}
	

	public void stop()  {
		closeShell();
		running=false;
	}
	
	public boolean isRunning() {
		return running;
	}

	@Override
	protected Collection<URL> findResources(final String path) {
		// For an OSGi bundle search, we add the root prefix to the given path
		throw new UnsupportedOperationException("TODO: need to use standard classpath search");
		//return OSGiUtils.findEntriesByPath(context.getBundleContext(), OSGiUtils.ROOT_PATH + path);
	}

	@Override
	protected ExecutionStrategy getExecutionStrategy() {
		return executionStrategy;
	}

	@Override
	protected Parser getParser() {
		return parser;
	}

	@Override
	public String getStartupNotifications() {
		return null;
	}

	
		
	@CliCommand(value = { "version" }, help = "Displays shell version")
	public String version(@CliOption(key = "", help = "Special version flags") final String extra) {
		StringBuilder sb = new StringBuilder();
		sb.append(" _____            _    ").append(LINE_SEPARATOR);
		sb.append("/  ___|          (_)").append(LINE_SEPARATOR);
		sb.append("\\ `--, _ __  _ __ _ _ __   __ _ ").append(LINE_SEPARATOR);
		sb.append(" `--. \\ '_ \\| '__| | '_ \\ / _` |").append(LINE_SEPARATOR);
		sb.append("/\\__/ / |_) | |  | | | | | (_| |").append(LINE_SEPARATOR);
		sb.append("\\____/| .__/|_|  |_|_| |_|\\__, |").append(LINE_SEPARATOR);
		sb.append("      | |                  __/ |").append(LINE_SEPARATOR);
		sb.append("      |_|                 |___/ ").append(" ").append(versionInfo()).append(LINE_SEPARATOR);
		sb.append(LINE_SEPARATOR);

		return sb.toString();
	
	}

}