package org.mule.tooling.ui.contribution.debugger.view.actions;

import java.io.File;
import java.util.concurrent.Callable;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.mule.tooling.incubator.replay.ReplayPlugin;
import org.mule.tooling.ui.contribution.debugger.controller.ReplayImages;
import org.mule.tooling.ui.contribution.debugger.service.MessageSnapshotService;
import org.mule.tooling.utils.SilentRunner;

public class ImportMessageSnapshotAction extends Action {

    private MessageSnapshotService snapshotService;

    public ImportMessageSnapshotAction(MessageSnapshotService snapshotService) {

        this.snapshotService = snapshotService;
        setImageDescriptor(ReplayImages.getDebuggerImages().getImageDescriptor(ReplayImages.IMPORT));
        setText("Import Message Snapshot");
        this.setEnabled(true);

    }

    @Override
    public void run() {

        final FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell());
        // dialog.setFilterExtensions(new String[] { ReplayPlugin.MSNAP_EXTENSION });
        final String file = dialog.open();
        if (file != null) {
            SilentRunner.run(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    final File snapshot = new File(file);
                    final String fullName = snapshot.getName();
                    final String name = fullName.substring(0, fullName.length() - ReplayPlugin.MSNAP_EXTENSION.length());
                    snapshotService.addSnaphost(name, MessageSnapshotService.load(snapshot));
                    snapshotService.store();
                    return null;
                }
            }, null);

        }

    }

}
