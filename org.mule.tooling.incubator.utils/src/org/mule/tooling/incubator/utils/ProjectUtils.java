package org.mule.tooling.incubator.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.mule.tooling.core.MuleRuntime;
import org.mule.tooling.core.model.IMuleProject;

public class ProjectUtils {
	
	/**
	 * Get a mule project from a general project.
	 * @param project
	 * @return
	 */
	public static IMuleProject safeGetMuleProjectFromProject(IProject project) {
		try {
			return MuleRuntime.create(project);
		} catch (CoreException e) {
			return null;
		}
	}
	
}
