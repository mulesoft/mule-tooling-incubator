package org.mule.tooling.ui.contribution.munit.editors.custom.mappers;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Text;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.Property;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.contribution.munit.editors.custom.MockProperties;
import org.mule.tooling.ui.contribution.munit.editors.custom.MockPropertiesTable;

/**
 * Mapper of the "then-return" configuration of the mock message processor
 */
public class ThenReturnMockMapper extends MunitMockModuleMapper {

	private Text payloadReference;
	private List<MockProperties> properties;
	private MockPropertiesTable propertiesTableViewer;

	public static MunitMockModuleMapper thenReturnLoaderInstanceFor(Text payloadReference, MockPropertiesTable propertiesTableViewer)
	{
		return new ThenReturnMockMapper(payloadReference, propertiesTableViewer);
	}

	public ThenReturnMockMapper(Text payloadReference, MockPropertiesTable propertiesTableViewer)
	{
		this.payloadReference = payloadReference;
		this.propertiesTableViewer = propertiesTableViewer;
		this.properties = propertiesTableViewer.getInputData();
		this.mappers.add(MessagePropertiesCollectionMapper.inboundPropertiesLoader(properties));
		this.mappers.add(MessagePropertiesCollectionMapper.outboundPropertiesLoader(properties));
		this.mappers.add(MessagePropertiesCollectionMapper.invocationPropertiesLoader(properties));
	}

	@Override
	protected void actOn(MessageFlowNode node,
			Map<String, PropertyCollectionMap> propertyCollections,
			String property) {
		Property payloadRef = node.getProperties().getPropertyCollection(property).getProperty("payload-ref");
		if ( payloadRef != null )
		{
			this.payloadReference.setText(payloadRef.getValue());
		}
	}

	@Override
	protected boolean matchPropertyCondition(String property) {
		return property.contains(getSchema("then-return"));
	}

	@Override
	protected void afterLoad(MessageFlowNode node, PropertyCollectionMap props) {
		propertiesTableViewer.setInputData(properties);
	}

	public void mapTo(MessageFlowNode node, PropertyCollectionMap props){
		PropertyCollectionMap propertyCollectionMap = new PropertyCollectionMap();

		List<MockProperties> inputData2 = propertiesTableViewer.getInputData();
		if ( inputData2 != null && !inputData2.isEmpty() )
		{
			for ( MunitMockModuleMapper mapper : mappers )
			{
				mapper.mapTo(node, propertyCollectionMap);
			}

		}
		props.addPropertyCollection(getSchema("then-return")+";0", propertyCollectionMap);
		propertyCollectionMap.addProperty(new Property("payload-ref", this.payloadReference.getText()));

	}

}
