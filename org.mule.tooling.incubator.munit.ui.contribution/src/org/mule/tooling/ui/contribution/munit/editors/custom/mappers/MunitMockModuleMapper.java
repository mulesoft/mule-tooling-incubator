package org.mule.tooling.ui.contribution.munit.editors.custom.mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;

/**
 * <p>
 * Abstract class to map elements from the XML for the Mock module of Munit
 * </p>
 */
public abstract class MunitMockModuleMapper {

    public static final String MUNIT_MOCK_SCHEMA = "@http://www.mulesoft.org/schema/mule/mock/%s";

    public static String getSchema(String itemIdentifier) {
        return String.format(MUNIT_MOCK_SCHEMA, itemIdentifier);
    }

    protected Collection<MunitMockModuleMapper> mappers = new ArrayList<MunitMockModuleMapper>();

    /**
     * The specific stuffs that a Mapper needs to do for a particular {@link PropertyCollectionMap}
     */
    protected abstract void actOn(MessageFlowNode node, Map<String, PropertyCollectionMap> propertyCollections, String property);

    /**
     * If the property can be processed by the mapper
     */
    protected abstract boolean matchPropertyCondition(String property);

    /**
     * Action to be perform after the {@link PropertyCollectionMap} is done.
     */
    protected abstract void afterLoad(MessageFlowNode node, PropertyCollectionMap props);

    /**
     * Action to map to {@link PropertyCollectionMap}
     */
    public abstract void mapTo(MessageFlowNode node, PropertyCollectionMap props);

    public void loadFrom(MessageFlowNode node, PropertyCollectionMap props) {
        Map<String, PropertyCollectionMap> propertyCollections = props.getPropertyCollections();
        for (String property : propertyCollections.keySet()) {
            if (matchPropertyCondition(property)) {
                loadInner(node, propertyCollections, property);
                actOn(node, propertyCollections, property);
            }
        }
        afterLoad(node, props);
    }

    protected void loadInner(MessageFlowNode node, Map<String, PropertyCollectionMap> propertyCollections, String property) {
        PropertyCollectionMap innerPropertyCollection = propertyCollections.get(property);
        for (MunitMockModuleMapper mapper : mappers) {
            mapper.loadFrom(node, innerPropertyCollection);
        }
    }

}
