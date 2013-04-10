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

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Abstract shell test.
 * 
 * @author David Winterfeldt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/shell-test-context.xml" })
public abstract class AbstractShellTest {

    final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private Bootstrap bootstrap;
    
    protected TestableShell shell;
    
    @Before
    public void init() {
        if (shell == null || !shell.isRunning()) {
            shell = (TestableShell) bootstrap.getJLineShellComponent();
    
            shell.start();
        }
    }
    	
	@After
	public void reset() {
	    shell.clear();
	}

}
