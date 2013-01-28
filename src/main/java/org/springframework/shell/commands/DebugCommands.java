package org.springframework.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.springframework.shell.support.util.OsUtils.LINE_SEPARATOR;

@Component
public class DebugCommands implements CommandMarker {

    protected final Logger logger = HandlerUtils.getLogger(getClass());

    @Autowired
    private JLineShellComponent shell;

    @CliCommand(value = { "system properties" }, help = "Shows the shell's properties")
    public String props() {
        final Set<String> data = new TreeSet<String>(); // For repeatability
        for (final Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            data.add(entry.getKey() + " = " + entry.getValue());
        }

        return StringUtils.collectionToDelimitedString(data, LINE_SEPARATOR) + LINE_SEPARATOR;
    }

    @CliCommand(value = { "date" }, help = "Displays the local date and time")
    public String date() {
        return DateFormat.getDateTimeInstance(
                DateFormat.FULL, DateFormat.FULL, Locale.US)
                .format(new Date());
    }

    //@CliCommand(value = { "flash test" }, help = "Tests message flashing")
    public void flashCustom() throws Exception {
        shell.flash(Level.FINE, "Hello world", "a");
        Thread.sleep(150);
        shell.flash(Level.FINE, "Short world", "a");
        Thread.sleep(150);
        shell.flash(Level.FINE, "Small", "a");
        Thread.sleep(150);
        shell.flash(Level.FINE, "Downloading xyz", "b");
        Thread.sleep(150);
        shell.flash(Level.FINE, "", "a");
        Thread.sleep(150);
        shell.flash(Level.FINE, "Downloaded xyz", "b");
        Thread.sleep(150);
        shell.flash(Level.FINE, "System online", "c");
        Thread.sleep(150);
        shell.flash(Level.FINE, "System ready", "c");
        Thread.sleep(150);
        shell.flash(Level.FINE, "System farewell", "c");
        Thread.sleep(150);
        shell.flash(Level.FINE, "", "c");
        Thread.sleep(150);
        shell.flash(Level.FINE, "", "b");
    }

}
