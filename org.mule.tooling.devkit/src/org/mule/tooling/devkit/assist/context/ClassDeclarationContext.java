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
import org.mule.tooling.devkit.assist.rules.HasAnnotation;
import org.mule.tooling.devkit.assist.rules.IsAbstract;
import org.mule.tooling.devkit.assist.rules.LocateNode;

public class ClassDeclarationContext extends SmartContext {

    public ClassDeclarationContext(IInvocationContext context) {
        super(context);
    }

    @Override
    protected ChainASTNodeType getVerifier() {
        return ChainASTNodeFactory.createAtClassVerifier();
    }

    @Override
    protected void doAddProposals(List<IJavaCompletionProposal> proposals, LocateNode node) {
        CompilationUnit cu = getCompilationUnit();
        int selectionOffset = getOffset();
        HasAnnotation hasAnnotation = new HasAnnotation("Connector", selectionOffset);
        new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), hasAnnotation);
        if (hasAnnotation.applies()) {
            IsAbstract isAbstract = new IsAbstract();
            cu.accept(isAbstract);
            if (isAbstract.applies()) {
                proposals.add(new DevkitTemplateProposal("Add Rest Template"));
                HasAnnotation hasOAuth = new HasAnnotation("OAuth", selectionOffset);
                cu.accept(hasOAuth);
                if (!hasOAuth.applies()) {
                    proposals.add(new DevkitTemplateProposal("Add OAuth annotation"));
                }
            } else {
                //ReconnectOn
            }

        }
    }

}
