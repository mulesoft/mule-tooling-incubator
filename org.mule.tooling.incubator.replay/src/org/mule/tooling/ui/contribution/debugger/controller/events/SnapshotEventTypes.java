package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.core.event.EventType;


public class SnapshotEventTypes {
    public static EventType<ISnapshotTakenHandler> SNAPSHOT_TAKEN = new EventType<ISnapshotTakenHandler>();
    
    public static EventType<ISnapshotRemovedHandler> SNAPSHOT_REMOVED = new EventType<ISnapshotRemovedHandler>();
    
    public static EventType<ISnapshotClearedHandler> SNAPSHOT_CLEARED = new EventType<ISnapshotClearedHandler>();
}
