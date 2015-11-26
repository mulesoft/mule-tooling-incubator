package org.mule.tooling.messaging.editor.global.booleanloaders;

import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;

import org.apache.commons.lang.StringUtils;

public class ProviderLoadedValueModifier extends Abstract3WayLoadedValueModifierTemplate {
    // Access
    public static final String PROVIDER_NAMESPACE = "http://www.mulesoft.org/schema/mule/anypoint-mq/provider";
    public static final String PROVIDER_STORE_ID_PREFIX = "@" + PROVIDER_NAMESPACE + ";";
    public static final String PROVIDER_REFERENCE_PROPERTY_ID = "provider-ref";
    public static final String PROVIDER_REFERENCE_BE_ID = "p-reference";
    public static final String PROVIDER_NESTED_BE_ID = "p-nested";
    
    protected Boolean haveNestedConfiguration(PropertyCollectionMap props) {
        return !haveGlobalRefConfiguration(props);
    }

    protected Boolean haveGlobalRefConfiguration(PropertyCollectionMap props) {
        String providerRef = props.getProperty(PROVIDER_REFERENCE_PROPERTY_ID, "");
        return StringUtils.isNotBlank(providerRef);
    }

    @Override
    protected String getNestedRadioBooleanId() {
        return PROVIDER_NESTED_BE_ID;
    }

    @Override
    protected String getGlobalRefRadioBooleanId() {
        return PROVIDER_REFERENCE_BE_ID;
    }

    @Override
    protected String getNoneConfigurationRadioBooleanId()
    {
        return "***invalid-option***";
    }

}
