package org.mule.tooling.devkit.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.mule.tooling.devkit.common.DevkitUtils;

public abstract class AbstractDevkitProjectWizzard extends Wizard {

    protected IProject getProjectWithDescription(String artifactId, IProgressMonitor monitor, IWorkspaceRoot root, IProjectDescription projectDescription) throws CoreException {
        IProject project = root.getProject(artifactId);
        if (!project.exists()) {
            project.create(projectDescription, monitor);
            project.open(monitor);
            project.setDescription(projectDescription, monitor);
        }
        return project;
    }
    
    protected String buildMainTargetFilePath(final String packageName, String className) {
        return DevkitUtils.MAIN_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + className + ".java";
    }


}
