/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.standard.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * Model encapsulating info about group and {@link CommandInfoModel}'s.
 *
 * @author Janne Valkealahti
 */
class GroupCommandInfoModel {

	private String group = "";
	private List<CommandInfoModel> commands = new ArrayList<>();

	GroupCommandInfoModel(String group, List<CommandInfoModel> commands) {
		this.group = group;
		this.commands = commands;
	}

	/**
	 * Builds {@link GroupCommandInfoModel}.
	 *
	 * @param group the group
	 * @param commands the command info models
	 * @return a group command info model
	 */
	static GroupCommandInfoModel of(String group, List<CommandInfoModel> commands) {
		return new GroupCommandInfoModel(group, commands);
	}

	public String getGroup() {
		return group;
	}

	public List<CommandInfoModel> getCommands() {
		return commands;
	}
}
