package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.core.event.IEventHandler;
import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDecorator;

public interface ISnapshotRemovedHandler extends IEventHandler {

    void onSnapshotRemoved(MessageSnapshotDecorator name);
}