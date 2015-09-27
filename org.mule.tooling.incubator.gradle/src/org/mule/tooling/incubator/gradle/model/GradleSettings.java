package org.mule.tooling.incubator.gradle.model;

import java.util.List;

import org.eclipse.core.resources.IProject;

public class GradleSettings {
	
	private List<String> modules;
	
	private IProject project;
	
	public List<String> getModules() {
		return modules;
	}

	public void setModules(List<String> modules) {
		this.modules = modules;
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}
	
}
