package org.springframework.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.shell.support.util.IOUtils;
import org.springframework.shell.support.util.MathUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

@Component
public class UtilityCommands implements CommandMarker {

    protected final Logger logger = HandlerUtils.getLogger(getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JLineShellComponent shell;

    @CliCommand(value = { "script" }, help = "Parses the specified resource file and executes its commands")
    public void script(
            @CliOption(key = { "", "file" }, help = "The file to locate and execute", mandatory = true) final File script,
            @CliOption(key = "lineNumbers", mandatory = false, specifiedDefaultValue = "true", unspecifiedDefaultValue = "false", help = "Display line numbers when executing the script") final boolean lineNumbers) {

        Assert.notNull(script, "Script file to parse is required");
        double startedNanoseconds = System.nanoTime();
        final InputStream inputStream = openScript(script);

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int i = 0;
            while ((line = in.readLine()) != null) {
                i++;
                if (lineNumbers) {
                    logger.fine("Line " + i + ": " + line);
                } else {
                    logger.fine(line);
                }
                if (!"".equals(line.trim())) {
                    boolean success = shell.executeCommand(line);
                    if (success && ((line.trim().startsWith("q") || line.trim().startsWith("ex")))) {
                        break;
                    } else if (!success) {
                        // Abort script processing, given something went wrong
                        throw new IllegalStateException("Script execution aborted");
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(inputStream, in);
            double executionDurationInSeconds = (System.nanoTime() - startedNanoseconds) / 1000000000D;
            logger.fine("Script required " + MathUtils.round(executionDurationInSeconds, 3) + " seconds to execute");
        }
    }

    /**
     * Opens the given script for reading
     *
     * @param script the script to read (required)
     * @return a non-<code>null</code> input stream
     */
    private InputStream openScript(final File script) {
        try {
            return new BufferedInputStream(new FileInputStream(script));
        } catch (final FileNotFoundException fnfe) {
            // Try to find the script via the classloader
            final Collection<URL> urls = findResources(script.getName());

            // Handle search failure
            Assert.notNull(urls, "Unexpected error looking for '" + script.getName() + "'");

            // Handle the search being OK but the file simply not being present
            Assert.notEmpty(urls, "Script '" + script + "' not found on disk or in classpath");
            Assert.isTrue(urls.size() == 1, "More than one '" + script + "' was found in the classpath; unable to continue");
            try {
                return urls.iterator().next().openStream();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private Collection<URL> findResources(final String path) {
        try {
            Resource[] resources = applicationContext.getResources(path);
            Collection<URL> list = new ArrayList<URL>(resources.length);
            for (Resource resource : resources) {
                list.add(resource.getURL());
            }
            return list;
        } catch (IOException ex) {
            logger.fine("Cannot find path " + path);
            // return Collections.emptyList();
            throw new RuntimeException(ex);
        }
    }


    /**
     * Returns any classpath resources with the given path
     *
     * @param path the path for which to search (never null)	@CliCommand(value = { "script" }, help = "Parses the specified resource file and executes its commands")
    public void script(
     @CliOption(key = { "", "file" }, help = "The file to locate and execute", mandatory = true) final File script,
     @CliOption(key = "lineNumbers", mandatory = false, specifiedDefaultValue = "true", unspecifiedDefaultValue = "false", help = "Display line numbers when executing the script") final boolean lineNumbers) {

     Assert.notNull(script, "Script file to parse is required");
     double startedNanoseconds = System.nanoTime();
     final InputStream inputStream = openScript(script);

     BufferedReader in = null;
     try {
     in = new BufferedReader(new InputStreamReader(inputStream));
     String line;
     int i = 0;
     while ((line = in.readLine()) != null) {
     i++;
     if (lineNumbers) {
     logger.fine("Line " + i + ": " + line);
     } else {
     logger.fine(line);
     }
     if (!"".equals(line.trim())) {
     boolean success = executeScriptLine(line);
     if (success && ((line.trim().startsWith("q") || line.trim().startsWith("ex")))) {
     break;
     } else if (!success) {
     // Abort script processing, given something went wrong
     throw new IllegalStateException("Script execution aborted");
     }
     }
     }
     } catch (IOException e) {
     throw new IllegalStateException(e);
     } finally {
     IOUtils.closeQuietly(inputStream, in);
     double executionDurationInSeconds = (System.nanoTime() - startedNanoseconds) / 1000000000D;
     logger.fine("Script required " + MathUtils.round(executionDurationInSeconds, 3) + " seconds to execute");
     }
     }
      * @return <code>null</code> if the search can't be performed
     * @since 1.2.0
     */

}
