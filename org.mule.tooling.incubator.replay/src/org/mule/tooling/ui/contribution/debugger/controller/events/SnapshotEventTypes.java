package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.core.event.EventType;


public class SnapshotEventTypes {
    public static EventType<ISnapshotTakenHandler> SNAPSHOT_TAKEN =  EventType.id("SNAPSHOT_TAKEN");
    
    public static EventType<ISnapshotRemovedHandler> SNAPSHOT_REMOVED = EventType.id("SNAPSHOT_REMOVED");
    
    public static EventType<ISnapshotClearedHandler> SNAPSHOT_CLEARED = EventType.id("SNAPSHOT_CLEARED");
}
