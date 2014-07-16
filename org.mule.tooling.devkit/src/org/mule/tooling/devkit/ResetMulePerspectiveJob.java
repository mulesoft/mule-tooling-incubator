package org.mule.tooling.devkit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.WorkbenchJob;
import org.mule.tooling.ui.MuleUIPlugin;

public final class ResetMulePerspectiveJob extends WorkbenchJob {

    ResetMulePerspectiveJob(String name) {
        super(name);
    }

    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null) {
            IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
            if (activePage != null) {
                IPerspectiveDescriptor[] openPerspectives = activePage.getOpenPerspectives();
                for (IPerspectiveDescriptor perspective : openPerspectives) {
                    if (perspective.getId().equals("org.mule.tooling.ui.toolingPerspective")) {
                        activePage.setPerspective(perspective);
                        activePage.resetPerspective();
                        return Status.OK_STATUS;
                    }
                }
            }
        }
        MuleUIPlugin.getDefault().getLog().log(new Status(IStatus.WARNING, DevkitUIPlugin.PLUGIN_ID, "Could not reset the Mule perspective during Devkit Plugin Update"));
        return Status.OK_STATUS;
    }
}