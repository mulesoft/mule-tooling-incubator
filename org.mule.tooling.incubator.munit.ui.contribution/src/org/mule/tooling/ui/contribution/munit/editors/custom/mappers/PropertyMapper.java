package org.mule.tooling.ui.contribution.munit.editors.custom.mappers;

import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;

/**
 * <p>
 * Mapper for a single {@link PropertyCollectionMap}
 * </p> 
 */
public interface PropertyMapper {

	void mapTo(MessageFlowNode node, PropertyCollectionMap props);

}
