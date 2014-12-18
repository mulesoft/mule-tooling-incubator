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
 * Class that matches the attribute matching collection of the mock module
 * message procesors.
 * <p>
 */
public class MessageProcessorAttributeCollectionMapper extends MunitMockModuleMapper {
	private static final String WHEN_CALLING_ATTRIBTUE = "whenCalling";
	private static final String MESSAGE_PROCESSOR_ATTRIBUTE = "messageProcessor";

	private boolean whenCalling;
	private String attributesIdentifier;
	private MessageProcessorMatchingForm form;
	private Map<String, String> attributes = new HashMap<String, String>();

	public static MessageProcessorAttributeCollectionMapper mockAttributeCollectionMapper(MessageProcessorMatchingForm form) {
		return new MessageProcessorAttributeCollectionMapper(form, "with-attributes", "with-attribute", false);
	}

	public static MessageProcessorAttributeCollectionMapper verifyAttributeCollectionMapper(MessageProcessorMatchingForm form) {
		return new MessageProcessorAttributeCollectionMapper(form, "attributes", "attribute", false);
	}

	public static MessageProcessorAttributeCollectionMapper mockThrowAnAttributeCollectionMapper(MessageProcessorMatchingForm form) {
		return new MessageProcessorAttributeCollectionMapper(form, "with-attributes", "with-attribute", true);
	}

	public MessageProcessorAttributeCollectionMapper(final MessageProcessorMatchingForm form, String attributesIdentifier, final String attributeIdentifier,
			boolean whenCalling) {

		this.form = form;
		this.whenCalling = whenCalling;
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
		String attributePropertyName = "";
		if (!whenCalling) {
			attributePropertyName = MESSAGE_PROCESSOR_ATTRIBUTE;
		} else {
			attributePropertyName = WHEN_CALLING_ATTRIBTUE;
		}

		if (node.getProperties() != null && node.getProperties().getProperty(attributePropertyName) != null
				&& node.getProperties().getProperty(attributePropertyName).getValue() != null) {
			this.form.setMessageProcessorRegexMatching(node.getProperties().getProperty(attributePropertyName).getValue());
		}

		this.form.setAttributeMatching(attributes);
	}

	public void mapTo(MessageFlowNode node, PropertyCollectionMap props) {
		PropertyCollectionMap propertyCollectionMap = new PropertyCollectionMap();
		for (MunitMockModuleMapper mapper : mappers) {
			mapper.mapTo(node, propertyCollectionMap);
		}

		props.addPropertyCollection(getSchema(attributesIdentifier) + ";0", propertyCollectionMap);
		if (!whenCalling) {
			props.addProperty(new Property(MESSAGE_PROCESSOR_ATTRIBUTE, this.form.getMessageProcessorRegexMatching()));
		} else {
			props.addProperty(new Property(WHEN_CALLING_ATTRIBTUE, this.form.getMessageProcessorRegexMatching()));
		}
	}
}
