package org.mule.tooling.ui.contribution.munit.listeners;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.model.MuleProjectKind;
import org.mule.tooling.ui.contribution.munit.MunitResourceUtils;
import org.mule.tooling.ui.contribution.munit.common.MunitUtilsAPI;

/**
 * The {@link PomResourceListener} listen for changes in the pom.xml files of {@link MuleProjectKind.APPLICATION}
 * 
 * @author damiansima
 */
public class PomResourceListener implements IResourceChangeListener {

    private static final String POM_FILE_NAME = "pom.xml";

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            if (event.getDelta() == null) {
                return;
            }

            event.getDelta().accept(new IResourceDeltaVisitor() {

                @Override
                public boolean visit(IResourceDelta delta) throws CoreException {
                    if (!(delta.getResource() instanceof IFile)) {
                        return true;
                    }

                    if (!(delta.getResource().getParent() instanceof IProject)) {
                        return true;
                    }

                    if (IResourceDelta.CHANGED == delta.getKind() || IResourceDelta.ADDED == delta.getKind()) {
                        IFile deltaResource = (IFile) delta.getResource();
                        onFileChanged(deltaResource);
                    }
                    return false;
                }
            });

        } catch (CoreException e) {
            // Activator.logError("Error while checking on resources", e);
        }
    }

    private void onFileChanged(final IFile file) throws CoreException {
        if (POM_FILE_NAME.equals(file.getName())) {
            MunitUtilsAPI.configureProjectForMunit(file);
        }
        return;
    }
}
