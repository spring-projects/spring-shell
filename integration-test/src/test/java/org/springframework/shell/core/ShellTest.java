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
package org.springframework.shell.core;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.plugin.support.DefaultBannerProvider;


/**
 * Start shell jar test.
 */
public class ShellTest extends AbstractShellTest {
    
    final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Test
    public void testShellStart() throws IOException {
        String result = exec();
        
        String expectedMessage = new DefaultBannerProvider().getWelcomeMessage();
        
        logger.info("Verifying welcome banner is '{}'.", expectedMessage);
        
        assertTrue("Could not find welcome message.", result.indexOf(expectedMessage) != -1);
    }
    
}
