package org.mule.tooling.devkit.assist.context;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeType;
import org.mule.tooling.devkit.assist.rules.LocateNode;

public abstract class SmartContext {

    private IInvocationContext context;
    private CompilationUnit compilationUnit;
    private int offset;

    public SmartContext(IInvocationContext context) {
        this.context = context;
        this.compilationUnit = context.getASTRoot();
        setOffset(context.getSelectionOffset());
        ASTNode node = context.getCoveringNode();
        System.out.println(node);
    }

    public void addProposals(List<IJavaCompletionProposal> proposals) {
        ChainASTNodeType verifier = getVerifier();
        CompilationUnit obj = context.getASTRoot();
        int selectionOffset = context.getSelectionOffset();
        LocateNode node = new LocateNode(selectionOffset);
        obj.accept(node);
        if (verifier.matches(node.getStackNodes().iterator())) {
            doAddProposals(proposals, node);
        }
    }

    protected abstract ChainASTNodeType getVerifier();

    protected abstract void doAddProposals(List<IJavaCompletionProposal> proposals, LocateNode node);

    public IInvocationContext getContext() {
        return context;
    }

    public void setContext(IInvocationContext context) {
        this.context = context;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public void setCompilationUnit(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
