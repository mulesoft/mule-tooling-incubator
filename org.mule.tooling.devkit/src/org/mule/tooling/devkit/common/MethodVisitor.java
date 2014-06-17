package org.mule.tooling.devkit.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodVisitor extends ASTVisitor {

    private List<MethodDeclaration> methods;

    public boolean visit(MethodDeclaration node) {
        if (methods == null) {
            methods = new ArrayList<MethodDeclaration>();
        }
        methods.add(node);
        return false;
    }

    public List<MethodDeclaration> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodDeclaration> methods) {
        this.methods = methods;
    }
}