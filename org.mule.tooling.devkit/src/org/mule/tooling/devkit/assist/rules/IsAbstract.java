package org.mule.tooling.devkit.assist.rules;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;

public class IsAbstract extends ASTRule {

    CompilationUnit cu;

    public boolean visit(CompilationUnit node) {
        cu = node;
        return true;
    }

    public boolean visit(Modifier node) {
        if (node.isAbstract() && node.getParent().equals(cu.types().get(0))) {
            applies = true;
        }

        return false;
    }
}
