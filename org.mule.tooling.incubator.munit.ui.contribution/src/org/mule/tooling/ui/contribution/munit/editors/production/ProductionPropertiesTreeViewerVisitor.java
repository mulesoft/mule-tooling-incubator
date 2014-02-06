package org.mule.tooling.ui.contribution.munit.editors.production;

import org.eclipse.jface.viewers.TreeViewer;
import org.mule.tooling.model.messageflow.Compartment;
import org.mule.tooling.model.messageflow.CompartmentLane;
import org.mule.tooling.model.messageflow.Container;
import org.mule.tooling.model.messageflow.EndpointNode;
import org.mule.tooling.model.messageflow.Flow;
import org.mule.tooling.model.messageflow.GlobalElement;
import org.mule.tooling.model.messageflow.GlobalUnknown;
import org.mule.tooling.model.messageflow.MuleConfiguration;
import org.mule.tooling.model.messageflow.MultiSourceNode;
import org.mule.tooling.model.messageflow.NestedContainer;
import org.mule.tooling.model.messageflow.PatternNode;
import org.mule.tooling.model.messageflow.ResponseNode;
import org.mule.tooling.model.messageflow.ScopeNode;
import org.mule.tooling.model.messageflow.SplitterNode;
import org.mule.tooling.model.messageflow.UnknownNode;
import org.mule.tooling.model.messageflow.util.MessageFlowEntityVisitor;

/**
 * <p>
 * {@link MessageFlowEntityVisitor} to set the element properties into a {@link TreeViewer} where those properties are
 * going to be seen. 
 * </p> 
 */
public class ProductionPropertiesTreeViewerVisitor implements MessageFlowEntityVisitor{

	private TreeViewer propertiesViewer;

	public ProductionPropertiesTreeViewerVisitor(TreeViewer propertiesViewer) {
		this.propertiesViewer = propertiesViewer;
	}

	@Override
	public void visitMuleConfiguration(MuleConfiguration muleConfiguration) {
	}

	@Override
	public void visitGlobalElement(GlobalElement globalElement) {
		propertiesViewer.setInput(globalElement.getProperties());
	}

	@Override
	public void visitContainer(Container container) {
		propertiesViewer.setInput(container.getProperties());

	}

	@Override
	public void visitNestedContainer(NestedContainer nestedContainer) {
		propertiesViewer.setInput(nestedContainer.getProperties());

	}

	@Override
	public void visitFlow(Flow flow) {
		propertiesViewer.setInput(flow.getProperties());

	}

	@Override
	public void visitCompartment(Compartment compartment) {
		propertiesViewer.setInput(compartment.getProperties());

	}

	@Override
	public void visitCompartmentLane(CompartmentLane lane) {

	}

	@Override
	public void visitMultiSourceNode(MultiSourceNode multiSourceNode) {
		propertiesViewer.setInput(multiSourceNode.getProperties());

	}

	@Override
	public void visitScopeNode(ScopeNode scopeNode) {
		propertiesViewer.setInput(scopeNode.getProperties());

	}

	@Override
	public void visitResponseNode(ResponseNode responseNode) {
	}

	@Override
	public void visitEndpointNode(EndpointNode endpointNode) {
		propertiesViewer.setInput(endpointNode.getProperties());

	}

	@Override
	public void visitPatternNode(PatternNode patternNode) {
		propertiesViewer.setInput(patternNode.getProperties());
	}

	@Override
	public void visitSplitterNode(SplitterNode splitterNode) {
		propertiesViewer.setInput(splitterNode.getProperties());

	}

	@Override
	public void visitGlobalUnknown(GlobalUnknown globalUnknown) {

	}

	@Override
	public void visitUnknownNode(UnknownNode unknownNode) {

	}

}

