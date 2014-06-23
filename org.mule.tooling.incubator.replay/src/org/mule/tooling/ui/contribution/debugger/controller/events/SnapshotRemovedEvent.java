package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDecorator;
import org.mule.tooling.utils.eventbus.EventType;
import org.mule.tooling.utils.eventbus.IEvent;

public class SnapshotRemovedEvent implements IEvent<ISnapshotRemovedHandler> {

    private MessageSnapshotDecorator name;

    public SnapshotRemovedEvent(MessageSnapshotDecorator name) {
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