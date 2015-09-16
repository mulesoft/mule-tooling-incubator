package org.mule.tooling.devkit.component;

import java.util.HashMap;
import java.util.Map;

public class ComponentBuilderFactory {

    private Map<String, IComponentBuilder> builders;

    private ComponentBuilderFactory() {
        builders = new HashMap<String, IComponentBuilder>();
        builders.put("Configuration", new ConfigurationBuilder());
        builders.put("ConnectionManagement", new ConnectionManagementBuilder());
        builders.put("OAuth2", new OAuth2Builder());
        builders.put("MetaDataCategory", new MedaDataCategoryBuilder());
        builders.put("Handler", new HandlerBuilder());
        builders.put("Connector", new ConnectorBuilder());
        builders.put("ProviderAwarePagingDelegate", new DefaultBuilder());
    }

    // TODO: Improve
    public static IComponentBuilder getBuilder(String type) {
        ComponentBuilderFactory factory = new ComponentBuilderFactory();
        if (factory.contains(type))
            return new ComponentBuilderFactory().builders.get(type);
        else {
            return new DefaultBuilder();
        }
    }

    private boolean contains(String type) {
        return builders.containsKey(type);
    }
}
