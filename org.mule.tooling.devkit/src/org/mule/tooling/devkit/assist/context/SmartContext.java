package org.mule.tooling.devkit.assist.context;

import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeType;

public abstract class SmartContext {

    protected IInvocationContext context;
    private int offset;

    public SmartContext(IInvocationContext context) {
        this.context = context;
        setOffset(context.getSelectionOffset());
    }

    public void addProposals(List<IJavaCompletionProposal> proposals) {
        ChainASTNodeType verifier = getVerifier();
        Stack<ASTNode> stackNodes = new Stack<ASTNode>();
        ASTNode coveringNode = context.getCoveringNode();
        ASTNode current = coveringNode;
        while (current != null) {
            stackNodes.add(0, current);
            current = current.getParent();
        }
        if (verifier.matches(stackNodes.iterator())) {
            doAddProposals(proposals, stackNodes);
        }
    }

    protected abstract ChainASTNodeType getVerifier();

    protected abstract void doAddProposals(List<IJavaCompletionProposal> proposals, Stack<ASTNode> stackNodes);

    public IInvocationContext getContext() {
        return context;
    }

    public void setContext(IInvocationContext context) {
        this.context = context;
    }

    public CompilationUnit getCompilationUnit() {
        return context.getASTRoot();
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
