package org.mule.tooling.devkit.assist.context;

import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.mule.tooling.devkit.assist.DevkitTemplateProposal;
import org.mule.tooling.devkit.assist.rules.ASTVisitorDispatcher;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeFactory;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeType;
import org.mule.tooling.devkit.assist.rules.HasAnnotation;

public class MethodDeclarationContext extends SmartContext {

    public MethodDeclarationContext(IInvocationContext context) {
        super(context);
    }

    @Override
    protected ChainASTNodeType getVerifier() {
        return ChainASTNodeFactory.createAtMethodVerifier();
    }

    @Override
    protected void doAddProposals(List<IJavaCompletionProposal> proposals, Stack<ASTNode> stackNodes) {
        int selectionOffset = getOffset();
        HasAnnotation hasConnect = new HasAnnotation("Connect", selectionOffset).addAnnotation("Disconnect").addAnnotation("ValidateConnection")
                .addAnnotation("ConnectionIdentifier").addAnnotation("MetaDataKeyRetriever").addAnnotation("MetaDataRetriever");
        HasAnnotation hasAnnotation = new HasAnnotation("Processor", selectionOffset);
        new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(stackNodes, hasConnect);
        new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(stackNodes, hasAnnotation);

        if (!hasAnnotation.applies() && !(hasConnect.applies())) {
            proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.reconnectOn"));
        }
        if (hasAnnotation.applies()) {
            hasAnnotation = new HasAnnotation("ReconnectOn", selectionOffset);
            new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(stackNodes, hasAnnotation);
            if (!hasAnnotation.applies()) {
                proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.reconnectOn",0,context));
            }
        }
    }

}
