package org.springframework.shell;

import java.net.URL;
import java.util.Collection;

import org.springframework.context.Lifecycle;
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
	private Thread shellThread;

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
		shellThread = new Thread(this, "Spring Shell");
		shellThread.start();
		running = true;
	}


	public void stop() {
		closeShell();
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	/**
	 * wait the shell command to complete by typing "quit" or "exit" 
	 * 
	 */
	public void waitForComplete() {
		try {
			shellThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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


}