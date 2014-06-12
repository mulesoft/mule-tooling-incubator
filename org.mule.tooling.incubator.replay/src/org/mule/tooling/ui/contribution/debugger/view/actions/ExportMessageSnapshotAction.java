package org.mule.tooling.ui.contribution.debugger.view.actions;

import java.io.File;
import java.util.concurrent.Callable;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.mule.tooling.incubator.replay.ReplayPlugin;
import org.mule.tooling.ui.contribution.debugger.controller.ReplayImages;
import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDecorator;
import org.mule.tooling.ui.contribution.debugger.service.MessageSnapshotService;
import org.mule.tooling.ui.contribution.debugger.view.IMuleSnapshotEditor;
import org.mule.tooling.ui.widgets.util.SilentRunner;

public class ExportMessageSnapshotAction extends Action {

    private IMuleSnapshotEditor editor;

    public ExportMessageSnapshotAction(final IMuleSnapshotEditor editor) {
        this.editor = editor;
        setImageDescriptor(ReplayImages.getDebuggerImages().getImageDescriptor(ReplayImages.EXPORT));
        setText("Export Message Snapshot");
        this.setEnabled(false);
        editor.getSnapshotTable().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                setEnabled(!event.getSelection().isEmpty());
            }
        });

    }

    @Override
    public void run() {
        final IStructuredSelection selection = (IStructuredSelection) editor.getSnapshotTable().getSelection();
        final MessageSnapshotDecorator messageSnapshotDecorator = (MessageSnapshotDecorator) selection.getFirstElement();
        final DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
        final String dir = dialog.open();
        if (dir != null) {
            SilentRunner.run(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    MessageSnapshotService.store(messageSnapshotDecorator.getSnapshot(), new File(dir, messageSnapshotDecorator.getName() + ReplayPlugin.MSNAP_EXTENSION));
                    return null;
                }
            }, null);

        }

    }

}
