package org.mule.tooling.devkit.builder;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.mule.tooling.devkit.common.ConnectorMavenModel;
import org.mule.tooling.devkit.template.TemplateFileWriter;
import org.mule.tooling.devkit.template.replacer.ClassReplacer;

public class ComponentFactory {

	private static final String CONFIGURATION_TEMPLATE = "/templates/connector_basic.tmpl";

	public static File createConfigurationStrategy(IProject project,
			ConnectorMavenModel model, String location, IProgressMonitor monitor)
			throws CoreException {

		TemplateFileWriter writer = new TemplateFileWriter(project,
				new NullProgressMonitor());
		writer.apply(CONFIGURATION_TEMPLATE, location, new ClassReplacer(model));
		
		return project.getFile(location).getRawLocation().toFile();
	}

}
