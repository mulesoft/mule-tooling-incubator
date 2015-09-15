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
public class SAPTransactionValuePersistence extends AbstractValuePersistence {

    /**
	 * 
	 */
    public SAPTransactionValuePersistence() {
    }

    @Override
    public String getId(PropertyCollectionMap newProperties, PropertyCollectionMap parentProperties, String id) {
        String property = newProperties.getProperty("txType", "");
        if (property.equals("none")) {
            return null;
        }
        // Future support of Multi TX
        /*
         * if (property.equals("mr")){
         * 
         * return "@http://www.mulesoft.org/schema/mule/ee/core/multi-transaction;1"; }
         */
        return "@http://www.mulesoft.org/schema/mule/sap/transaction;1";
    }

    @Override
    public PropertyCollection adjust(List<PropertyCollection> defs2, String id) {
        for (PropertyCollection ca : defs2) {
            if (ca.getName().startsWith("@http://www.mulesoft.org/schema/mule/sap/transaction")) {
                Property property = new Property();
                property.setName("txType");
                property.setValue("sap");
                ca.getProperties().add(property);
                return ca;
            }
            // Future support of Multi TX
            /*
             * if (ca.getName().startsWith("@http://www.mulesoft.org/schema/mule/ee/core/multi-transaction")){ Property property = new Property(); property.setName("txType");
             * property.setValue("mr"); ca.getProperties().add(property); return ca; }
             */
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
