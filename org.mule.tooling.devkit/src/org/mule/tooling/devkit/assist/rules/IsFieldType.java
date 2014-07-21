package org.mule.tooling.devkit.assist.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.mule.tooling.devkit.ASTUtils;

public class IsFieldType extends ASTRule {

    List<String> name = new ArrayList<String>();

    public IsFieldType(String name, int location) {
        super(location);
        this.name.add(name);
    }

    CompilationUnit cu;
    MethodDeclaration method;
    FieldDeclaration field;

    public IsFieldType addType(String name) {
        this.name.add(name);
        return this;
    }

    public boolean visit(FieldDeclaration node) {

        if (ASTUtils.contains(node, location)) {
            field = node;
            if (hasType(field.getType().toString())) {
                applies = true;
            }
            return true;
        }
        return false;
    }

    public boolean visit(CompilationUnit node) {
        cu = node;
        return true;
    }

    private boolean hasType(String node) {
        return name.contains(node);
    }
}
