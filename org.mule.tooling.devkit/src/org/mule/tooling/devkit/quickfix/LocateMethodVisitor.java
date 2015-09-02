package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class LocateMethodVisitor extends ASTVisitor {

    private ASTNode node;
    private int chartStart;

    public LocateMethodVisitor(int chartStart) {
        this.chartStart = chartStart;
    }

    public boolean visit(MethodDeclaration node) {
        int currentChartStart = -1;

        if (node.getName().getStartPosition() == chartStart) {
            this.setNode(node);
        }
        return currentChartStart == chartStart;
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        if (node.getTypeName().toString().equals("Optional")) {
            this.setNode(node);
        }
        return false;
    }

    public ASTNode getNode() {
        return node;
    }

    public void setNode(ASTNode node) {
        this.node = node;
    }
}
