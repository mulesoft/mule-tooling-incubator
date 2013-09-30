package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.core.event.EventType;


public class SnapshotEventTypes {
    public static EventType<ISnapshotTakenHandler> SNAPSHOT_TAKEN = new EventType<ISnapshotTakenHandler>();
}
