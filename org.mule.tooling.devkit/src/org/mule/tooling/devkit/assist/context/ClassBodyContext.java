package org.mule.tooling.devkit.assist.context;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.mule.tooling.devkit.assist.DevkitTemplateProposal;
import org.mule.tooling.devkit.assist.rules.ASTVisitorDispatcher;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeFactory;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeType;
import org.mule.tooling.devkit.assist.rules.ExistsFieldWithAnnotation;
import org.mule.tooling.devkit.assist.rules.ExistsMethodWithAnnotation;
import org.mule.tooling.devkit.assist.rules.HasAnnotation;
import org.mule.tooling.devkit.assist.rules.LocateNode;

public class ClassBodyContext extends SmartContext {

    public ClassBodyContext(IInvocationContext context) {
        super(context);
    }

    @Override
    protected ChainASTNodeType getVerifier() {
        return ChainASTNodeFactory.createAtClassBodyVerifier();
    }

    @Override
    protected void doAddProposals(List<IJavaCompletionProposal> proposals, LocateNode node) {

        CompilationUnit cu = getCompilationUnit();
        int selectionOffset = getOffset();
        HasAnnotation hasAnnotation = new HasAnnotation("Connector", selectionOffset);
        new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), hasAnnotation);
        if (hasAnnotation.applies()) {
            proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addConfigurable", 9, cu));
            ExistsFieldWithAnnotation fieldCheck = new ExistsFieldWithAnnotation("Default");
            new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), fieldCheck);
            if (fieldCheck.applies()) {
                proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addDefaultConfigurable", 9, cu));
            }
            proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addProcessor", 10, cu));
            proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addSource", 6, cu));
            proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addTransformer", 7, cu));
            ExistsMethodWithAnnotation methodCheck = new ExistsMethodWithAnnotation("MetaDataKeyRetriever");
            new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), methodCheck);
            if (!methodCheck.applies()) {
                proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addMetadata", 5, cu));
            } else {
                methodCheck = new ExistsMethodWithAnnotation("MetaDataRetriever");
                new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), methodCheck);
                if (!methodCheck.applies()) {
                    proposals.add(new DevkitTemplateProposal("Add MetaDataRetriever", 9));
                } else {
                    methodCheck = new ExistsMethodWithAnnotation("QueryTranslator");
                    new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), methodCheck);
                    if (!methodCheck.applies()) {
                        proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.addQueryTranslator", 9, cu));
                    }
                }

            }
        }
    }

}