package org.springframework.shell.commands;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.springframework.roo.support.logging.HandlerUtils;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link OsOperations} interface.
 * 
 * @author Stefan Schmidt
 * @since 1.2.0
 */
@Component
public class OsOperationsImpl implements OsOperations {
	private static final Logger LOGGER = HandlerUtils.getLogger(OsOperationsImpl.class);

	public void executeCommand(final String command) throws IOException {
		final File root = new File(".");
		final Process p = Runtime.getRuntime().exec(command, null, root);
		Reader input = new InputStreamReader(p.getInputStream());
		Reader errors = new InputStreamReader(p.getErrorStream());

		for (String line : IOUtils.readLines(input)) {
			if (line.startsWith("[ERROR]")) {
				LOGGER.severe(line);
			}
			else if (line.startsWith("[WARNING]")) {
				LOGGER.warning(line);
			}
			else {
				LOGGER.info(line);
			}
		}

		
		for (String line : IOUtils.readLines(errors)) {
			if (line.startsWith("[ERROR]")) {
				LOGGER.severe(line);
			}
			else if (line.startsWith("[WARNING]")) {
				LOGGER.warning(line);
			}
			else {
				LOGGER.info(line);
			}
		}

		
		p.getOutputStream().close();


		try {
			if (p.waitFor() != 0) {
				LOGGER.warning("The command '" + command + "' did not complete successfully");
			}
		} catch (final InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}