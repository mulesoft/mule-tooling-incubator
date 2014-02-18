package org.mule.tooling.ui.contribution.munit.common.visitors;

import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.Property;

/**
 * <p>
 * Utility class to read the name property from a {@link MessageFlowNode}
 * </p>
 * 
 * @param <T>
 *            The type of the message flow node.
 */
public class FlowNodeNameReader<T extends MessageFlowNode> {

    public String getNameFrom(T messageFlowNode) {
        if (messageFlowNode.getProperties() != null) {
            Property property = messageFlowNode.getProperties().getProperty("name");
            if (property != null) {
                return property.getValue();
            }
        }

        return null;
    }
}
