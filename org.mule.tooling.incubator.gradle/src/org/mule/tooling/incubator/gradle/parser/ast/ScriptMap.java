package org.mule.tooling.incubator.gradle.parser.ast;

import java.util.HashMap;

import org.codehaus.groovy.ast.ASTNode;

/**
 * Represents a map that has been read from the script, a little bit more digested 
 * from its script raw notation and includes the line number.
 * 
 * @author juancavallotti
 * 
 */
public class ScriptMap extends HashMap<String, String> {
    
    private static final long serialVersionUID = 1L;

    private ASTNode sourceNode;

    
    public ASTNode getSourceNode() {
        return sourceNode;
    }

    
    public void setSourceNode(ASTNode sourceNode) {
        this.sourceNode = sourceNode;
    }
    
}
