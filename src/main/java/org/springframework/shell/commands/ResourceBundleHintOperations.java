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
package org.springframework.shell.commands;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.shell.core.AbstractShell;
import org.springframework.shell.support.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ResourceBundleHintOperations implements HintOperations {

	private static ResourceBundle bundle = ResourceBundle.getBundle(HintCommands.class.getName());
	
	public String hint(String topic) {
		if (StringUtils.isBlank(topic)) {
			topic = determineTopic();
		}
		try {
			String message = bundle.getString(topic);
			return message.replace("\r", StringUtils.LINE_SEPARATOR).replace("${completion_key}", AbstractShell.completionKeys);
		} catch (MissingResourceException exception) {
			return "Cannot find topic '" + topic + "'";
		}
	
}

	public SortedSet<String> getCurrentTopics() {
		SortedSet<String> result = new TreeSet<String>();
		String topic = determineTopic();
		if ("general".equals(topic)) {
			for (Enumeration<String> keys = bundle.getKeys(); keys.hasMoreElements();) {
				result.add(keys.nextElement());
			}
			// result.addAll(bundle.keySet()); ResourceBundle.keySet() method in JDK 6+
		} else {
			result.add(topic);
		}
		return result;
	}
	
	private String determineTopic() {
		return "start";
		//return "general";
	}
}