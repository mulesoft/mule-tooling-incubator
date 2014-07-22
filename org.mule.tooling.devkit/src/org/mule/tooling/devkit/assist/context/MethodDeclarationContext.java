package org.mule.tooling.devkit.assist.context;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.mule.tooling.devkit.assist.DevkitTemplateProposal;
import org.mule.tooling.devkit.assist.rules.ASTVisitorDispatcher;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeFactory;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeType;
import org.mule.tooling.devkit.assist.rules.HasAnnotation;
import org.mule.tooling.devkit.assist.rules.LocateNode;

public class MethodDeclarationContext extends SmartContext {

    @Override
    protected ChainASTNodeType getVerifier() {
        return ChainASTNodeFactory.createAtMethodVerifier();
    }

    @Override
    protected void doAddProposals(List<IJavaCompletionProposal> proposals, LocateNode node, CompilationUnit cu, int selectionOffset) {
        HasAnnotation hasConnect = new HasAnnotation("Connect", selectionOffset).addAnnotation("Disconnect").addAnnotation("ValidateConnection")
                .addAnnotation("ConnectionIdentifier").addAnnotation("MetaDataKeyRetriever").addAnnotation("MetaDataRetriever");
        HasAnnotation hasAnnotation = new HasAnnotation("Processor", selectionOffset);
        new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasAnnotation);
        new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasConnect);

        if (!hasAnnotation.applies() && !(hasConnect.applies())) {
            proposals.add(new DevkitTemplateProposal("Add Processor"));
        }
        if (hasAnnotation.applies()) {
            hasAnnotation = new HasAnnotation("ReconnectOn", selectionOffset);
            new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasAnnotation);
            if (!hasAnnotation.applies()) {
                proposals.add(new DevkitTemplateProposal("Add ReconnectOn"));
            }
        }
    }

}
