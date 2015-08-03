package org.mule.tooling.properties.extension;

public class PropertyKeySuggestion {
	
	private String suggestion;
	private String description;
	private String defaultValue;
	
	public PropertyKeySuggestion() {
	}

	public PropertyKeySuggestion(String suggestion, String description,
			String defaultValue) {
		super();
		this.suggestion = suggestion;
		this.description = description;
		this.defaultValue = defaultValue;
	}

	public String getSuggestion() {
		return suggestion;
	}
	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((suggestion == null) ? 0 : suggestion.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyKeySuggestion other = (PropertyKeySuggestion) obj;
		if (suggestion == null) {
			if (other.suggestion != null)
				return false;
		} else if (!suggestion.equals(other.suggestion))
			return false;
		return true;
	}

	
	
	
}
