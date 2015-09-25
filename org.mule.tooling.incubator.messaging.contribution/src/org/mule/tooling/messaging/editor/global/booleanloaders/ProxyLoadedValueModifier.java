package org.mule.tooling.messaging.editor.global.booleanloaders;

import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class ProxyLoadedValueModifier extends Abstract3WayLoadedValueModifierTemplate {
    // PROXY
    public static final String PROXY_NAMESPACE = "http://www.mulesoft.org/schema/mule/http/proxy";
    public static final String PROXY_STORE_ID_PREFIX = "@" + PROXY_NAMESPACE + ";";
    public static final String PROXY_REFERENCE_PROPERTY_ID = "proxy-ref";
    public static final String PROXY_REFERENCE_BE_ID = "reference";
    public static final String PROXY_NESTED_BE_ID = "nested";
    public static final String PROXY_NONE_BE_ID = "include-nothing";
    
    protected Boolean haveNestedConfiguration(PropertyCollectionMap props) {
        Set<String> keySet = props.getPropertyCollections().keySet();
        for (String propertyCollectionName : keySet) {
            if (propertyCollectionName.startsWith(PROXY_STORE_ID_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    protected Boolean haveGlobalRefConfiguration(PropertyCollectionMap props) {
        String proxyRef = props.getProperty(PROXY_REFERENCE_PROPERTY_ID, "");
        return StringUtils.isNotBlank(proxyRef);
    }

    @Override
    protected String getNestedRadioBooleanId() {
        return PROXY_NESTED_BE_ID;
    }

    @Override
    protected String getGlobalRefRadioBooleanId() {
        return PROXY_REFERENCE_BE_ID;
    }

    @Override
    protected String getNoneConfigurationRadioBooleanId() {
        return PROXY_NONE_BE_ID;
    }
}
