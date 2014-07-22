package org.mule.tooling.devkit.assist.context;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.mule.tooling.devkit.assist.DevkitTemplateProposal;
import org.mule.tooling.devkit.assist.rules.ASTVisitorDispatcher;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeFactory;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeType;
import org.mule.tooling.devkit.assist.rules.ExistsMethodWithAnnotation;
import org.mule.tooling.devkit.assist.rules.HasAnnotation;
import org.mule.tooling.devkit.assist.rules.LocateNode;

public class ClassBodyContext extends SmartContext {

    @Override
    protected ChainASTNodeType getVerifier() {
        return ChainASTNodeFactory.createAtClassBodyVerifier();
    }

    @Override
    protected void doAddProposals(List<IJavaCompletionProposal> proposals, LocateNode node, CompilationUnit cu, int selectionOffset) {
        HasAnnotation hasAnnotation = new HasAnnotation("Connector", selectionOffset);
        new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), hasAnnotation);
        if (hasAnnotation.applies()) {
            proposals.add(new DevkitTemplateProposal("Add New Configurable", 9));
            proposals.add(new DevkitTemplateProposal("Add New Processor", 10));
            proposals.add(new DevkitTemplateProposal("Add New Source", 6));
            proposals.add(new DevkitTemplateProposal("Add New Transformer", 7));
            ExistsMethodWithAnnotation methodCheck = new ExistsMethodWithAnnotation("MetaDataKeyRetriever");
            new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), methodCheck);
            if (!methodCheck.applies()) {
                proposals.add(new DevkitTemplateProposal("Add MetaDataKeyRetriever", 5));
            } else {
                methodCheck = new ExistsMethodWithAnnotation("MetaDataRetriever");
                new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), methodCheck);
                if (!methodCheck.applies()) {
                    proposals.add(new DevkitTemplateProposal("Add MetaDataRetriever", 9));
                } else {
                    methodCheck = new ExistsMethodWithAnnotation("QueryTranslator");
                    new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), methodCheck);
                    if (!methodCheck.applies()) {
                        proposals.add(new DevkitTemplateProposal("Add QueryTranslator", 9));
                    }
                }

            }
        }
    }

}
