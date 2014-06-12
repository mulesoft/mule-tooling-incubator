package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.core.event.EventType;
import org.mule.tooling.core.event.IEvent;
import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDecorator;

public class SnapshotTakenEvent implements IEvent<ISnapshotTakenHandler> {

    private MessageSnapshotDecorator snapshot;

    public SnapshotTakenEvent( MessageSnapshotDecorator snapshot) {
        super();
        this.snapshot = snapshot;
        
    }

    @Override
    public EventType<ISnapshotTakenHandler> getAssociatedType() {
        return SnapshotEventTypes.SNAPSHOT_TAKEN;
    }

    @Override
    public void dispatch(ISnapshotTakenHandler handler) {
        handler.onSnapshotTaken(snapshot);
    }

}