package org.mule.tooling.studio.ui.editor;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.*;
import org.eclipse.ui.forms.widgets.*;
import org.mule.tooling.editor.model.Namespace;
import org.mule.tooling.studio.ui.StudioUIEditorPlugin;

public class NamespacePage extends FormPage {

    private NamespaceDetailsBlock block;

    public NamespacePage(FormEditor editor) {
        super(editor, "namespace.form.editor", Messages.getString("NamespacePage.label"));
        block = new NamespaceDetailsBlock(this);
    }

    protected void createFormContent(final IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        
        form.setText(Messages.getString("NamespacePage.title"));
        form.setBackgroundImage(StudioUIEditorPlugin.getDefault().getImage(StudioUIEditorPlugin.IMG_FORM_BG));
        block.createContent(managedForm);
    }
    

    public Namespace getModel() {
        // TODO Auto-generated method stub
        return block.getModel();
    }
}