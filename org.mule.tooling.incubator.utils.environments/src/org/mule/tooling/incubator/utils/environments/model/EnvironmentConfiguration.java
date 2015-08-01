package org.mule.tooling.incubator.utils.environments.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class EnvironmentConfiguration {
	
	private HashMap<String, Properties> environmentsConfiguration;
	
	public EnvironmentConfiguration() {
		environmentsConfiguration = new HashMap<String, Properties>();
	}
	
	public void addEnvironment(String name, Properties values) {
		environmentsConfiguration.put(name, values);
	}
	
	public List<EnvironmentConfigurationElement> elementsForKey(String key) {
		ArrayList<EnvironmentConfigurationElement> ret = new ArrayList<EnvironmentConfigurationElement>(environmentsConfiguration.size());
		
		for(String envName : environmentsConfiguration.keySet()) {
			Properties props = environmentsConfiguration.get(envName);
			ret.add(new EnvironmentConfigurationElement(key, props.getProperty(key, ""), envName));
		}
		
		return ret;
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

	public void updateConfigParts(List<EnvironmentConfigurationElement> currentConfiguration) {
		for(EnvironmentConfigurationElement elm : currentConfiguration) {
			Properties p = environmentsConfiguration.get(elm.getEnvironment());
			if (p == null) {
				System.out.println("Need to define what to do in this siutation");
				return;
			}
			if (p.containsKey(elm.getKey())) {
				p.setProperty(elm.getKey(), elm.getValue());
			}
		}
	}

	public HashMap<String, Properties> getEnvironmentsConfiguration() {
		return environmentsConfiguration;
	}
	
}
