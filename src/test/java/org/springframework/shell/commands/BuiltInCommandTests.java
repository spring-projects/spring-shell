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
package org.springframework.shell.commands;

import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import uk.co.it.modular.hamcrest.date.DateMatchers;

public class BuiltInCommandTests extends AbstractShellIntegrationTest {
	
	@Test
	public void dateTest() throws ParseException {
		
		//Execute command
		CommandResult cr = getShell().executeCommand("date");
		
		//Get result   
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL,Locale.US);
		Date result = df.parse(cr.getResult().toString());
		
		//Make assertions
		Date now = new Date();
		MatcherAssert.assertThat(now, DateMatchers.within(5, TimeUnit.SECONDS, result));		
	}
	
	@Test
	public void OsCommandTest() throws ParseException {
		
		String osName = System.getProperty("os.name").toLowerCase(Locale.US);
		String pathSep = System.getProperty("path.separator");
		boolean isUnix = pathSep.equals(":") && osName.endsWith("x");
		if (isUnix) {
			CommandResult cr = getShell().executeCommand("! ls /tmp");
			assertTrue(cr.isSuccess());
		}
		
	}
	
}
