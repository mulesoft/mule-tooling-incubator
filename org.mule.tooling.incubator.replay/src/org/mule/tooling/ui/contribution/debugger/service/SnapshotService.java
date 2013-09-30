package org.mule.tooling.ui.contribution.debugger.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.mule.tooling.core.event.EventBus;
import org.mule.tooling.ui.contribution.debugger.controller.events.SnapshotTakenEvent;

import com.mulesoft.mule.debugger.commons.MessageSnapshot;

public class SnapshotService {

    private Map<String, MessageSnapshot> snapshots;
    private EventBus eventBus;

    public SnapshotService(EventBus eventBus) {
        this.eventBus = eventBus;
        snapshots = new LinkedHashMap<String, MessageSnapshot>();
    }

    public Collection<MessageSnapshot> getSnapshots() {
        return snapshots.values();
    }

    public void addSnaphost(String name, MessageSnapshot snapshot) {
        this.snapshots.put(name, snapshot);
        eventBus.fireEvent(new SnapshotTakenEvent(name, snapshot));
    }
    
    public MessageSnapshot getSnapshot(String name){
        return snapshots.get(name);
    }

    public void removeSnapshot(String name) {
        this.snapshots.remove(name);
    }

    public Map<String, MessageSnapshot> getAllDefinedSnapshots() {
        return snapshots;
    }

}