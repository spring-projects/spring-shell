package org.springframework.shell.commands;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.roo.shell.AbstractShell;
import org.springframework.roo.support.util.StringUtils;
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