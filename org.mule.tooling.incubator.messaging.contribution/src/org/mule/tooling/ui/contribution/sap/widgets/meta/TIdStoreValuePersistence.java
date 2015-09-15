/**
 * 
 */
package org.mule.tooling.ui.contribution.sap.widgets.meta;


import java.util.List;

import org.mule.tooling.model.messageflow.Property;
import org.mule.tooling.model.messageflow.PropertyCollection;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.modules.core.widgets.meta.AbstractValuePersistence;

/**
 * @author mariano
 * 
 */
public class TIdStoreValuePersistence extends AbstractValuePersistence {

    /**
	 * 
	 */
    public TIdStoreValuePersistence() {
    }

    @Override
    public String getId(PropertyCollectionMap newProperties, PropertyCollectionMap parentProperties, String id) {
        String property = newProperties.getProperty("tidStoreType", "");
        if (property.equals("none")) {
            return null;
        }

        if (property.equals("mule-object-store")) {
            return "@http://www.mulesoft.org/schema/mule/sap/mule-object-store-tid-store;1";
        }

        // memory-store
        return "@http://www.mulesoft.org/schema/mule/sap/default-in-memory-tid-store;1";
    }

    @Override
    public PropertyCollection adjust(List<PropertyCollection> defs2, String id) {
        for (PropertyCollection ca : defs2) {
            if (ca.getName().startsWith("@http://www.mulesoft.org/schema/mule/sap/default-in-memory-tid-store")) {
                Property property = new Property();
                property.setName("tidStoreType");
                property.setValue("memory-store");
                ca.getProperties().add(property);
                return ca;
            }
            if (ca.getName().startsWith("@http://www.mulesoft.org/schema/mule/sap/mule-object-store-tid-store")) {
                Property property = new Property();
                property.setName("tidStoreType");
                property.setValue("mule-object-store");
                ca.getProperties().add(property);
                return ca;
            }
        }
        return new PropertyCollection();
    }

    @Override
    public String convertModelToXML(String str) {
        return null;
    }

    @Override
    public String convertXMLToModel(String str) {
        return null;
    }
}
