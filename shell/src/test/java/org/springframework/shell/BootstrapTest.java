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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.springframework.shell.core.JLineShellComponent;

public class BootstrapTest {

	@Test
	public void test() throws IOException {
		try {
			Bootstrap bootstrap = new Bootstrap(null);
			JLineShellComponent shell = bootstrap.getJLineShellComponent();
			
			//This is a brittle assertion - as additional 'test' commands are added to the suite, this number will increase.
			assertEquals("Number of CommandMarkers is incorrect", 5, shell.getSimpleParser().getCommandMarkers().size());
			assertEquals("Number of Converters is incorrect", 16, shell.getSimpleParser().getConverters().size());			
		} catch (RuntimeException t) {
			throw t;
		}
	}

}
