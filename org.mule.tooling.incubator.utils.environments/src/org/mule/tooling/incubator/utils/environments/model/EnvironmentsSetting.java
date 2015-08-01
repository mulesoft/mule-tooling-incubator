package org.mule.tooling.incubator.utils.environments.model;

import java.util.Map;
import java.util.List;

/**
 * Encapsulates a setting that spans through multiple environments.
 * @author juancavallotti
 *
 */
public class EnvironmentsSetting {
	private String key;
	private boolean present;
	private Map<String, String> settings;
	private List<String> environmentNames;
	
	public EnvironmentsSetting() {
	}
	
	public EnvironmentsSetting(String key, boolean present, Map<String, String> settings, List<String> environmentNames) {
		super();
		this.key = key;
		this.present = present;
		this.settings = settings;
		this.environmentNames = environmentNames;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isPresent() {
		return present;
	}
	public void setPresent(boolean present) {
		this.present = present;
	}
	public Map<String, String> getSettings() {
		return settings;
	}
	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	public List<String> getEnvironmentNames() {
		return environmentNames;
	}

	public void setEnvironmentNames(List<String> environmentNames) {
		this.environmentNames = environmentNames;
	}

	public String getForEnvironment(String envName) {
		return settings.get(envName);
	}
	
	public void setForEnvironment(String envName, String value) {
		settings.put(envName, value);
	}
	
}
