package org.mule.tooling.messaging.editor.global.booleanloaders;

import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class AccessLoadedValueModifier extends Abstract3WayLoadedValueModifierTemplate {
    // Acces
    public static final String ACCESS_NAMESPACE = "http://www.mulesoft.org/schema/mule/mq/access";
    public static final String ACCESS_STORE_ID_PREFIX = "@" + ACCESS_NAMESPACE + ";";
    public static final String ACCESS_REFERENCE_PROPERTY_ID = "access-ref";
    public static final String ACCESS_REFERENCE_BE_ID = "reference";
    public static final String ACCESS_NESTED_BE_ID = "nested";
    
    protected Boolean haveNestedConfiguration(PropertyCollectionMap props) {
        Set<String> keySet = props.getPropertyCollections().keySet();
        for (String propertyCollectionName : keySet) {
            if (propertyCollectionName.startsWith(ACCESS_STORE_ID_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    protected Boolean haveGlobalRefConfiguration(PropertyCollectionMap props) {
        String tlsContextRef = props.getProperty(ACCESS_REFERENCE_PROPERTY_ID, "");
        return StringUtils.isNotBlank(tlsContextRef);
    }

    @Override
    protected String getNestedRadioBooleanId() {
        return ACCESS_NESTED_BE_ID;
    }

    @Override
    protected String getGlobalRefRadioBooleanId() {
        return ACCESS_REFERENCE_BE_ID;
    }

    @Override
    protected String getNoneConfigurationRadioBooleanId()
    {
        return null;
    }

}
