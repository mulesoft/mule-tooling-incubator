package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.core.event.IEventHandler;

public interface ISnapshotClearedHandler extends IEventHandler {

    void onSnapshotsCleared();
}