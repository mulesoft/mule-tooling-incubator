package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.core.event.IEventHandler;

import com.mulesoft.mule.debugger.commons.MessageSnapshot;

public interface ISnapshotTakenHandler extends IEventHandler {

    void onSnapshotTaken(String name, MessageSnapshot snapshot);
}