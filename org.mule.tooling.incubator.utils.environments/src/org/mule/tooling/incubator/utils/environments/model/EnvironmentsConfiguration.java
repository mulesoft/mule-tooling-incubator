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
	private Set<String> newKeys;
	
	
	public EnvironmentsConfiguration(String environmentsPrefix, boolean malformed) {
		this.environmentsPrefix = environmentsPrefix;
		this.malformed = malformed;
		environmentsConfiguration = new HashMap<String, Properties>();
		newEnvironments = new HashSet<String>();
		newKeys = new HashSet<String>();
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
		
		boolean present = !values.isEmpty();
		
		if (newKeys.contains(key)) {
			present = true;
		}
		
		return new EnvironmentsSetting(key, present, values, new ArrayList<String>(environmentsConfiguration.keySet()));
	}
	
	public PropertyKeyTreeNode buildCombinedKeySet() {
		
		Set<String> buffer = new HashSet<String>(newKeys);
		
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
		
		//if the key is new, it should get removed given now it has some history
		if (newKeys.contains(setting.getKey())) {
			newKeys.remove(setting.getKey());
		}
		
	}

	public HashMap<String, Properties> getEnvironmentsConfiguration() {
		return environmentsConfiguration;
	}
	
	/**
	 * Deletes a given key and children keys
	 * @param prefix
	 */
	public void deleteKeys(PropertyKeyTreeNode key) {
		
		//generate the expansion of the keys
		Set<String> toDeleteKeys = key.collectKeys(new HashSet<String>());
		
		
		for(Properties props : environmentsConfiguration.values()) {
			for(String dk : toDeleteKeys) {
				if (props.containsKey(dk)) {
					props.remove(dk);
				}
			}
		}
		//the key might be a new key.
		for (String dk : toDeleteKeys) {
			if (newKeys.contains(dk)) {
				newKeys.remove(dk);
			}
		}
	}
	
	/**
	 * Create a new environment in this configuration and will be saved in a new file.
	 * @param suffix
	 */
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
	
	public void createNewKey(String key) {
		newKeys.add(key);
	}
	
	public void clearNewKeys() {
		newKeys.clear();
	}
}
