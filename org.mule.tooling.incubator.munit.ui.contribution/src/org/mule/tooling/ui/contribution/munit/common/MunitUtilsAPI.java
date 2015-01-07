package org.mule.tooling.ui.contribution.munit.common;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.model.MuleProjectKind;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;
import org.mule.tooling.ui.contribution.munit.MunitResourceUtils;

/**
 * The Class is meant to be an static facade to expose a simpler Studio/Eclipse API
 * 
 * @author damiansima
 */
public class MunitUtilsAPI {

    /**
     * Returns the Mule project to which this file belongs.
     * 
     * @param file
     * @return null if the project of the file is not a Mule {@link MuleProjectKind.APPLICATION}
     * @throws CoreException
     */
    public static IMuleProject getMuleProject(IFile file) throws CoreException {
        IMuleProject muleProject = null;

        if (MuleProjectKind.APPLICATION.classifies(file.getProject())) {
            muleProject = MuleProjectKind.APPLICATION.adapt(file.getProject());
        }

        return muleProject;

        // option 1
        // muleProject = (IMuleProject) file.getProject().getAdapter(IMuleProject.class);
        // if (muleProject != null) {
        // return muleProject;
        // }
        //
        // option 2
        // muleProject = MuleRuntime.create(file.getProject());
        // if (muleProject != null) {
        // return muleProject;
        // }

    }

    public static IWorkspace getWorkspace(IFile file) {
        return file.getWorkspace();
    }

    public static IWorkspace getWorkspace(IMuleProject muleProject) {
        return muleProject.getJavaProject().getProject().getWorkspace();
    }

    /**
     * This method will try to configure the project to work properly with munit.
     * 
     * @param file
     * @throws CoreException
     */
    public static void configureProjectForMunit(IFile file) {
        try {
            IMuleProject muleProject = MunitUtilsAPI.getMuleProject(file);
            configureProjectForMunit(muleProject);
        } catch (CoreException e) {
            MunitPlugin.log(e);
        }
    }

    /**
     * This method will try to configure the project to work properly with munit.
     * 
     * @param file
     * @throws CoreException
     */
    public static void configureProjectForMunit(IMuleProject muleProject) {
        if (null == muleProject) {
            return;
        }

        if (!getWorkspace(muleProject).isTreeLocked()) {
            MunitResourceUtils.configureProjectForMunit(muleProject);
            return;
        }

        WorkspaceJob workspaceJob = buildConfigureMuleProjectJob(muleProject);
        workspaceJob.setRule(getWorkspace(muleProject).getRoot());
        workspaceJob.schedule();
    }

    private static WorkspaceJob buildConfigureMuleProjectJob(final IMuleProject muleProject) {
        return new WorkspaceJob("configure_project_for_munit") {

            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                MunitResourceUtils.configureProjectForMunit(muleProject);
                return Status.OK_STATUS;
            }
        };
    }
}
