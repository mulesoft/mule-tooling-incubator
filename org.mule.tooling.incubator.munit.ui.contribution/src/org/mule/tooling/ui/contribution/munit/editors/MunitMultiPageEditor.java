package org.mule.tooling.ui.contribution.munit.editors;

import org.eclipse.core.resources.IResourceChangeListener;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.messageflow.editor.MultiPageMessageFlowEditor;

/**
 * <p>
 * The {@link MultiPageMessageFlowEditor} for Munit, the only difference is that it creates a {@link MunitMessageFlowEditor}
 * <p>
 */
public class MunitMultiPageEditor extends MultiPageMessageFlowEditor implements IResourceChangeListener {

    public MunitMultiPageEditor() {
        setFlowEditor(createMessageFlowEditor());
    }

    @Override
    protected MessageFlowEditor createMessageFlowEditor() {
        return new MunitMessageFlowEditor();
    }

}
