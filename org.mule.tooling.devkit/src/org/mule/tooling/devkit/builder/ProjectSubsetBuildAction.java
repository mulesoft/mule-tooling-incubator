package org.mule.tooling.devkit.builder;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.actions.BuildAction;


public class ProjectSubsetBuildAction extends BuildAction {

    private IProject[] projectsToBuild = new IProject[0];

    public ProjectSubsetBuildAction(IShellProvider shellProvider, int type, IProject[] projects) {
        super(shellProvider, type);
        this.projectsToBuild = projects;
    }

    @Override
    protected List<IProject> getSelectedResources() {
        return Arrays.asList(this.projectsToBuild);
    }
}