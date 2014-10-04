package org.mule.tooling.incubator.gradle.parser.ast;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.eclipse.jface.text.IDocument;


public class GradleScriptASTParser {
    
    private List<ASTNode> nodes;
    
    /**
     * Builds the AST parser and parses the script. If the privded document is not parseable
     * it will throw multiple compilation errors exception right away. 
     * @param document
     * @throws MultipleCompilationErrorsException
     */
    public GradleScriptASTParser(IDocument document) throws MultipleCompilationErrorsException {
        AstBuilder builder = new AstBuilder();
        nodes = builder.buildFromString(document.get());
    }
    
    public GradleScriptASTVisitor walkScript() {
        GradleScriptASTVisitor visitor = new GradleScriptASTVisitor();
        for (ASTNode node : nodes) {
            node.visit(visitor);
        }
        return visitor;
    }
    
}
