package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.core.event.EventType;
import org.mule.tooling.core.event.IEvent;

public class SnapshotRemovedEvent implements IEvent<ISnapshotRemovedHandler> {

    private String name;

    public SnapshotRemovedEvent(String name) {
        super();
        this.name = name;

    }

    @Override
    public EventType<ISnapshotRemovedHandler> getAssociatedType() {
        return SnapshotEventTypes.SNAPSHOT_REMOVED;
    }

    @Override
    public void dispatch(ISnapshotRemovedHandler handler) {
        handler.onSnapshotRemoved(name);
    }

}