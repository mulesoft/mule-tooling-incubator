package org.mule.tooling.incubator.gradle.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="dependencies")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class StudioDepencencies {
	
	
	private List<StudioDependency> dependencies;

	@XmlElement(name="dependency")
	public List<StudioDependency> getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(List<StudioDependency> dependencies) {
		this.dependencies = dependencies;
	}
	
}
