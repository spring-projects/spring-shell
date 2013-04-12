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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command results for <code>TestableShell</code>.
 * 
 * @author David Winterfeldt
 */
public class CommandResult {

    private final String outputText;
    private final Map<String, Object> commandOutput;
    private final Map<String, List<jline.Completion>> completorOutput;
    private final boolean errors;

    @SuppressWarnings("rawtypes")
    public CommandResult(String outputText, Map<String, Object> commandOutput,
                         Map<String, List<jline.Completion>> completorOutput,
                         boolean errors) {
        this.outputText = outputText;
        
        // copying so if values change/clear during processing this result is indepenent
        this.commandOutput = new HashMap<String, Object>(commandOutput);
        this.completorOutput = new HashMap<String, List<jline.Completion>>(completorOutput);
        
        this.errors = errors;
    }

    /**
     * Gets output text.
     */
    public String getOutputText() {
        return outputText;
    }

    /**
     * Gets command output <code>Map</code>.
     */
    public Map<String, Object> getCommandOutput() {
        return commandOutput;
    }

    /**
     * Gets completor output <code>Map</code>.
     */
    @SuppressWarnings("rawtypes")
    public Map<String, List<jline.Completion>> getCompletorOutput() {
        return completorOutput;
    }

    /**
     * Whether or not the command has any errors. 
     */
    public boolean hasErrors() {
        return errors;
    }
    
}
