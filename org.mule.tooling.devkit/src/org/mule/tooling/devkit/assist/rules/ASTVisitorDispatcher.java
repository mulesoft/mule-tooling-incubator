package org.mule.tooling.devkit.assist.rules;

import java.util.Collection;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

public class ASTVisitorDispatcher {

    private final int nodeType;

    public ASTVisitorDispatcher(int nodeType) {
        this.nodeType = nodeType;
    }

    public void dispactch(Collection<ASTNode> collection, ASTVisitor visitor) {
        for (ASTNode node : collection) {
            if (node.getNodeType() == nodeType) {
                node.accept(visitor);
            }
        }
    }
}
