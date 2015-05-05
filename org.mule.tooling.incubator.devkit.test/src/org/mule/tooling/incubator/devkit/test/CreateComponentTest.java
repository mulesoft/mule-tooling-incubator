package org.mule.tooling.incubator.devkit.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.tooling.devkit.common.ConnectorMavenModel;

public class CreateComponentTest {

	private IProject project;

	@Before
	public void setup() throws CoreException {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("Test");
		project.create(null);
		project.open(null);
	}

	@Test
	public void createConfiguration() throws IOException, CoreException {
		IProgressMonitor progress = new NullProgressMonitor();
		File expected = new File("resources/ConnectorConnectionStrategy.result");
		String file = "Strategy.java";
		ConnectorMavenModel connectorMavenModel = new ConnectorMavenModel();
		connectorMavenModel.setGenerateDefaultBody(true);
		connectorMavenModel.setConfigClassName("ConnectorConnectionStrategy");
		connectorMavenModel.setPackage("org.mule.modules.configuration");
		File generated = org.mule.tooling.devkit.builder.ComponentFactory
				.createConfigurationStrategy(project, connectorMavenModel,
						file, progress);
		assertTrue(FileUtils.contentEquals(expected, generated));
	}

	@After
	public void tearDown() {
		try {
			project.delete(true, new NullProgressMonitor());
		} catch (CoreException e) {
			// ignore this error.
		}
	}
}
