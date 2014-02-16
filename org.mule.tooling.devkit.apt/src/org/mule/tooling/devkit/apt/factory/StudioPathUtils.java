package org.mule.tooling.devkit.apt.factory;

import java.lang.reflect.Method;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.eclipse.core.resources.IFile;
import org.mule.devkit.apt.model.factory.PathUtils;

public class StudioPathUtils implements PathUtils {
	private ProcessingEnvironment env;

	public StudioPathUtils(ProcessingEnvironment env) {
		this.env = env;
	}

	@SuppressWarnings("restriction")
	@Override
	public String getSourceFilePath(Element element) {
		// env.g
		// Project[] projects =
		// ResourcesPlugin.getWorkspace().getRoot().getProjects();
		try {
			Method method = env.getClass().getMethod("getEnclosingIFile",
					Element.class);
			IFile file = (IFile) method.invoke(env, element);
				return file.getRawLocation().toString();
		} catch (Exception ex) {

		}
		return "";
	}
}
