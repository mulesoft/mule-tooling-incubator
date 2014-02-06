package org.mule.tooling.ui.contribution.munit;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.mule.tooling.core.builder.container.ContainerBehavior;
import org.mule.tooling.core.builder.messageflow.MuleToFlowTransformer;
import org.mule.tooling.model.messageflow.Container;
import org.mule.tooling.model.messageflow.MessageFlowEntity;
import org.mule.tooling.model.messageflow.MuleConfiguration;
import org.mule.tooling.model.messageflow.NestedContainer;
import org.mule.tooling.model.module.ContainerDefinition;
import org.mule.tooling.model.module.GraphicalContainerDefinition;
import org.mule.tooling.model.module.NodeDefinition;

public class MunitBehavior implements ContainerBehavior{

	private static final Namespace MOCK_NAMESPACE = new Namespace("mock", "http://www.mulesoft.org/schema/mule/mock");

	@Override
	public void handleContainerElements(List<Element> elements,
			Container container, ContainerDefinition containerDefinition,
			MuleConfiguration muleConfiguration, String runtimeVersion,
			MuleToFlowTransformer muleToFlowTransformer) {
		
		for ( Element element : elements){
            NodeDefinition def = muleToFlowTransformer.getDefinitionForElement(element, runtimeVersion);
            JAXBElement<? extends MessageFlowEntity> entity = muleToFlowTransformer.getPopulatedNode(element, muleConfiguration, def, runtimeVersion);

				if ( MOCK_NAMESPACE.equals(element.getNamespace()) && ("when".equals(element.getName()) || "spy".equals(element.getName()))){
					getSetupContainer(container, containerDefinition).getCompartmentLaneEntries().add(entity);
				}
				else{
					getCoreContainer(container, containerDefinition).getCompartmentLaneEntries().add(entity);

				}
		}
	}

    NestedContainer getSetupContainer(Container container, ContainerDefinition containerDefinition) {
        List<GraphicalContainerDefinition> graphicalContainerDefinitions = containerDefinition.getGraphicalContainerDefinitions();
        GraphicalContainerDefinition setupContainerDef = graphicalContainerDefinitions.get(0);
        NestedContainer setupContainer = container.getNestedContainer(setupContainerDef.getId());
        return setupContainer;
    }
    
    NestedContainer getCoreContainer(Container container, ContainerDefinition containerDefinition) {
        List<GraphicalContainerDefinition> graphicalContainerDefinitions = containerDefinition.getGraphicalContainerDefinitions();
        GraphicalContainerDefinition coreContainerDef = graphicalContainerDefinitions.get(1);
        return container.getNestedContainer(coreContainerDef.getId());
    }
    
}
