package org.mule.tooling.ui.contribution.debugger.view.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.mule.tooling.ui.contribution.debugger.controller.ReplayImages;
import org.mule.tooling.ui.contribution.debugger.service.SnapshotService;
import org.mule.tooling.ui.contribution.debugger.view.IMuleSnapshotEditor;
import org.mule.tooling.ui.contribution.debugger.view.impl.CreateSnapshotDialog;

import com.mulesoft.mule.debugger.commons.MessageSnapshot;

public class DuplicateSnapshotAction extends Action {

    private IMuleSnapshotEditor snapshotEditor;
    private SnapshotService service;

    public DuplicateSnapshotAction(IMuleSnapshotEditor snapshot, SnapshotService service) {
        super();
        this.snapshotEditor = snapshot;
        this.service = service;
        setText("Duplicate");
        setToolTipText("Duplicate");
        setImageDescriptor(ReplayImages.getDebuggerImages().getImageDescriptor(ReplayImages.DUPLICATE));
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
        IStructuredSelection selection = (IStructuredSelection) snapshotEditor.getSnapshotTable().getSelection();
        MessageSnapshot snapshot = service.getSnapshot(String.valueOf(selection.getFirstElement()));
        if (snapshot != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = null;
            try {
                objectOutputStream = new ObjectOutputStream(out);
                objectOutputStream.writeObject(snapshot);
                objectOutputStream.flush();
            } catch (Exception e) {

            } finally {
                if (objectOutputStream != null) {
                    try {
                        objectOutputStream.close();
                    } catch (IOException e) {

                    }
                }
            }

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());
            MessageSnapshot readObject;
            try {
                readObject = (MessageSnapshot) new ObjectInputStream(byteArrayInputStream).readObject();
                CreateSnapshotDialog dialog = new CreateSnapshotDialog(Display.getDefault().getActiveShell());
                int open = dialog.open();
                if (Dialog.OK == open) {
                    service.addSnaphost(dialog.getName(), readObject);
                }
            } catch (IOException e) {

            } catch (ClassNotFoundException e) {

            }
        }

    }

}