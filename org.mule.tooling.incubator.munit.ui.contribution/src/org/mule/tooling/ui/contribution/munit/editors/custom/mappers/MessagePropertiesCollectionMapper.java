package org.mule.tooling.ui.contribution.munit.editors.custom.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.contribution.munit.editors.custom.MockProperties;

/**
 * <p>
 * Class that maps the properties that a mock message processor must return.
 * </p>
 */
public class MessagePropertiesCollectionMapper extends MunitMockModuleMapper {

    private String propertiesIdentifier;

    public static MessagePropertiesCollectionMapper invocationPropertiesLoader(List<MockProperties> properties) {
        return new MessagePropertiesCollectionMapper("invocation-properties", "invocation-property", new MockAgregator("INVOCATION", properties), new MockMapper("INVOCATION",
                properties, "invocation-property"));
    }

    public static MessagePropertiesCollectionMapper outboundPropertiesLoader(List<MockProperties> properties) {
        return new MessagePropertiesCollectionMapper("outbound-properties", "outbound-property", new MockAgregator("OUTBOUND", properties), new MockMapper("OUTBOUND", properties,
                "outbound-property"));
    }

    public static MessagePropertiesCollectionMapper inboundPropertiesLoader(List<MockProperties> properties) {
        return new MessagePropertiesCollectionMapper("inbound-properties", "inbound-property", new MockAgregator("INBOUND", properties), new MockMapper("INBOUND", properties,
                "inbound-property"));
    }

    public MessagePropertiesCollectionMapper(String propertiesIndentifier, String propertyIdentifier, PropertyAgregator agregator, MockMapper mockMapper) {
        propertiesIdentifier = propertiesIndentifier;
        mappers = new ArrayList<MunitMockModuleMapper>();
        mappers.add(new ItemCollectionMapperLoader(propertyIdentifier, agregator, mockMapper));
    }

    @Override
    protected void actOn(MessageFlowNode node, Map<String, PropertyCollectionMap> propertyCollections, String property) {
    }

    @Override
    protected boolean matchPropertyCondition(String property) {
        return property.contains(getSchema(propertiesIdentifier));
    }

    @Override
    protected void afterLoad(MessageFlowNode node, PropertyCollectionMap props) {
    }

    private static class MockAgregator implements PropertyAgregator {

        String type;
        List<MockProperties> properties;

        public MockAgregator(String type, List<MockProperties> properties) {
            this.type = type;
            this.properties = properties;
        }

        @Override
        public void agregate(PropertyCollectionMap map) {
            properties.add(new MockProperties(map.getProperty("key", ""), map.getProperty("value-ref", ""), type));
        }

    }

    private static class MockMapper implements PropertyMapper {

        private String type;
        private List<MockProperties> properties;
        private String identifier;

        public MockMapper(String type, List<MockProperties> properties, String identifier) {
            this.type = type;
            this.properties = properties;
            this.identifier = identifier;
        }

        @Override
        public void mapTo(MessageFlowNode node, PropertyCollectionMap props) {
            int count = 1;
            for (MockProperties property : properties) {
                if (type.equals(property.getType())) {
                    PropertyCollectionMap subMap = new PropertyCollectionMap();
                    subMap.addProperty("key", property.getName());
                    subMap.addProperty("value-ref", property.getValue());
                    props.addPropertyCollection(getSchema(identifier) + ";" + count, subMap);
                    count++;
                }

            }
        }
    }

    @Override
    public void mapTo(MessageFlowNode node, PropertyCollectionMap props) {
        PropertyCollectionMap propertyMap = new PropertyCollectionMap();

        for (MunitMockModuleMapper mapper : mappers) {
            mapper.mapTo(node, propertyMap);
        }

        if (!propertyMap.isEmpty()) {
            props.addPropertyCollection(getSchema(propertiesIdentifier) + ";0", propertyMap);
        }
    }
}
