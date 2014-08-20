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

public class ParameterContext extends SmartContext {

    public ParameterContext(IInvocationContext context) {
        super(context);
    }

    @Override
    protected ChainASTNodeType getVerifier() {
        return ChainASTNodeFactory.createAtParameterVerifier();
    }

    @Override
    protected void doAddProposals(List<IJavaCompletionProposal> proposals, Stack<ASTNode> stackNodes) {
        int selectionOffset = getOffset();
        HasAnnotation hasAnnotation = new HasAnnotation("Processor", selectionOffset);
        new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(stackNodes, hasAnnotation);
        if (hasAnnotation.applies()) {
            HasAnnotation hasOptional = new HasAnnotation("Optional", selectionOffset);
            new ASTVisitorDispatcher(ASTNode.SINGLE_VARIABLE_DECLARATION).dispactch(stackNodes, hasOptional);
            if (!hasOptional.applies()) {
                proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.annotation.optional",0,context));
                HasAnnotation hasDefault = new HasAnnotation("Default", selectionOffset);
                new ASTVisitorDispatcher(ASTNode.SINGLE_VARIABLE_DECLARATION).dispactch(stackNodes, hasDefault);
                if (!hasDefault.applies()) {
                    proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.annotation.default",0,context));
                }
            } else {
                HasAnnotation hasDefault = new HasAnnotation("Default", selectionOffset);
                new ASTVisitorDispatcher(ASTNode.SINGLE_VARIABLE_DECLARATION).dispactch(stackNodes, hasDefault);
                if (!hasDefault.applies()) {
                    proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.annotation.default",0,context));
                }
            }
        }
    }

}
