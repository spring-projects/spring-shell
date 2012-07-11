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
package org.springframework.shell.plugin.support;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.roo.shell.CommandMarker;
import org.springframework.roo.support.util.VersionUtils;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.stereotype.Component;

import static org.springframework.roo.support.util.StringUtils.*;

/**
 * Default Banner provider.
 * 
 * @author Jarred Li
 *
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultBannerProvider implements BannerProvider, CommandMarker {

	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	//@CliCommand(value = { "shell-version" }, help = "Displays shell version")
	public String getBanner() {
		StringBuilder sb = new StringBuilder();
		sb.append(" _____            _    ").append(LINE_SEPARATOR);
		sb.append("/  ___|          (_)").append(LINE_SEPARATOR);
		sb.append("\\ `--, _ __  _ __ _ _ __   __ _ ").append(LINE_SEPARATOR);
		sb.append(" `--. \\ '_ \\| '__| | '_ \\ / _` |").append(LINE_SEPARATOR);
		sb.append("/\\__/ / |_) | |  | | | | | (_| |").append(LINE_SEPARATOR);
		sb.append("\\____/| .__/|_|  |_|_| |_|\\__, |").append(LINE_SEPARATOR);
		sb.append("      | |                  __/ |").append(LINE_SEPARATOR);
		sb.append("      |_|                 |___/ ").append(" ").append(this.getVersion()).append(LINE_SEPARATOR);
		sb.append(LINE_SEPARATOR);

		return sb.toString();
	}


	public String getVersion() {
		return VersionUtils.versionInfo();
	}

	public String getWelcomeMessage() {
		return "Welcome to " + name() + ". For assistance press or type \"hint\" then hit ENTER.";
	}

	public String name() {
		return "Spring Shell";
	}
}