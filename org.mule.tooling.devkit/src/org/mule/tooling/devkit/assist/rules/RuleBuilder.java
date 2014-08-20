package org.mule.tooling.devkit.assist.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.mule.tooling.devkit.assist.Rule;

public class RuleBuilder {

    private Rule rule;
    ASTVisitorDispatcher currentDispatcher;

    public RuleBuilder atClassDeclaration() {
        currentDispatcher = new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT);
        return this;
    }

    public RuleBuilder HasAnnotation(String annotation) {
        return this;
    }

    public Rule build() {
        Rule result = rule;
        rule = null;
        return result;
    }
}
