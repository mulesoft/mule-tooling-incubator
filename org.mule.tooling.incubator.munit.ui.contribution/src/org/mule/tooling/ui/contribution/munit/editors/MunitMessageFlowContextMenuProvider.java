package org.mule.tooling.ui.contribution.munit.editors;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IEditorPart;
import org.mule.tooling.messageflow.editor.MessageFlowContextMenuProvider;
import org.mule.tooling.messageflow.editor.MessageFlowNodeContextMenuProviderManager;
import org.mule.tooling.model.messageflow.MessageFlowNode;

/**
 * <p>
 * Munit menu for the production view in the Munit test editor.
 * </p>
 */
public class MunitMessageFlowContextMenuProvider extends MessageFlowContextMenuProvider {

    public MunitMessageFlowContextMenuProvider(IEditorPart editor, EditPartViewer viewer, ActionRegistry registry) {
        super(editor, viewer, registry);
    }

    @Override
    public void buildContextMenu(IMenuManager menu) {
        final MessageFlowNode selected = getSelectedNode();

        if (selected != null) {
            final MessageFlowNodeContextMenuProviderManager defaultMenuProvider = MessageFlowNodeContextMenuProviderManager.getDefault();
            defaultMenuProvider.addActionsForNode(menu, selected);
        }
    }

}
