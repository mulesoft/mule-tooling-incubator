package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDecorator;
import org.mule.tooling.utils.eventbus.EventType;
import org.mule.tooling.utils.eventbus.IEvent;

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