package org.mule.tooling.ui.contribution.debugger.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.mule.tooling.ui.contribution.debugger.controller.ReplayImages;
import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDecorator;
import org.mule.tooling.ui.contribution.debugger.service.MessageSnapshotService;
import org.mule.tooling.ui.contribution.debugger.view.IMuleSnapshotEditor;

public class DeleteMessageSnapshotAction extends Action {

    private IMuleSnapshotEditor snapshotEditor;
    private MessageSnapshotService service;

    public DeleteMessageSnapshotAction(IMuleSnapshotEditor snapshot, MessageSnapshotService service) {
        super();
        this.snapshotEditor = snapshot;
        this.service = service;
        setText("Delete Message Snapshot");
        setToolTipText("Delete Message Snapshot");
        setImageDescriptor(ReplayImages.getDebuggerImages().getImageDescriptor(ReplayImages.DELETE));
        setEnabled(false);
        snapshotEditor.getSnapshotTable().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                setEnabled(!event.getSelection().isEmpty());
            }
        });
    }

    @Override
    public void run() {
        final IStructuredSelection selection = (IStructuredSelection) snapshotEditor.getSnapshotTable().getSelection();
        final MessageSnapshotDecorator snapshot = (MessageSnapshotDecorator) selection.getFirstElement();
        service.removeSnapshot(snapshot.getName());
        service.store();
    }

}
