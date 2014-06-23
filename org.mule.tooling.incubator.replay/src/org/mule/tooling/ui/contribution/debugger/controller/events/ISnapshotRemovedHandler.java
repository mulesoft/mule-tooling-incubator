package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDecorator;
import org.mule.tooling.utils.eventbus.IEventHandler;

public interface ISnapshotRemovedHandler extends IEventHandler {

    void onSnapshotRemoved(MessageSnapshotDecorator name);
}