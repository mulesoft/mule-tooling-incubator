package org.mule.tooling.incubator.utils.environments.model;

public class EnvironmentConfigurationElement {
	
	private String key;
	private String value;
	private String environment;
	
	public EnvironmentConfigurationElement() {
	}
	
	
	public EnvironmentConfigurationElement(String key, String value, String environment) {
		super();
		this.key = key;
		this.value = value;
		this.environment = environment;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
}
