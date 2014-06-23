package org.mule.tooling.ui.contribution.munit.coverage;

import org.mule.tooling.utils.eventbus.EventBus;
import org.mule.tooling.utils.eventbus.EventType;
import org.mule.tooling.utils.eventbus.IEvent;


/**
 * <p>
 * {@link EventBus} event, it notifies that the Coverage for a Munit suite was updated
 * </p>
 */
public class CoverageUpdatedEvent implements IEvent<ICoverageUpdatedHandler> {

    public static final EventType<ICoverageUpdatedHandler> SUITE_COVERAGE_UPDATED = EventType.id("COVERAGE_UPDATED");

    private CoverageReport report;

    public CoverageUpdatedEvent(CoverageReport report) {
        this.report = report;
    }

    @Override
    public EventType<ICoverageUpdatedHandler> getAssociatedType() {
        return SUITE_COVERAGE_UPDATED;
    }

    @Override
    public void dispatch(ICoverageUpdatedHandler handler) {
        handler.onCoverageUpdated(report);
    }

}
