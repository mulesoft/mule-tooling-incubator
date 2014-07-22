package org.mule.tooling.devkit.assist.context;

import java.util.List;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeType;
import org.mule.tooling.devkit.assist.rules.LocateNode;

public abstract class SmartContext {

    public void addProposals(List<IJavaCompletionProposal> proposals, IInvocationContext context) {
        ChainASTNodeType verifier = getVerifier();
        CompilationUnit obj = context.getASTRoot();
        int selectionOffset = context.getSelectionOffset();
        LocateNode node = new LocateNode(selectionOffset);
        obj.accept(node);
        if (verifier.matches(node.getStackNodes().iterator())) {
            doAddProposals(proposals, node, obj, selectionOffset);
        }
    }

    protected abstract ChainASTNodeType getVerifier();

    protected abstract void doAddProposals(List<IJavaCompletionProposal> proposals, LocateNode node, CompilationUnit cu, int selectionOffset);
}
