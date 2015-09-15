package org.mule.tooling.ui.contribution.sap.widgets.meta;

import org.apache.commons.lang.StringUtils;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.modules.core.widgets.meta.AttributesPersistenceTransformer;

public class XmlDefinitionPersistenceTransformer implements AttributesPersistenceTransformer {

    @Override
    public void onLoad(final MessageFlowNode node, final PropertyCollectionMap props) {
        final String embedded = getXmlDefinition(props);
        final String file = props.getProperty("definitionFile", null);
        
        boolean isEmbedded = StringUtils.isNotEmpty(embedded);
        boolean isFile = !isEmbedded && StringUtils.isNotEmpty(file);
        boolean isPayload = !isEmbedded && !isFile;

        props.addProperty("payloadXML", Boolean.toString(isPayload));
        props.addProperty("embedXML", Boolean.toString(isEmbedded));
        props.addProperty("fileXML", Boolean.toString(isFile));
    }

    private String getXmlDefinition(final PropertyCollectionMap props) {
        for (String key : props.getPropertyCollections().keySet()) {
            if(key != null && key.startsWith("@http://www.mulesoft.org/schema/mule/sap/definition")) {
                PropertyCollectionMap definitionProperty = props.getPropertyCollections().get(key);
                if (definitionProperty != null) {
                    return definitionProperty.getProperty("definition-xml", null);
                }
            }
        }
        return null;
    }
    
    @Override
    public void onSave(final MessageFlowNode node, final PropertyCollectionMap props) {
        // do nothing
    }

}
