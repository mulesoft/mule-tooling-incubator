package org.mule.tooling.ui.contribution.munit.editors.custom;

/**
 * <p>
 * The returned message properties of the "then-return" configuration for the mock:when message processor.
 * </p> 
 */
public class MockProperties {
	String name;
	String value;
	String type;
	
	public MockProperties(String name, String value, String type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	
}
