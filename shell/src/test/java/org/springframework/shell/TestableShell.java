/*
 * Copyright 2011-2013 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import jline.ConsoleReader;
import jline.ConsoleReaderWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.ExecutionStrategy;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.JLineCompletorAdapter;
import org.springframework.shell.core.JLineLogHandler;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.core.Parser;
import org.springframework.shell.core.Shell;
import org.springframework.shell.event.ParseResult;
import org.springframework.shell.event.ShellStatus.Status;
import org.springframework.util.StringUtils;


/**
 * 
 * Testable shell implementation implementing ShellEvent interface.
 * 
 * @author tushark
 * @author David Wwinterfeldt
 */
public class TestableShell extends JLineShellComponent implements ShellEvent {

    private static final Logger logger = LoggerFactory.getLogger(TestableShell.class);

    protected static final Object EXIT_SHELL_COMMAND = TestableShell.class.getCanonicalName() + ".exit";
    private EventedInputStream eis = null;
    private ByteArrayOutputStream output = null;
    private JLineCompletorAdapter completorAdaptor = null;
    private Map<String, List> completorOutput = new HashMap<String, List>();
    private Map<String, Object> commandOutput = new HashMap<String, Object>();
    private ConsoleReader newConsoleReader = null;
    private String name = null;
    private String commandExecError = null;
    private CountDownLatch resultLatch = null;

    private Writer wrappedOut = null; // saj

    final private Parser oldParser;
    private Parser parserHook = null;
    private long timeout = 60;
    public static boolean useVirtualTerm = true;

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("windows") == -1 && useVirtualTerm) {
            System.setProperty("jline.terminal", jline.VirtualUnixTerminal.class.getCanonicalName());
        }
    }

    public static void setVirtualTerminal(boolean yes) {
        if (yes) {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.indexOf("windows") == -1 && useVirtualTerm) {
                System.setProperty("jline.terminal", jline.VirtualUnixTerminal.class.getCanonicalName());
            }
        } else {
            System.setProperty("jline.terminal", "");
        }
    }

    public TestableShell(String name, boolean launchShell) throws ClassNotFoundException, IOException {
        oldParser = super.getParser();
        resultLatch = new CountDownLatch(1);
        parserHook = new Parser() {
            public ParseResult parse(String buffer) {
                logger.debug("In parsing hook .... with input buffer <" + buffer + ">");
                ParseResult result = null;
                try {
                    result = oldParser.parse(buffer);
                } catch (Exception e) {
                    String reason = e.getMessage() != null ? " Reason: " + e.getMessage() + " Exception: "
                            + String.valueOf(e) : " Exception: " + String.valueOf(e);
                    addError(
                            "Parsing failed...." + reason + " buffer returned by EIS "
                                    + eis.getBufferFormdAfterReading(), e);
                    return null;
                }
                
                if (result == null) {
                    addError("Parsing failed....", null);
                } else {
                    logger.debug("Parse Result is " + result);
                }
                return result;
            }

            public int completeAdvanced(String buffer, int cursor, List<Completion> candidates) {
                return oldParser.completeAdvanced(buffer, cursor, candidates);
            }

            public int complete(String buffer, int cursor, List<String> candidates) {
                return oldParser.complete(buffer, cursor, candidates);
            }
        };

        this.name = name;
        eis = new EventedInputStream();
        output = new ByteArrayOutputStream(1024 * 10);
        PrintStream sysout = new PrintStream(output, true);

        // saj
        wrappedOut = new BufferedWriter(new OutputStreamWriter(sysout));

        try {
            ConsoleReaderWrapper wrapper = new ConsoleReaderWrapper(eis, wrappedOut);
            logger.debug("Reader created is " + wrapper);
            newConsoleReader = wrapper;
            reader = newConsoleReader;
            completorAdaptor = new JLineCompletorAdapterWrapper(getParser(), this);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Gets name of shell.
     */
    public String getName() {
        return name;
    }

    /**
     * Exec a shell command.
     */
    public CommandResult exec(String command) {
        logger.info("Executing '{}' in '{}' shell.", command, getName());
        
        try {
            addChars(command).newline();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
        waitForOutput();
        
        // FIXME: why isn't waitForOutput working?
        try { Thread.sleep(100); } catch (InterruptedException e) {}

        String outputText = getOutputText();
        Map<String, Object> commandOutput = getCommandOutput();
        @SuppressWarnings("rawtypes")
        Map<String, List> completorOutput = getCompletorOutput();

        if (hasError()) {
            logger.info("Command failed with error " + getError());
        } else {
            logger.info("Executed '{}'.  completorOutput='{}'  outputText='{}'  commandOutput='{}'", 
                        new Object[] { command, completorOutput, outputText, commandOutput });
        }
        
        return new CommandResult(outputText, commandOutput, completorOutput);
   }
    protected void addError(String string, Exception e) {
        if (e != null) {
            logger.error(string, e);
        } else {
            logger.error(string);
        }
        commandExecError = string;
        resultLatch.countDown();
    }

    public void addCompletorOutput(String buffer, List<?> candidates) {
        completorOutput.put(buffer, candidates);
        resultLatch.countDown();
    }

    @Override
    protected ExecutionStrategy getExecutionStrategy() {

        final ExecutionStrategy oldStrategy = super.getExecutionStrategy();
        return new ExecutionStrategy() {
            public Object execute(ParseResult parseResult) throws RuntimeException {
                Object obj = null;
                String command = null;
                try {
                    String method = parseResult.getMethod().getName();
                    String className = parseResult.getInstance().getClass().getCanonicalName();
                    command = className + "." + method;
                    long l1 = System.currentTimeMillis();
                    obj = oldStrategy.execute(parseResult);
                    long l2 = System.currentTimeMillis();
                    logger.debug("Completed execution of command " + command + " in " + (l2 - l1) + " ms.");
                    if (obj != null) {
                        logger.debug("Command output class is " + obj.getClass());
                    } else {
                        obj = "VOID";
                    }
                } catch (Exception e) {
                    addError("Error running command ", e);
                    throw new RuntimeException(e);
                }
                logger.debug("Adding outuut and notifying threads ..");
                addOutput(command, obj);
                if (command.equals(EXIT_SHELL_COMMAND)) {
                    resultLatch.countDown();
                }
                return obj;
            }

            public boolean isReadyForCommands() {
                return oldStrategy.isReadyForCommands();
            }

            public void terminate() {
                oldStrategy.terminate();
                logger.debug("Shell is exiting.");
            }
        };
    }

    @Override
    protected ConsoleReader createConsoleReader() {
        logger.debug("Returning wrapper reader -->" + newConsoleReader);
        return newConsoleReader;
    }

    /**
     * Following code is copied from JLineShell of spring shell to manipulate
     * consoleReader. JLineShell adds its own completorAdaptor.
     * 
     * Another addition is using of ThreadLocal for storing shell instead of
     * static singleton instance.
     */
    @Override
    public void run() {
        reader = createConsoleReader();

        setPromptPath(null);

        JLineLogHandler handler = new JLineLogHandler(reader, this);
        JLineLogHandler.prohibitRedraw(); // Affects this thread only

        // reader.addCompletor(new JLineCompletorAdapter(getParser()));
        reader.addCompletor(completorAdaptor);

        reader.setBellEnabled(true);
        if (Boolean.getBoolean("jline.nobell")) {
            reader.setBellEnabled(false);
        }

        // flashMessageRenderer();
        flash(Level.FINE, this.getProductName() + " " + this.getVersion(), Shell.WINDOW_TITLE_SLOT);
        printBannerAndWelcome();

        String startupNotifications = getStartupNotifications();
        if (StringUtils.hasText(startupNotifications)) {
            logger.info(startupNotifications);
        }

        setShellStatus(Status.STARTED);

        // Handle any "execute-then-quit" operation

        String rooArgs = System.getProperty("roo.args");
        if (rooArgs != null && !"".equals(rooArgs)) {
            setShellStatus(Status.USER_INPUT);
            boolean success = executeCommand(rooArgs);
            if (exitShellRequest == null) {
                // The command itself did not specify an exit shell code, so
                // we'll fall back to something sensible here
                executeCommand("quit"); // ROO-839
                exitShellRequest = success ? ExitShellRequest.NORMAL_EXIT : ExitShellRequest.FATAL_EXIT;
            }
            setShellStatus(Status.SHUTTING_DOWN);
        } else {
            // Normal RPEL processing
            promptLoop();
        }
        logger.debug("Exiting the shell");
    }

    public ShellEvent tab() throws IOException {
        return eis.tab();
    }

    public ShellEvent addChars(String seq) throws IOException {
        return eis.addChars(seq);
    }

    public ShellEvent addCtrlZ() throws IOException {
        return eis.addCtrlZ();
    }

    public ShellEvent addCtrlD() throws IOException {

        return eis.addCtrlD();
    }

    public ShellEvent newline() throws IOException {
        eis.newline();
        return end();
    }

    public synchronized ShellEvent end() {
        return eis.end();
    }

    public void eof() {
        eis.eof();
    }

    public void terminate() {
        closeShell();
    }

    @Override
    public void stop() {
        super.stop();
    }
    
    public String getOutputText() {
        return output.toString();
    }

    public String getPlainOutputText() { // saj
        StringBuilder strBldr = new StringBuilder();

        String tmpStr = output.toString();

        logger.debug("getOutputText shell string is:\n" + tmpStr); // saj
        int x = 0;
        int havePromptOnFront = 0;

        x = tmpStr.indexOf("\\\n");
        if (x != -1) { // remove possible continuation
            tmpStr = tmpStr.substring(x + 2);
        }

        x = tmpStr.indexOf(";\n");
        if (x != -1) { // remove command echo
            tmpStr = tmpStr.substring(x + 2);
        }

        x = tmpStr.indexOf("CommandResult [");
        if (x != -1) {
            x = tmpStr.indexOf(0x0a, x);
            if (x != -1) { // remove freaky CommandResult
                tmpStr = tmpStr.substring(x + 1);
            }
        }

        x = tmpStr.lastIndexOf(">"); // may be prompt on end
        if (x != -1 && x >= tmpStr.length() - 3) { // found prompt near end of
                                                   // output
            if (tmpStr.lastIndexOf(0x0a) != -1) {
                tmpStr = tmpStr.substring(0, tmpStr.lastIndexOf(0x0a));
            } else {
                tmpStr = "";
            }
        }

        String mask = "\\r";
        tmpStr = tmpStr.replaceAll(mask, "").trim();

        strBldr.append(tmpStr);

        tmpStr = getPresentationString(this)[0];

        x = tmpStr.indexOf("CommandResult [");
        if (x != -1) {
            x = x - 1;
            for (; x >= 0; x--) {
                char ch = tmpStr.charAt(x);
                if (!Character.isWhitespace(ch))
                    break;
            }
            tmpStr = tmpStr.substring(0, x + 1);
        }
        tmpStr = tmpStr.trim();
        strBldr.append(tmpStr);

        logger.debug("getOutputText result string is:\n" + strBldr.toString()); // saj
        if (strBldr.toString().indexOf("Cluster-1") != -1 || strBldr.toString().startsWith(">")) {
            logger.debug("getOutputText missed something:\n" + toHexString(output.toByteArray())); // saj
        }
        return strBldr.toString();
    }

    public static String toHexString(byte[] array) {
        char[] symbols = "0123456789ABCDEF".toCharArray();
        char[] hexValue = new char[array.length * 2];

        for (int i = 0; i < array.length; i++) {
            // convert the byte to an int
            int current = array[i] & 0xff;
            // determine the Hex symbol for the last 4 bits
            hexValue[i * 2 + 1] = symbols[current & 0x0f];
            // determine the Hex symbol for the first 4 bits
            hexValue[i * 2] = symbols[current >> 4];
        }
        return new String(hexValue);
    }

    /**
     * Clears shell after a test run.
     */
    public synchronized void clear() {
        clearEvents(); 
//        eof();
    }

    /**
     * Clears internal buffers and other variables.
     */
    public synchronized void clearEvents() {
        resultLatch = new CountDownLatch(1);
        commandExecError = null;
        eis.clearEvents();
        completorOutput.clear();
        commandOutput.clear();
        logger.debug("buffer before clear <" + reader.getCursorBuffer().toString() + ">");
        reader.getCursorBuffer().clearBuffer();
        logger.debug("buffer after clear <" + reader.getCursorBuffer().toString() + ">");
        output.reset();
        try {
            output.flush();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Map<String, Object> getCommandOutput() {
        return Collections.unmodifiableMap(commandOutput);
    }

    @SuppressWarnings("rawtypes")
    public Map<String, List> getCompletorOutput() {
        return Collections.unmodifiableMap(completorOutput);
    }

    public void addOutput(String command, Object object) {
        commandOutput.put(command, object);
        // resultLatch.countDown();
    }

    public void waitForOutput() {
        try {
            boolean completed = resultLatch.await(timeout, TimeUnit.SECONDS);
            if (!completed) {
                commandExecError = "Command Timeout Error ";
//                threadDump();
                logger.error(commandExecError);
            }
        } catch (InterruptedException e) {
            commandExecError = "Command InterruptedException Error ";
            logger.error(commandExecError);
        }
    }

    @Override
    protected void handleExecutionResult(Object result) {
        super.handleExecutionResult(result);
        resultLatch.countDown();
    }

    @Override
    protected Parser getParser() {
        return parserHook;
    }

    public boolean hasError() {
        return (commandExecError != null);
    }

    public String getError() {
        return commandExecError;
    }

    public static List<jline.Completion> autoComplete(TestableShell shell, String command) {
//        try {
//            logger.info("Executing auto-complete command " + command + " with command Mgr " + CommandManager.getInstance());
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        try {
            shell.addChars(command).addChars("\t").newline();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        shell.waitForOutput();
        if (shell.hasError()) {
            throw new RuntimeException("Command " + command + " failed with error " + shell.getError());
        } else {
            List<jline.Completion> completorOutput = shell.getCompletorOutput().get(command);
            String outputText = shell.getOutputText();
            Object commandOutput = shell.getCommandOutput();
            logger.info("Command completorOutput for " + command + ": " + completorOutput);
            logger.info("Command outputText for " + command + ": " + outputText);
            shell.clearEvents();
            return completorOutput;
        }
    }

    public static Object[] execAndLogCommand(TestableShell shell, String command, PrintWriter commandOutputFile,
            boolean useFineLevelForLogging) {
//        try {
//            logger.info("Executing command " + command + " with command Mgr " + CommandManager.getInstance());
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        try {
            shell.addChars(command).addChars(";").newline();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        shell.waitForOutput();
        if (shell.hasError()) {
            throw new RuntimeException("Command " + command + " failed with error " + shell.getError());
        } else {
            Map<String, List> completorOutput = shell.getCompletorOutput();
            String outputText = shell.getOutputText();
            Map<String, Object> commandOutput = shell.getCommandOutput();

            String[] tmpArr = getPresentationString(shell);
            String presentationStr = tmpArr[0];
            String errStr = tmpArr[1];
            if (!useFineLevelForLogging) {
                logger.info("Command completorOutput for " + command + ": " + completorOutput);
                logger.info("Command outputText for " + command + ": " + outputText);
                logger.info("Command output for " + command + ": " + commandOutput);
                logger.info("Presentation string for " + command + ":\n" + presentationStr);
            } else {
                logger.trace("Command completorOutput for " + command + ": " + completorOutput);
                logger.trace("Command outputText for " + command + ": " + outputText);
                logger.trace("Command output for " + command + ": " + commandOutput);
                logger.trace("Presentation string for " + command + ":\n" + presentationStr);
            }

            /*
             * CommandOutputValidator is doing this also issue is if command
             * contains ERROR below code will fail it. if (errStr.length() > 0)
             * { logger.info(errStr); // todo lynn; throw this
             * instead }
             * 
             * 
             * if ((presentationStr.indexOf("ERROR") >= 0) ||
             * (presentationStr.indexOf("Exception") >= 0)) { throw new
             * RuntimeException("Unexpected command output:\n" + presentationStr);
             * } //todo lynn - enable this when bug 45337 is fixed if
             * ((outputText.indexOf("ERROR") >= 0) ||
             * (outputText.indexOf("Exception") >= 0)) { throw new
             * RuntimeException("Unexpected output text:\n" + outputText); }
             */

            logCommandOutput(shell, commandOutputFile, command, presentationStr, errStr);
            commandOutput = (Map<String, Object>) copyMap(commandOutput);
            completorOutput = (Map<String, List>) copyMap(completorOutput);
            shell.clearEvents();
            return new Object[] { commandOutput, completorOutput, presentationStr, errStr };
        }
    }

    public static Object[] execCommand(TestableShell shell, String command, PrintWriter commandOutputFile) {
//        try {
//            logger.info("Executing command " + command + " with command Mgr " + CommandManager.getInstance());
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        try {
            shell.addChars(command).addChars(";").newline();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        shell.waitForOutput();
        if (shell.hasError()) {
            throw new RuntimeException("Command " + command + " failed with error " + shell.getError());
        } else {
            Map<String, List> completorOutput = shell.getCompletorOutput();
            String outputText = shell.getOutputText();
            Map<String, Object> commandOutput = shell.getCommandOutput();
            String[] tmpArr = getPresentationString(shell);
            String presentationStr = tmpArr[0];
            String errStr = tmpArr[1];

            commandOutput = (Map<String, Object>) copyMap(commandOutput);
            completorOutput = (Map<String, List>) copyMap(completorOutput);
            shell.clearEvents();
            return new Object[] { commandOutput, completorOutput, presentationStr, errStr };
        }
    }

    public static String[] getCommandOutputStrings(TestableShell shell) {
        if (hasResultObject(shell)) {
            return getPresentationString(shell);
        } else {
            String[] x = { shell.getOutputText(), "" };
            return x;
        }
    }

    private static boolean hasResultObject(TestableShell shell) {
        for (Object value : shell.getCommandOutput().values()) {
//            if (value instanceof CommandResult) {
//                CommandResult cr = (CommandResult) value;
//                try {
//                    logger.info("TestableShell saw ResultObject as follows: " + cr.getContent().toString());
//                    logger.info("                                    size: " + cr.getContent().size());
//                    logger.info("                                toString: " + cr.getContent().toString());
//                    logger.info(
//                            "                                   class: " + cr.getContent().getClass().getName());
//                    if (cr.getContent().size() == 0) {
//                        return false;
//                    } else {
//                        StringBuffer checkForOutput = new StringBuffer();
//                        while (cr.hasNextLine()) {
//                            String line = cr.nextLine();
//                            checkForOutput.append(line);
//                        }
//                        String tmpStr = checkForOutput.toString();
//                        if (tmpStr.trim().length() == 0) {
//                            return false;
//                        } else {
//                            return true;
//                        }
//                    }
//                } catch (Exception ex) {
//                }
//                return true;
//            }
        }

        return false;
    }

    private static String[] getPresentationString(TestableShell shell) {
        Map<String, Object> result = shell.getCommandOutput();
        StringBuffer presentationStr = new StringBuffer();
        StringBuffer errStr = new StringBuffer();
        for (Object key : result.keySet()) {
            Object value = result.get(key);
        }
        return new String[] { presentationStr.toString(), errStr.toString() };
    }

    private static String checkForRightPadding(String aStr) {
        logger.info("Checking for white space for line: \"" + aStr + "\"");
        String[] tokens = aStr.split("\n");
        StringBuffer errStr = new StringBuffer();
        for (String line : tokens) {
            // for (String )
            int whiteSpaceCount = 0;
            for (int i = line.length() - 1; i >= 0; i--) {
                char ch = line.charAt(i);
                if (Character.isWhitespace(ch)) {
                    whiteSpaceCount++;
                    if (i == 0) { // found a blank line
                        errStr.append("\"" + line + "\" contains " + whiteSpaceCount
                                + " white space characters on the right\n");
                    }
                } else { // found a non-white space character
                    if (whiteSpaceCount > 0) { // we previously found a
                                               // whitespace character
                        errStr.append("\"" + line + "\" contains " + whiteSpaceCount
                                + " white space characters on the right\n");
                    }
                    break;
                }
            }
        }
        return errStr.toString();
    }

    private static void logCommandOutput(TestableShell shell, PrintWriter commandOutputFile, String command,
            String output, String errStr) {
        StringBuffer sb = new StringBuffer();
        sb.append("Output (starts at beginnning of next line), output length is " + output.length() + ":\n" + output + "<--end of output\n");
        if (errStr.length() > 0) {
            sb.append(errStr + "\n");
        }
        sb.append("\n");
//        synchronized (this) {
            commandOutputFile.print(sb.toString());
            commandOutputFile.flush();
//        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object copyMap(Map map) {
        Map map2 = new HashMap();
        Set<Map.Entry> set = map.entrySet();
        for (Map.Entry e : set) {
            map2.put(e.getKey(), e.getValue());
        }
        return map2;
    }

}
