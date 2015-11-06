package org.mule.tooling.studio.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mule.tooling.editor.persistance.Utils;
import org.mule.tooling.studio.ui.StudioUIEditorPlugin;

/**
 * Studio Form Editor. TODO add XML View for 2 way editing
 */
public class StudioUIFormEditor extends FormEditor {

    private boolean isDirty = true;

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
    }

    @Override
    protected FormToolkit createToolkit(Display display) {
        // Create a toolkit that shares colors between editors.
        return new FormToolkit(StudioUIEditorPlugin.getDefault().getFormColors(display));
    }

    @Override
    protected void addPages() {
        try {
            addPage(new NamespacePage(this));
        } catch (PartInitException e) {
            //
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        isDirty = false;
        new Utils().serialize(((NamespacePage) this.getActivePageInstance()).getModel(), this.getEditorInput());
    }

    @Override
    public void doSaveAs() {

    }
    
    @Override
    public boolean isDirty() {
        //TODO add real logic to this
        return isDirty;
    }
    
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }
}