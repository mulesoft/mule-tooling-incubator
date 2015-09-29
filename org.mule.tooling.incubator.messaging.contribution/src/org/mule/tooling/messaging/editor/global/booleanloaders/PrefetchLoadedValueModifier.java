package org.mule.tooling.messaging.editor.global.booleanloaders;

import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class PrefetchLoadedValueModifier extends Abstract3WayLoadedValueModifierTemplate {
    // Prefetched
    public static final String PREFETCH_NAMESPACE = "http://www.mulesoft.org/schema/mule/mq/prefetched";
    public static final String PREFETCH_STORE_ID_PREFIX = "@" + PREFETCH_NAMESPACE + ";";
    public static final String PREFETCH_REFERENCE_PROPERTY_ID = "prefetch-ref";
    public static final String PREFETCH_REFERENCE_BE_ID = "reference";
    public static final String PREFETCH_NESTED_BE_ID = "nested";
    public static final String PREFETCH_NONE_BE_ID = "include-nothing";

    protected Boolean haveNestedConfiguration(PropertyCollectionMap props) {
        Set<String> keySet = props.getPropertyCollections().keySet();
        for (String propertyCollectionName : keySet) {
            if (propertyCollectionName.startsWith(PREFETCH_STORE_ID_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    protected Boolean haveGlobalRefConfiguration(PropertyCollectionMap props) {
        String tlsContextRef = props.getProperty(PREFETCH_REFERENCE_PROPERTY_ID, "");
        return StringUtils.isNotBlank(tlsContextRef);
    }

    @Override
    protected String getNestedRadioBooleanId() {
        return PREFETCH_NESTED_BE_ID;
    }

    @Override
    protected String getGlobalRefRadioBooleanId() {
        return PREFETCH_REFERENCE_BE_ID;
    }

    @Override
    protected String getNoneConfigurationRadioBooleanId() {
        return PREFETCH_NONE_BE_ID;
    }

}
