package org.mule.tooling.ui.contribution.debugger.view.actions;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.messageflow.editor.MultiPageMessageFlowEditor;
import org.mule.tooling.messageflow.editpart.EntityEditPart;
import org.mule.tooling.messageflow.util.MessageFlowUtils;
import org.mule.tooling.messageflow.util.MulePath;
import org.mule.tooling.model.messageflow.MuleConfiguration;
import org.mule.tooling.ui.contribution.debugger.service.MuleDebuggerService;

import com.mulesoft.mule.debugger.commons.MessageSnapshot;

public class ReplayFromProcessorCommand extends Command {

    private MessageSnapshot snapshot;

    protected ReplayFromProcessorCommand(MessageSnapshot snapshot) {
        super();
        this.snapshot = snapshot;
    }

    @Override
    public void execute() {
        final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            return;
        }
        final IEditorPart activeEditor = activeWorkbenchWindow.getActivePage().getActiveEditor();
        if (activeEditor instanceof MultiPageMessageFlowEditor) {

            String mpPath = null;
            String appName = null;

            final MultiPageMessageFlowEditor editor = (MultiPageMessageFlowEditor) activeEditor;
            final MessageFlowEditor messageFlowEditor = editor.getFlowEditor();
            final List<?> selectedEditParts = messageFlowEditor.getViewer().getSelectedEditParts();
            if (!selectedEditParts.isEmpty()) {
                final EntityEditPart<?> selectedEditPart = (EntityEditPart<?>) selectedEditParts.get(0);
                MuleConfiguration muleConfig = messageFlowEditor.getModelRoot().getEntity();
                IMuleProject muleProject = messageFlowEditor.getMuleProject();

                final MulePath path = MessageFlowUtils.findMulePathForEntity(messageFlowEditor.getMuleProject(), muleConfig, selectedEditPart.getEntity());
                if (path != null) {
                    mpPath = path.toString();
                    appName = muleProject.getName();
                }
            }
            if (mpPath == null) {
                mpPath = snapshot.getPath();
                appName = snapshot.getAppName();
            }

            MuleDebuggerService.getDefault().getDebuggerClient().replayFromMessageProcessor(snapshot, appName, mpPath);
        }
    }

    @Override
    public boolean canUndo() {
        return false;
    }

}