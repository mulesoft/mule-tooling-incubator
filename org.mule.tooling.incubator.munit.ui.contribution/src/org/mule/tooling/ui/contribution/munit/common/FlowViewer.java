package org.mule.tooling.ui.contribution.munit.common;

import org.mule.tooling.model.messageflow.Flow;

/**
 * <p>
 * Common interface that must be implemented for those graphical viewers that exposes mule code
 * </p>
 */
public interface FlowViewer {

    /**
     * <p>
     * Shows all the flows that match with filters
     * </p>
     * 
     * @param filters
     *            <p>
     *            All the filters that must be satisfied by the shown flows
     *            </p>
     */
    void show(Filter<Flow>... filters);

    /**
     * <p>
     * Reverts the flow selection done by {@link FlowViewer#show(FlowFilter...)}
     * </p>
     */
    void showAll();
}
