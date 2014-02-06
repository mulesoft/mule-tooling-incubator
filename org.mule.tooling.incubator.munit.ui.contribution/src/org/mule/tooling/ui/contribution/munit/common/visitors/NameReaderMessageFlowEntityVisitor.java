package org.mule.tooling.ui.contribution.munit.common.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mule.tooling.model.messageflow.Compartment;
import org.mule.tooling.model.messageflow.CompartmentLane;
import org.mule.tooling.model.messageflow.Container;
import org.mule.tooling.model.messageflow.EndpointNode;
import org.mule.tooling.model.messageflow.Flow;
import org.mule.tooling.model.messageflow.GlobalElement;
import org.mule.tooling.model.messageflow.GlobalUnknown;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.MuleConfiguration;
import org.mule.tooling.model.messageflow.MultiSourceNode;
import org.mule.tooling.model.messageflow.NestedContainer;
import org.mule.tooling.model.messageflow.PatternNode;
import org.mule.tooling.model.messageflow.ResponseNode;
import org.mule.tooling.model.messageflow.ScopeNode;
import org.mule.tooling.model.messageflow.SplitterNode;
import org.mule.tooling.model.messageflow.UnknownNode;
import org.mule.tooling.model.messageflow.util.MessageFlowEntityVisitor;
import org.mule.tooling.ui.contribution.munit.common.Filter;

/**
 * <p>
 * {@link MessageFlowEntityVisitor} to read the name attribute of a {@link MessageFlowNode}
 * </p> 
 * 
 * <ol>
 * <li>If the element to be visited is not a {@link MessageFlowNode} then the name will be null.</li> 
 * <li>If the element to be visited is not accepted by the filters then the name will be null</li> 
 * <li>If the element to be visited does not contain the property "name" then the name will be null</li> 
 * </ol>
 */
public class NameReaderMessageFlowEntityVisitor implements MessageFlowEntityVisitor {
	
	private String flowNodeName;
    private List<Filter<MessageFlowNode>> filters = new ArrayList<Filter<MessageFlowNode>>();
	private FlowNodeNameReader<MessageFlowNode> flowNodeNameReader = new FlowNodeNameReader<MessageFlowNode>();
	
	public NameReaderMessageFlowEntityVisitor(Filter<MessageFlowNode> ... messageFlowNodeFilters) {
		filters = Arrays.asList(messageFlowNodeFilters);
	}

	@Override
	public void visitMuleConfiguration(MuleConfiguration muleConfiguration) {
		
	}

	@Override
	public void visitGlobalElement(GlobalElement globalElement) {
	}

	@Override
	public void visitContainer(Container container) {
		doVisit(container);
	}

	@Override
	public void visitNestedContainer(NestedContainer nestedContainer) {
	}

	@Override
	public void visitFlow(Flow flow) {
		doVisit(flow);
	}

	@Override
	public void visitCompartment(Compartment compartment) {
		doVisit(compartment);
	}

	@Override
	public void visitCompartmentLane(CompartmentLane lane) {
	}

	@Override
	public void visitMultiSourceNode(MultiSourceNode multiSourceNode) {
		doVisit(multiSourceNode);
	}

	@Override
	public void visitScopeNode(ScopeNode scopeNode) {
		doVisit(scopeNode);
	}

	@Override
	public void visitResponseNode(ResponseNode responseNode) {
	}

	@Override
	public void visitEndpointNode(EndpointNode endpointNode) {
		doVisit(endpointNode);
	}

	@Override
	public void visitPatternNode(PatternNode patternNode) {
		doVisit(patternNode);
	}

	@Override
	public void visitSplitterNode(SplitterNode splitterNode) {
		doVisit(splitterNode);
	}

	@Override
	public void visitGlobalUnknown(GlobalUnknown globalUnknown) {
	}

	@Override
	public void visitUnknownNode(UnknownNode unknownNode) {
	}

	private <T extends MessageFlowNode> void doVisit(T messageFlowNode){
		if  (accept(messageFlowNode) )
		{
			flowNodeName = flowNodeNameReader.getNameFrom(messageFlowNode);
		}
	}
	
	private <T extends MessageFlowNode> boolean accept(T messageFlowNode){
		boolean accepted = true;
		for ( Filter<MessageFlowNode> filter : filters){
			accepted &= filter.accept(messageFlowNode);
		}
		
		return accepted;
	}
	
	public String getFlowNodeName() {
		return flowNodeName;
	}
}
