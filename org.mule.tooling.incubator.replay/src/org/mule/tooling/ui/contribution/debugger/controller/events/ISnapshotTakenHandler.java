package org.mule.tooling.ui.contribution.debugger.controller.events;

import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDecorator;
import org.mule.tooling.utils.eventbus.IEventHandler;

public interface ISnapshotTakenHandler extends IEventHandler {

    void onSnapshotTaken(MessageSnapshotDecorator snapshot);
}