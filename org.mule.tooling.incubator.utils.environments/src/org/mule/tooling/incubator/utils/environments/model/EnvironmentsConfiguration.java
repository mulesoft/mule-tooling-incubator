package org.mule.tooling.incubator.utils.environments.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

public class EnvironmentsConfiguration {
	
	private final String environmentsPrefix;
	private final boolean malformed;
	private HashMap<String, Properties> environmentsConfiguration;
	private Set<String> newEnvironments;
	
	public EnvironmentsConfiguration(String environmentsPrefix, boolean malformed) {
		this.environmentsPrefix = environmentsPrefix;
		this.malformed = malformed;
		environmentsConfiguration = new HashMap<String, Properties>();
		newEnvironments = new HashSet<String>();
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
	
	public void createNewEnvironment(String suffix) {
		if (malformed) {
			return;
		}
		
		String propsFileName = buildFileName(suffix);
		
		if (environmentsConfiguration.containsKey(propsFileName)) {
			throw new IllegalStateException("The environment already has settings for the given suffix: " + suffix);
		}
		
		Properties props = new Properties();
		environmentsConfiguration.put(propsFileName, props);
		newEnvironments.add(propsFileName);
		
	}
	
	private String buildFileName(String suffix) {
		return environmentsPrefix + "-"+ suffix + ".properties";
	}

	public boolean canAddEnvironments() {
		return !malformed;
	}

	public Set<String> getNewEnvironments() {
		return newEnvironments;
	}

	public void clearNewEnvironments() {
		newEnvironments.clear();
	}
}
