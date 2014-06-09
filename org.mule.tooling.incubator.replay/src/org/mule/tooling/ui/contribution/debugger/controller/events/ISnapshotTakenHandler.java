package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.core.event.IEventHandler;
import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDescriptor;

public interface ISnapshotTakenHandler extends IEventHandler {

    void onSnapshotTaken(MessageSnapshotDescriptor snapshot);
}