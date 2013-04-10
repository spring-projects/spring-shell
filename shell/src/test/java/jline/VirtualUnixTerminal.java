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
package jline;

import java.io.IOException;
import java.io.InputStream;

import jline.UnixTerminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testable Shell implementation implementing ShellEvent interface.
 * 
 * @author tushark
 */
public class VirtualUnixTerminal extends UnixTerminal {
    
    final Logger logger = LoggerFactory.getLogger(getClass());
    
    public void initializeTerminal() throws IOException, InterruptedException {
        logger.info("Using virtual UnixTerminal");
    }

    /** 
     * Restore the original terminal configuration, which can be used when
     * shutting down the console reader. The ConsoleReader cannot be
     * used after calling this method.
     */
    public void restoreTerminal() throws Exception {
        /*if (ttyConfig != null) {
            stty(ttyConfig);
            ttyConfig = null;
        }*/
        resetTerminal();
    }

    public int readVirtualKey(InputStream in) throws IOException {
        int c = readCharacter(in);

        /*if (backspaceDeleteSwitched)
            if (c == DELETE)
                c = BACKSPACE;
            else if (c == BACKSPACE)
                c = DELETE;*/

        // in Unix terminals, arrow keys are represented by
        // a sequence of 3 characters. E.g., the up arrow
        // key yields 27, 91, 68
        if (c == ARROW_START && in.available() > 0) {
            // Escape key is also 27, so we use InputStream.available()
            // to distinguish those. If 27 represents an arrow, there
            // should be two more chars immediately available.
            while (c == ARROW_START) {
                c = readCharacter(in);
            }
            if (c == ARROW_PREFIX || c == O_PREFIX) {
                c = readCharacter(in);
                if (c == ARROW_UP) {
                    return CTRL_P;
                } else if (c == ARROW_DOWN) {
                    return CTRL_N;
                } else if (c == ARROW_LEFT) {
                    return CTRL_B;
                } else if (c == ARROW_RIGHT) {
                    return CTRL_F;
                } else if (c == HOME_CODE) {
                    return CTRL_A;
                } else if (c == END_CODE) {
                    return CTRL_E;
                } else if (c == DEL_THIRD) {
                    c = readCharacter(in); // read 4th
                    return DELETE;
                }
            } 
        } 
        // handle unicode characters, thanks for a patch from amyi@inf.ed.ac.uk
        if (c > 128) {
            replayStream.setInput(c, in);
            c = replayReader.read();
        }
        
        return c;
    } 
    
    public boolean isANSISupported() {
      return !Boolean.getBoolean("shell.disable.color");
    }

}