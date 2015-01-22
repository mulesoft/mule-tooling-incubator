package org.mule.tooling.devkit;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

public class ProjectResourcesHandler implements IResourceChangeListener {

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        // final IResource closingProject = event.getResource();
        // Display.getDefault().asyncExec(new Runnable(){
        // public void run() {
        // for (IWorkbenchPage page : getSite().getWorkbenchWindow().getPages()) {
        // FileEditorInput editorInput = (FileEditorInput) MyEditor.this.getEditorInput();
        // if (editorInput.getFile().getProject().equals(closingProject))
        // page.closeEditor(page.findEditor(editorInput), true);
        // }
        // }
        // });
    }

}
