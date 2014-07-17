package org.mule.tooling.devkit.assist.rules;

import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTNode;

public class ChainASTNodeType {

    ChainASTNodeType next;
    private int astNodeType;

    public ChainASTNodeType getNext() {
        return next;
    }

    public void setNext(ChainASTNodeType next) {
        this.next = next;
    }

    public int getAstNodeType() {
        return astNodeType;
    }

    public void setAstNodeType(int astNodeType) {
        this.astNodeType = astNodeType;
    }

    public boolean matches(Iterator<ASTNode> nodes) {
        ASTNode node = nodes.next();
        if (node.getNodeType() == astNodeType) {
            if (nodes.hasNext()) {
                if (next != null) {
                    return next.matches(nodes);
                } else {
                    return false;
                }
            } else {
                if (next != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public int size() {
        return 1 + ((next == null) ? 0 : next.size());
    }
}
