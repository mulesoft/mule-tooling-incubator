package org.mule.tooling.ui.contribution.debugger.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.mule.tooling.ui.contribution.debugger.controller.DebuggerEventTypes;
import org.mule.tooling.ui.contribution.debugger.controller.ReplayImages;
import org.mule.tooling.ui.contribution.debugger.controller.events.IDebuggerConnectedHandler;
import org.mule.tooling.ui.contribution.debugger.controller.events.IDebuggerDisconnectedHandler;
import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDecorator;
import org.mule.tooling.ui.contribution.debugger.service.MuleDebuggerService;
import org.mule.tooling.ui.contribution.debugger.view.IMuleSnapshotEditor;

public class ReplayFromMessageProcessorAction extends Action {

    private IMuleSnapshotEditor editor;
    public ReplayFromMessageProcessorAction(final IMuleSnapshotEditor editor) {
        super();
        this.editor = editor;
        setImageDescriptor(ReplayImages.getDebuggerImages().getImageDescriptor(ReplayImages.PLAY));
        setText("Replay From Message Processor");
        this.setEnabled(false);
        final MuleDebuggerService debuggerService = MuleDebuggerService.getDefault();
        editor.getSnapshotTable().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                setEnabled(checkCanBeEnabled(editor, debuggerService));

            }
        });

        debuggerService.getEventBus().registerUIThreadListener(DebuggerEventTypes.CONNECTED, new IDebuggerConnectedHandler() {

            @Override
            public void onConnected() {

                setEnabled(checkCanBeEnabled(editor, debuggerService));

            }
        });

        debuggerService.getEventBus().registerUIThreadListener(DebuggerEventTypes.DISCONNECTED, new IDebuggerDisconnectedHandler() {

            @Override
            public void onDisconnected() {
                setEnabled(checkCanBeEnabled(editor, debuggerService));
            }
        });

    }

    protected boolean checkCanBeEnabled(final IMuleSnapshotEditor editor, final MuleDebuggerService debuggerService) {
        return !editor.getSnapshotTable().getSelection().isEmpty() && debuggerService.getDebuggerClient() != null;
    }

    @Override
    public void run() {
        final IStructuredSelection selection = (IStructuredSelection) editor.getSnapshotTable().getSelection();
        final MessageSnapshotDecorator snapshotDescriptor = (MessageSnapshotDecorator) selection.getFirstElement();
        new ReplayFromProcessorCommand(snapshotDescriptor.getSnapshot()).execute();
    }

}