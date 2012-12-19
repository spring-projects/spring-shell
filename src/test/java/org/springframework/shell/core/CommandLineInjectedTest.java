/*
 * Copyright 2011-2012 the original author or authors.
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


import java.io.IOException;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.commands.test.OptionsInjectedDummyCommand;
import org.springframework.shell.support.logging.HandlerUtils;

public class CommandLineInjectedTest {

	@Test
	public void commandLineInjected() throws IOException {
		try {
			Bootstrap bootstrap = new Bootstrap(null);
			AnnotationConfigApplicationContext ctx = bootstrap.getParentApplicationContext();
			OptionsInjectedDummyCommand dummyCommand = ctx.getBean(OptionsInjectedDummyCommand.class);
			Assert.assertNotNull("commandLine was not injected into a command", dummyCommand.getCommandLine());
		} catch (RuntimeException t) {
			throw t;
		} finally {
			HandlerUtils.flushAllHandlers(Logger.getLogger(""));
		}
	}
}
