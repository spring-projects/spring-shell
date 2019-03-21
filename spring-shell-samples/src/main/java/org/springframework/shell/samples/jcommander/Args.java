/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell.samples.jcommander;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

import javax.validation.constraints.Min;

/**
 * An example straight from the JCommander documentation.
 *
 * @author Eric Bottard
 * @author Cédric Beust
 */
public class Args {
	@Parameter
	private List<String> parameters = new ArrayList<>();

	@Min(3)
	@Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
	private Integer verbose = 1;

	@Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
	private String groups;

	@Parameter(names = "-debug", description = "Debug mode")
	private boolean debug = false;

	@Override
	public String toString() {
		return "Args{" +
			"parameters=" + parameters +
			", verbose=" + verbose +
			", groups='" + groups + '\'' +
			", debug=" + debug +
			'}';
	}
}
