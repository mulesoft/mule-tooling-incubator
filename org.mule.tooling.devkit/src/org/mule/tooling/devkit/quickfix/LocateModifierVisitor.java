package org.mule.tooling.devkit.quickfix;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class LocateModifierVisitor extends ASTVisitor {

    private ASTNode node;

    private int chartStart;

    private ModifierKeyword modifier;

    public LocateModifierVisitor(int chartStart, ModifierKeyword modifier) {
        this.chartStart = chartStart;
        this.modifier = modifier;
    }

    public boolean visit(Modifier node) {
        if (node.getKeyword().equals(modifier)) {
            setNode(node);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean visit(FieldDeclaration node) {
        List<VariableDeclarationFragment> fragments = node.fragments();
        for (VariableDeclarationFragment obj : fragments) {
            if (obj.getStartPosition() == chartStart) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        if (node.getName().getStartPosition() == chartStart) {
            return true;
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
