package org.mule.tooling.ui.contribution.munit.editors.custom.mappers;

import java.util.Map;

import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;

/**
 * <p>
 * Loader for the {@link PropertyCollectionMap} that comes from the XML, this is a general class used to retrieve collection of elements
 * </p>
 */
public class ItemCollectionMapperLoader extends MunitMockModuleMapper {

    private String itemIdentifier;
    private PropertyAgregator agregator;
    private PropertyMapper mapper;

    public ItemCollectionMapperLoader(String attributeIdentifier, PropertyAgregator agregator, PropertyMapper mapper) {
        this.itemIdentifier = attributeIdentifier;
        this.agregator = agregator;
        this.mapper = mapper;
    }

    @Override
    protected void actOn(MessageFlowNode node, Map<String, PropertyCollectionMap> propertyCollections, String property) {
        PropertyCollectionMap propertyCollectionMap = propertyCollections.get(property);
        agregator.agregate(propertyCollectionMap);
    }

    @Override
    protected boolean matchPropertyCondition(String property) {
        return property.contains(getSchema(itemIdentifier));
    }

    @Override
    protected void afterLoad(MessageFlowNode node, PropertyCollectionMap props) {
    }

    @Override
    public void mapTo(MessageFlowNode node, PropertyCollectionMap props) {
        mapper.mapTo(node, props);
    }
}
