package org.mule.tooling.devkit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.devkit.builder.DevkitNature;

public class ProjectResourcesHandler implements IResourceChangeListener {

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        final IResource closingProject = event.getResource();
        try {
            if (closingProject.getProject().hasNature(DevkitNature.NATURE_ID)) {
                getDirtyEditors(closingProject.getProject());
            }
        } catch (CoreException e) {
            // Nothing to do
        }
    }

    public static void getDirtyEditors(IProject project) {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
        for (int i = 0; i < windows.length; i++) {
            IWorkbenchPage[] pages = windows[i].getPages();
            for (int x = 0; x < pages.length; x++) {
                IEditorReference[] editors = pages[x].getEditorReferences();
                for (int z = 0; z < editors.length; z++) {
                    IEditorReference ep = editors[z];
                    IEditorInput input;
                    try {
                        input = ep.getEditorInput();

                        if (input instanceof IFileEditorInput) {
                            IFileEditorInput fileInput = (IFileEditorInput) input;
                            IFile file = fileInput.getFile();
                            if (project != null && !(file.getProject() == project))
                                continue;
                            pages[x].closeEditor(ep.getEditor(false), false);
                        }
                    } catch (Exception e) {
                        // do nothing
                    }
                }
            }
        }
    }
}
