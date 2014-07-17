package org.mule.tooling.devkit.assist.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.mule.tooling.devkit.ASTUtils;

public class HasAnnotation extends ASTRule {

    List<String> name = new ArrayList<String>();

    public HasAnnotation(String name, int location) {
        super(location);
        this.name.add(name);
    }

    CompilationUnit cu;
    MethodDeclaration method;
    SingleVariableDeclaration variable;
    FieldDeclaration field;
    String annotation;

    public HasAnnotation addAnnotation(String name) {
        this.name.add(name);
        return this;
    }

    public boolean visit(FieldDeclaration node) {

        if (ASTUtils.contains(node, location)) {
            field = node;
            return true;
        }
        return false;
    }

    public boolean visit(CompilationUnit node) {
        cu = node;
        return true;
    }

    public boolean visit(MethodDeclaration node) {
        if (ASTUtils.contains(node, location)) {
            method = node;
            return true;
        }
        return true;
    }

    public boolean visit(SingleVariableDeclaration node) {
        if (ASTUtils.contains(node, location)) {
            variable = node;
            return true;
        }
        return true;
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        ASTNode parent = node.getParent();
        if (ASTUtils.contains(field, location)) {
            if (hasAnnotation(node) && parent.equals(field)) {
                applies = true;
            }
        } else if (ASTUtils.contains(variable, location)) {
            if (hasAnnotation(node) && parent.equals(variable)) {
                applies = true;
            }
        } else if (ASTUtils.contains(cu, location)) {
            if (hasAnnotation(node) && (node.getParent().equals(cu.types().get(0)))) {
                applies = true;
            }
        } else if (ASTUtils.contains(method, location)) {
            if (hasAnnotation(node) && parent.equals(method)) {
                applies = true;
            }
        }
        return false;
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        ASTNode parent = node.getParent();
        if (ASTUtils.contains(field, location)) {
            if (hasAnnotation(node) && parent.equals(field)) {
                applies = true;
            }
        } else if (ASTUtils.contains(variable, location)) {
            if (hasAnnotation(node) && parent.equals(variable)) {
                applies = true;
            }
        } else if (ASTUtils.contains(cu, location)) {
            if (hasAnnotation(node) && (node.getParent().equals(cu.types().get(0)))) {
                applies = true;
            }
        } else if (ASTUtils.contains(method, location)) {
            if (hasAnnotation(node) && parent.equals(method)) {
                applies = true;
            }
        }
        return false;
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        ASTNode parent = node.getParent();
        if (ASTUtils.contains(field, location)) {
            if (hasAnnotation(node) && parent.equals(field)) {
                applies = true;
            }
        } else if (ASTUtils.contains(variable, location)) {
            if (hasAnnotation(node) && parent.equals(variable)) {
                applies = true;
            }
        } else if (ASTUtils.contains(cu, location)) {
            if (hasAnnotation(node) && (node.getParent().equals(cu.types().get(0)))) {
                applies = true;
            }
        } else if (ASTUtils.contains(method, location)) {
            if (hasAnnotation(node) && parent.equals(method)) {
                applies = true;
            }
        }
        return false;
    }

    private boolean hasAnnotation(Annotation node) {
        return name.contains(node.getTypeName().toString());
    }
}
