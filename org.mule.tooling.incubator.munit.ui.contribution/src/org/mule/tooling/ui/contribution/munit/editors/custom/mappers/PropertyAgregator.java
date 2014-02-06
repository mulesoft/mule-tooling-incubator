package org.mule.tooling.ui.contribution.munit.editors.custom.mappers;

import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;

/**
 * <p>
 * General interface to accumulative add properties into the {@link PropertyCollectionMap}
 * </p> 
 */
public interface PropertyAgregator {

	void agregate(PropertyCollectionMap map);
	
}
