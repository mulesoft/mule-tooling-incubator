package org.mule.tooling.devkit.assist.rules;

import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.mule.tooling.devkit.ASTUtils;

public class LocateNode extends ASTVisitor {

    protected int location;
    private Stack<ASTNode> stackNodes = new Stack<ASTNode>();

    
    public Stack<ASTNode> getStackNodes() {
        return stackNodes;
    }

    
    public void setStackNodes(Stack<ASTNode> stackNodes) {
        this.stackNodes = stackNodes;
    }

    public LocateNode() {
        this.location = -1;
    }

    public LocateNode(int location) {
        this.location = location;
    }

    public void preVisit(ASTNode node) {
        // default implementation: do nothing
        if (ASTUtils.contains(node, location)) {
            stackNodes.push(node);
        }
    }

    public ASTNode getNode() {
        return stackNodes.peek();
    }
}
