/**
 * 
 */
package org.mule.tooling.ui.contribution.sap.widgets.meta;


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mule.tooling.model.messageflow.PropertyCollection;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.modules.core.widgets.meta.AbstractValuePersistence;

/**
 * @author mariano
 * 
 */
public class XmlDefinitionValuePersistence extends AbstractValuePersistence {

    /**
	 * 
	 */
    public XmlDefinitionValuePersistence() {
    }

    @Override
    public String getId(PropertyCollectionMap newProperties, PropertyCollectionMap parentProperties, String id) {
        String property = newProperties.getProperty("definition-xml", "");
        if (StringUtils.isEmpty(property)) {
            return null;
        }

        return "@http://www.mulesoft.org/schema/mule/sap/definition;1";
    }

    @Override
    public PropertyCollection adjust(List<PropertyCollection> defs2, String id) {
        for (PropertyCollection ca : defs2) {
            if (ca.getName().startsWith("@http://www.mulesoft.org/schema/mule/sap/definition")) {
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
