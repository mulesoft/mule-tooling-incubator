package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.core.event.EventType;
import org.mule.tooling.core.event.IEvent;

public class SnapshotClearedEvent implements IEvent<ISnapshotClearedHandler> {

    public SnapshotClearedEvent() {
        super();
    }

    @Override
    public EventType<ISnapshotClearedHandler> getAssociatedType() {
        return SnapshotEventTypes.SNAPSHOT_CLEARED;
    }

    @Override
    public void dispatch(ISnapshotClearedHandler handler) {
        handler.onSnapshotsCleared();
    }

}