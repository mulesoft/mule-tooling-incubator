package org.mule.tooling.incubator.devkit.test;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NewDevKitProjectTest {

	private IProject project;

	@Before
	public void setup() throws CoreException {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("Test");
		project.create(null);
		project.open(null);
	}

	@Test
	public void createGeneric() throws IOException, CoreException {
		//TODO add test;
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
