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
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.shell.support.util.FileUtils;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.shell.support.util.VersionUtils;
import org.springframework.stereotype.Component;

/**
 * Default Banner provider.
 *
 * @author Jarred Li
 * @author Costin Leau
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultBannerProvider implements BannerProvider {

	public String getBanner() {
		StringBuilder sb = new StringBuilder();
		sb.append(FileUtils.readBanner(DefaultBannerProvider.class, "banner.txt"));
		sb.append(getVersion()).append(OsUtils.LINE_SEPARATOR);
		sb.append(OsUtils.LINE_SEPARATOR);

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