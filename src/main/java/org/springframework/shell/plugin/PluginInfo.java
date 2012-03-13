package org.springframework.shell.plugin;

import java.util.LinkedList;
import java.util.List;

public class PluginInfo {

	private List<String> configClassNames = new LinkedList<String>();
	
	public void addConfigurationClassName(String configClassName) {
		this.configClassNames.add(configClassName);
		
	}
	
	public List<String> getConfigClassNames() {
		return this.configClassNames;
	}

}
