package org.mule.tooling.ui.contribution.munit.coverage;

import org.mule.tooling.utils.eventbus.IEventHandler;


/**
 * <p>
 * The handler of the {@link CoverageUpdatedEvent}
 * </p>
 */
public interface ICoverageUpdatedHandler extends IEventHandler {

    void onCoverageUpdated(CoverageReport report);
}
