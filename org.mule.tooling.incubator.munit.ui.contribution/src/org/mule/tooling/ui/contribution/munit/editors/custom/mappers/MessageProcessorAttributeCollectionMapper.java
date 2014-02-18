package org.mule.tooling.ui.contribution.munit.editors.custom.mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.Property;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.contribution.munit.editors.custom.MessageProcessorMatchingForm;

/**
 * <p>
 * Class that matches the attribute matching collection of the mock module message procesors.
 * <p>
 */
public class MessageProcessorAttributeCollectionMapper extends MunitMockModuleMapper {

    private String attributesIdentifier;

    private MessageProcessorMatchingForm form;
    private Map<String, String> attributes = new HashMap<String, String>();

    public static MessageProcessorAttributeCollectionMapper mockAttributeCollectionMapper(MessageProcessorMatchingForm form) {
        return new MessageProcessorAttributeCollectionMapper(form, "with-attributes", "with-attribute");
    }

    public static MessageProcessorAttributeCollectionMapper verifyAttributeCollectionMapper(MessageProcessorMatchingForm form) {
        return new MessageProcessorAttributeCollectionMapper(form, "attributes", "attribute");
    }

    public MessageProcessorAttributeCollectionMapper(final MessageProcessorMatchingForm form, String attributesIdentifier, final String attributeIdentifier) {
        this.form = form;
        this.attributesIdentifier = attributesIdentifier;
        mappers = new ArrayList<MunitMockModuleMapper>();
        mappers.add(new ItemCollectionMapperLoader(attributeIdentifier, new PropertyAgregator() {

            @Override
            public void agregate(PropertyCollectionMap map) {
                attributes.put(map.getProperty("name", ""), map.getProperty("whereValue-ref", ""));
            }
        }, new PropertyMapper() {

            @Override
            public void mapTo(MessageFlowNode node, PropertyCollectionMap props) {
                Map<String, String> inputData = form.getAttributeMatching();
                int i = 1;
                for (String key : inputData.keySet()) {
                    PropertyCollectionMap attributeMap = new PropertyCollectionMap();
                    attributeMap.addProperty("name", key);
                    attributeMap.addProperty("whereValue-ref", inputData.get(key));
                    props.addPropertyCollection(String.format(MUNIT_MOCK_SCHEMA, attributeIdentifier) + ";" + i, attributeMap);
                    i++;
                }
            }
        }));
    }

    @Override
    protected void actOn(MessageFlowNode node, Map<String, PropertyCollectionMap> propertyCollections, String property) {
    }

    @Override
    protected boolean matchPropertyCondition(String property) {
        return property.contains(getSchema(attributesIdentifier));
    }

    @Override
    protected void afterLoad(MessageFlowNode node, PropertyCollectionMap props) {
        if (node.getProperties() != null && node.getProperties().getProperty("messageProcessor") != null && node.getProperties().getProperty("messageProcessor").getValue() != null) {
            this.form.setMessageProcessorRegexMatching(node.getProperties().getProperty("messageProcessor").getValue());
        }

        this.form.setAttributeMatching(attributes);
    }

    public void mapTo(MessageFlowNode node, PropertyCollectionMap props) {
        PropertyCollectionMap propertyCollectionMap = new PropertyCollectionMap();
        for (MunitMockModuleMapper mapper : mappers) {
            mapper.mapTo(node, propertyCollectionMap);
        }

        props.addPropertyCollection(getSchema(attributesIdentifier) + ";0", propertyCollectionMap);
        props.addProperty(new Property("messageProcessor", this.form.getMessageProcessorRegexMatching()));
    }
}
