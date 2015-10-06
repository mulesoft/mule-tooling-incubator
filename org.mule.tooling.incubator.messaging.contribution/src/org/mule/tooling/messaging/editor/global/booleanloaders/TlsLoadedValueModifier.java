package org.mule.tooling.messaging.editor.global.booleanloaders;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;

public class TlsLoadedValueModifier extends Abstract3WayLoadedValueModifierTemplate {
    // TLS
    public static final String TLS_NAMESPACE = "http://www.mulesoft.org/schema/mule/tls/context";
    public static final String TLS_STORE_ID_PREFIX = "@" + TLS_NAMESPACE + ";";
    public static final String TLS_REFERENCE_PROPERTY_ID = "tlsContext-ref";
    public static final String TLS_REFERENCE_BE_ID = "reference";
    public static final String TLS_NESTED_BE_ID = "nested";
    public static final String TLS_NONE_BE_ID = "include-nothing";
    
    protected Boolean haveNestedConfiguration(PropertyCollectionMap props) {
        Set<String> keySet = props.getPropertyCollections().keySet();
        for (String propertyCollectionName : keySet) {
            if (propertyCollectionName.startsWith(TLS_STORE_ID_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    protected Boolean haveGlobalRefConfiguration(PropertyCollectionMap props) {
        String tlsContextRef = props.getProperty(TLS_REFERENCE_PROPERTY_ID, "");
        return StringUtils.isNotBlank(tlsContextRef);
    }

    @Override
    protected String getNestedRadioBooleanId() {
        return TLS_NESTED_BE_ID;
    }

    @Override
    protected String getGlobalRefRadioBooleanId() {
        return TLS_REFERENCE_BE_ID;
    }

    @Override
    protected String getNoneConfigurationRadioBooleanId() {
        return TLS_NONE_BE_ID;
    }
}
