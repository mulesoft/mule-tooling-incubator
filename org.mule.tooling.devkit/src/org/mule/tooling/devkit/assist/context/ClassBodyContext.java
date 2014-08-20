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
import org.mule.tooling.devkit.assist.rules.ExistsFieldWithAnnotation;
import org.mule.tooling.devkit.assist.rules.ExistsMethodWithAnnotation;
import org.mule.tooling.devkit.assist.rules.HasAnnotation;

public class ClassBodyContext extends SmartContext {

    public ClassBodyContext(IInvocationContext context) {
        super(context);
    }

    @Override
    protected ChainASTNodeType getVerifier() {
        return ChainASTNodeFactory.createAtClassBodyVerifier();
    }

    @Override
    protected void doAddProposals(List<IJavaCompletionProposal> proposals, Stack<ASTNode> stackNodes) {
        int selectionOffset = getOffset();
        HasAnnotation hasAnnotation = new HasAnnotation("Connector", selectionOffset);
        new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(stackNodes, hasAnnotation);
        if (hasAnnotation.applies()) {
            proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addConfigurable", 9, context));
            ExistsFieldWithAnnotation fieldCheck = new ExistsFieldWithAnnotation("Default");
            new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(stackNodes, fieldCheck);
            if (fieldCheck.applies()) {
                proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addDefaultConfigurable", 9, context));
            }
            proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addProcessor", 10, context));
            proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addSource", 6, context));
            proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addTransformer", 7, context));
            ExistsMethodWithAnnotation methodCheck = new ExistsMethodWithAnnotation("MetaDataKeyRetriever");
            new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(stackNodes, methodCheck);
            if (!methodCheck.applies()) {
                proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addMetadata", 5, context));
            } else {
                methodCheck = new ExistsMethodWithAnnotation("MetaDataRetriever");
                new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(stackNodes, methodCheck);
                if (!methodCheck.applies()) {
                    proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.metadata.retriever", 9,context));
                } else {
                    methodCheck = new ExistsMethodWithAnnotation("QueryTranslator");
                    new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(stackNodes, methodCheck);
                    if (!methodCheck.applies()) {
                        proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addQueryTranslator", 9, context));
                    }
                }

            }
        }
    }

}
