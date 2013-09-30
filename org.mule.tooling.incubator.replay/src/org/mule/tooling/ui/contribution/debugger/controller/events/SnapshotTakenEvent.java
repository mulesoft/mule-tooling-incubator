package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.core.event.EventType;
import org.mule.tooling.core.event.IEvent;

import com.mulesoft.mule.debugger.commons.MessageSnapshot;

public class SnapshotTakenEvent implements IEvent<ISnapshotTakenHandler> {

    private String name;
    private MessageSnapshot snapshot;

    public SnapshotTakenEvent(String name, MessageSnapshot snapshot) {
        super();
        this.name = name;
        this.snapshot = snapshot;
    }

    @Override
    public EventType<ISnapshotTakenHandler> getAssociatedType() {
        return SnapshotEventTypes.SNAPSHOT_TAKEN;
    }

    @Override
    public void dispatch(ISnapshotTakenHandler handler) {
        handler.onSnapshotTaken(name, snapshot);
    }

}