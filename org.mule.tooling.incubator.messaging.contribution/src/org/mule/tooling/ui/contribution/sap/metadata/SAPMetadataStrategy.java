package org.mule.tooling.ui.contribution.sap.metadata;

import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.Property;
import org.mule.tooling.model.messageflow.PropertyCollection;
import org.mule.tooling.model.messageflow.util.PropertiesUtils;
import org.mule.tooling.ui.modules.core.metadata.DefaultMetadataStrategy;
import org.mule.tooling.ui.modules.core.metadata.MetadataHelpers;


public class SAPMetadataStrategy extends DefaultMetadataStrategy {
    private static String ID = "http://www.mulesoft.org/schema/mule/sap/endpoint";
    private static boolean OVERRIDES_METADATA = true;
    private boolean forceRetrieveMetadata = false;
    
    public SAPMetadataStrategy() {
        super(OVERRIDES_METADATA);
    }

    @Override
    public String getId() {
        return ID;
    }

    
    public boolean isForceRetrieveMetadata() {
        return forceRetrieveMetadata;
    }

    
    public void setForceRetrieveMetadata(boolean forceRetrieveMetadata) {
        this.forceRetrieveMetadata = forceRetrieveMetadata;
    }
    
    @Override
    protected boolean shouldGetMetadata(PropertyCollection oldProperties, MessageFlowNode nodeWithNewProperties) {
        if (isForceRetrieveMetadata()) {
            return true;
        } else {
            boolean shouldGet = super.shouldGetMetadata(oldProperties, nodeWithNewProperties);
            
            final PropertyCollection newProperties = nodeWithNewProperties.getProperties();
            String typeChooserName = MetadataHelpers.getTypeAttributeName(nodeWithNewProperties);
            Property typeProperty = PropertiesUtils.findPropertyByName(newProperties, typeChooserName);

            return shouldGet || (typeProperty == null || !PropertiesUtils.isEmptyProperty(typeProperty));
        }
    }
}
