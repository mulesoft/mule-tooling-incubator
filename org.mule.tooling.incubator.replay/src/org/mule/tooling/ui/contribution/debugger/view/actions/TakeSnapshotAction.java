package org.mule.tooling.ui.contribution.debugger.view.actions;

import org.eclipse.jface.action.Action;
import org.mule.tooling.ui.contribution.debugger.controller.DebuggerEventTypes;
import org.mule.tooling.ui.contribution.debugger.controller.ReplayImages;
import org.mule.tooling.ui.contribution.debugger.controller.events.IDebuggerConnectedHandler;
import org.mule.tooling.ui.contribution.debugger.controller.events.IDebuggerDisconnectedHandler;
import org.mule.tooling.ui.contribution.debugger.service.MuleDebuggerService;
import org.mule.tooling.ui.contribution.debugger.service.SnapshotService;

import com.mulesoft.mule.debugger.client.DebuggerClient;
import com.mulesoft.mule.debugger.client.DefaultDebuggerResponseCallback;
import com.mulesoft.mule.debugger.commons.MessageSnapshot;

public class TakeSnapshotAction extends Action {

    private SnapshotService service;

    public TakeSnapshotAction(SnapshotService service) {
        super();
        this.service = service;
        setImageDescriptor(ReplayImages.getDebuggerImages().getImageDescriptor(ReplayImages.SNAPSHOT));
        setToolTipText("Take Snapshot");
        setText("Take Snapshot");
        MuleDebuggerService debuggerService = MuleDebuggerService.getDefault();
        setEnabled(debuggerService.getDebuggerClient() != null);
        debuggerService.getEventBus().registerListener(DebuggerEventTypes.CONNECTED, new IDebuggerConnectedHandler() {

            @Override
            public void onConnected() {
                setEnabled(true);
            }
        });

        debuggerService.getEventBus().registerListener(DebuggerEventTypes.DISCONNECTED, new IDebuggerDisconnectedHandler() {

            @Override
            public void onDisconnected() {
                setEnabled(false);
            }
        });
    }

    @Override
    public void run() {
        DebuggerClient debuggerClient = MuleDebuggerService.getDefault().getDebuggerClient();
        debuggerClient.takeMessageSnapshot(new DefaultDebuggerResponseCallback() {

            @Override
            public void onMessageSnapshotTaken(MessageSnapshot snapshot) {
                service.addSnaphost("Snapshot" + service.getSnapshots().size(), snapshot);
            }

        });

    }

}