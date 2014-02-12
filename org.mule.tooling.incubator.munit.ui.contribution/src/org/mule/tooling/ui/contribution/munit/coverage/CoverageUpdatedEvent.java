package org.mule.tooling.ui.contribution.munit.coverage;

import org.mule.tooling.core.event.EventType;
import org.mule.tooling.core.event.IEvent;


public class CoverageUpdatedEvent implements IEvent<ICoverageUpdatedHandler> {

    public static final EventType<ICoverageUpdatedHandler> MULE_MESSAGE_ARRIVED = EventType.id("COVERAGE_UPDATED");

    private CoverageReport report;

    public CoverageUpdatedEvent(CoverageReport report) {
        this.report = report;
    }

    @Override
    public EventType<ICoverageUpdatedHandler> getAssociatedType() {
        return MULE_MESSAGE_ARRIVED;
    }

    @Override
    public void dispatch(ICoverageUpdatedHandler handler) {
            handler.onCoverageUpdated(report);
    }

}
