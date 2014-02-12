package org.mule.tooling.ui.contribution.munit.coverage;

import org.mule.tooling.core.event.IEventHandler;


public interface ICoverageUpdatedHandler extends IEventHandler{

    void onCoverageUpdated(CoverageReport report);
}
