package org.mule.tooling.incubator.utils.environments.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

public class EnvironmentsConfiguration {
	
	private HashMap<String, Properties> environmentsConfiguration;
	
	public EnvironmentsConfiguration() {
		environmentsConfiguration = new HashMap<String, Properties>();
	}
	
	public void addEnvironment(String name, Properties values) {
		environmentsConfiguration.put(name, values);
	}
	
	public EnvironmentsSetting elementsForKey(String key) {
		
		TreeMap<String, String> values = new TreeMap<String, String>();
		
		for(String envName : environmentsConfiguration.keySet()) {
			Properties props = environmentsConfiguration.get(envName);
			
			String value = props.getProperty(key, null);
			if (value != null) {
				values.put(envName, value);
			}
			
		}
		
		return new EnvironmentsSetting(key, !values.isEmpty(), values, new ArrayList<String>(environmentsConfiguration.keySet()));
	}
	
	public PropertyKeyTreeNode buildCombinedKeySet() {
		
		Set<String> buffer = new HashSet<String>();
		
		PropertyKeyTreeNode node = new PropertyKeyTreeNode(null, null);
		
		for(String envName : environmentsConfiguration.keySet()) {
			Properties props = environmentsConfiguration.get(envName);
			buffer.addAll(props.stringPropertyNames());
		}
		
		for(String key : buffer) {
			node.storeKey(key);
		}
		
		return node;
	}

	public void updateConfigParts(EnvironmentsSetting setting) {
		

		for(String envName : setting.getEnvironmentNames()) {
			Properties p = environmentsConfiguration.get(envName);
			if (p == null) {
				System.out.println("TODO - define what to do for new environment");
				continue;
			}
			
			if (setting.isPresent()) {
				p.setProperty(setting.getKey(), setting.getSettings().get(envName));
			} else {
				p.remove(setting.getKey());
			}
			
		}
		
	}

	public HashMap<String, Properties> getEnvironmentsConfiguration() {
		return environmentsConfiguration;
	}
	
}
