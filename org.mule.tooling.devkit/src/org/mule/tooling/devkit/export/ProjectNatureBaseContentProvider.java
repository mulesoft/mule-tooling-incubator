package org.mule.tooling.devkit.export;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ProjectNatureBaseContentProvider implements IStructuredContentProvider {

    final private String natureId;

    public ProjectNatureBaseContentProvider(String natureId) {
        this.natureId = natureId;
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object[] getElements(Object inputElement) {
        IWorkspace workspace = (IWorkspace) inputElement;
        List<IProject> projects = new ArrayList<IProject>();
        for (IProject project : workspace.getRoot().getProjects()) {
            try {
                if (project.isAccessible() && project.hasNature(natureId)) {
                    projects.add(project);
                }
            } catch (CoreException e) {
                // Ignore error
            }
        }
        return projects.toArray();
    }

}
