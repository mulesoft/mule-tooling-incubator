package org.mule.tooling.devkit.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution2;
import org.mule.tooling.devkit.DevkitImages;
import org.mule.tooling.devkit.builder.ProjectSubsetBuildAction;

public class FullBuild implements DevkitQuickFix, IMarkerResolution2 {

    @Override
    public boolean hasFixForMarker(IMarker marker) {
        String problem;
        try {
            problem = (String) marker.getAttribute(IMarker.MESSAGE);
            return problem.equals(org.mule.tooling.devkit.apt.Messages.CLEAN_BUILD_REQUIRED);
        } catch (CoreException e) {
            // Do nothing
        }

        return false;
    }

    @Override
    public String getLabel() {
        return "Run Clean Build";
    }

    @Override
    public void run(IMarker marker) {
        try {
            IResource resource = marker.getResource();

            IProject project = resource.getProject();
            ProjectSubsetBuildAction projectBuild = new ProjectSubsetBuildAction(new IShellProvider() {

                @Override
                public Shell getShell() {
                    return new Shell();
                }
            }, IncrementalProjectBuilder.CLEAN_BUILD, new IProject[] { project });
            marker.delete();
            projectBuild.run();

        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public String getDescription() {
        return "Launches a Clean build of the project";
    }

    @Override
    public Image getImage() {
        return DevkitImages.getManagedImage("", "build_exec.gif");
    }
}
