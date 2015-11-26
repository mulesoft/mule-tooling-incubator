package org.mule.tooling.messaging.editor.global;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mule.tooling.model.messageflow.Property;
import org.mule.tooling.model.messageflow.PropertyCollection;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.modules.core.widgets.meta.AbstractValuePersistence;

public final class PrefetchValuePersistence extends AbstractValuePersistence {
    public static final String AUXILIARY_PROPERTY_PREFIX = "auxiliary;";
    public static final String PREFETCH_NAMESPACE = "http://www.mulesoft.org/schema/mule/anypoint-mq/prefetch";
    public static final String PREFETCH_STORE_ID_PREFIX = "@" + PREFETCH_NAMESPACE + ";";
    
    @Override
    public String getId(final PropertyCollectionMap newProperties, PropertyCollectionMap parentProperties, String id) {
        List<Property> properties = newProperties.getPropertiesMap().asPropertyList();
        if (!getPersistenceProperties(properties).isEmpty()) {
            return getStoreId(id);
        }
        return null;
    }

    private List<String> getPersistenceProperties(List<Property> properties) {
        List<String> persistenceProperties = new ArrayList<>();
        for (Property property : properties) {
            if (!property.getName().startsWith(AUXILIARY_PROPERTY_PREFIX)) {
                persistenceProperties.add(property.getName());
            }
        }
        return persistenceProperties;
    }

    @Override
    public PropertyCollection adjust(final List<PropertyCollection> defs2, String id) {
        for (PropertyCollection propertyCollection : defs2) {
            if (propertyCollection.getName().startsWith(PREFETCH_STORE_ID_PREFIX)) {
                return propertyCollection;
            }
        }

        return new PropertyCollection();
    }

    private String getStoreId(String nodeId) {
        if (StringUtils.isNotBlank(nodeId)) {
            // generate id for nested element with ;0 since there should be only one of each (therefore ordering -the index- doesn't matter)
            return "@" + nodeId + ";" + 0;
        }
        return null;
    }
    
    @Override
    public String convertModelToXML(final String str) {
        return null;
    }

    @Override
    public String convertXMLToModel(final String str) {
        return null;
    }
}