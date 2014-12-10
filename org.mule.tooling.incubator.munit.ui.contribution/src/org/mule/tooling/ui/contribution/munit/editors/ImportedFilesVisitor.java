package org.mule.tooling.ui.contribution.munit.editors;

import java.util.ArrayList;
import java.util.List;

import org.mule.tooling.model.messageflow.Compartment;
import org.mule.tooling.model.messageflow.CompartmentLane;
import org.mule.tooling.model.messageflow.Container;
import org.mule.tooling.model.messageflow.EndpointNode;
import org.mule.tooling.model.messageflow.GlobalElement;
import org.mule.tooling.model.messageflow.GlobalUnknown;
import org.mule.tooling.model.messageflow.MuleConfiguration;
import org.mule.tooling.model.messageflow.MultiSourceNode;
import org.mule.tooling.model.messageflow.NestedContainer;
import org.mule.tooling.model.messageflow.PatternNode;
import org.mule.tooling.model.messageflow.ResponseNode;
import org.mule.tooling.model.messageflow.SplitterNode;
import org.mule.tooling.model.messageflow.UnknownNode;
import org.mule.tooling.model.messageflow.util.MessageFlowEntityVisitor;

/**
 * <p>
 * Gets the files that are imported from an Munit test, it reads all the import statements from the Munit suite
 * </p>
 */
public class ImportedFilesVisitor implements MessageFlowEntityVisitor {

    private List<String> files;

    public ImportedFilesVisitor() {
        this.files = new ArrayList<String>();
    }

    @Override
    public void visitMuleConfiguration(MuleConfiguration muleConfiguration) {
    }

    @Override
    public void visitGlobalElement(GlobalElement globalElement) {
        if ("http://www.springframework.org/schema/beans/import".equals(globalElement.getType())) {
            files.add(globalElement.getProperties().getProperty("resource").getValue());
        }
    }

    @Override
    public void visitContainer(Container container) {
    }

    @Override
    public void visitNestedContainer(NestedContainer nestedContainer) {
    }

    @Override
    public void visitCompartment(Compartment compartment) {
    }

    @Override
    public void visitCompartmentLane(CompartmentLane lane) {
    }

    @Override
    public void visitMultiSourceNode(MultiSourceNode multiSourceNode) {
    }

    @Override
    public void visitResponseNode(ResponseNode responseNode) {
    }

    @Override
    public void visitEndpointNode(EndpointNode endpointNode) {
    }

    @Override
    public void visitPatternNode(PatternNode patternNode) {
    }

    @Override
    public void visitSplitterNode(SplitterNode splitterNode) {
    }

    @Override
    public void visitGlobalUnknown(GlobalUnknown globalUnknown) {
    }

    @Override
    public void visitUnknownNode(UnknownNode unknownNode) {
    }

    public List<String> getFiles() {
        return files;
    }

}
