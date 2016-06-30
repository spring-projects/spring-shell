/*
 * Copyright 2013 the original author or authors.
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
package org.springframework.shell.samples.hiworld.commands;

import org.junit.Test;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.samples.helloworld.AppConfig;

import static org.junit.Assert.assertEquals;

/**
 * Test the Spring Java-based Configuration ({@link AppConfig})
 * has scanned {@link HiWorldCommands} and the "hi" command
 * can be executed successfully
 *
 * @author Robin Howlett
 */
public class HiWorldCommandTests {

    @Test
    public void testHiAndHello() {
        Bootstrap bootstrap = new Bootstrap(null, Bootstrap.CONTEXT_PATH, AppConfig.class.getPackage().getName());

        JLineShellComponent shell = bootstrap.getJLineShellComponent();

        CommandResult cr = shell.executeCommand("hi");
        assertEquals(true, cr.isSuccess());
        assertEquals("Hi World!", cr.getResult());
    }

}
