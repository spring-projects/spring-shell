/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.shell.table;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Rule;
import org.junit.rules.TestName;

import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

/**
 * Base class that allows reading a sample result rendering of a table, based on the actual
 * class and method name of the test.
 *
 * @author Eric Bottard
 */
public class AbstractTestWithSample {

	@Rule
	public TestName testName = new TestName();

	protected String sample() throws IOException {
		String sampleName = String.format("%s-%s.txt",
				this.getClass().getSimpleName(), testName.getMethodName());
		InputStream stream = TableTest.class.getResourceAsStream(sampleName);
		Assert.notNull(stream, "Can't find expected rendering result at " + sampleName);
		return FileCopyUtils.copyToString(new InputStreamReader(stream, "UTF-8")).replace("&", "");
	}

	/**
	 * Generate a simple rows x columns model made of chars.
	 */
	protected TableModel generate(int rows, int columns) {
		Character[][] data = new Character[rows][columns];
		for (int row = 0; row < rows; row++) {
			data[row] = new Character[columns];
			for (int column = 0; column < columns; column++) {
				data[row][column] = (char) ('a' + row * columns + column);
			}
		}
		return new ArrayTableModel(data);
	}

}
