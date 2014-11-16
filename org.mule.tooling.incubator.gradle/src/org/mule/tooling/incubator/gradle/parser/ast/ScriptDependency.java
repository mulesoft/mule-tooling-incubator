package org.mule.tooling.incubator.gradle.parser.ast;

import org.codehaus.groovy.ast.ASTNode;
import org.mule.tooling.incubator.gradle.parser.Dependency;


public class ScriptDependency extends Dependency {
    
    private static final String GROUP = "group";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final String NOEXT = "noExt";
    private static final String NOCLASSIFIER = "noClassifier";
    private static final String CLASSIFIER = "classifier";
    private static final String EXT = "ext";
    
    private ASTNode sourceNode;
    
    public ScriptDependency() {
        // TODO Auto-generated constructor stub
    }

    public ScriptDependency(ScriptMap dep) {
        setGroup(dep.get(GROUP));
        setArtifact(dep.get(NAME));
        setVersion(dep.get(VERSION));
        if ("true".equals(dep.get(NOEXT))) {
            setExtension("jar");
        } else {
            setExtension("zip");
        }
                
        if ("true".equals(dep.get(NOCLASSIFIER))) {
            setClassifier(null);
        } else {
            setClassifier("plugin");
        }
        
        //it might have defined its own classifier
        if (dep.containsKey(CLASSIFIER)) {
            setClassifier(dep.get(CLASSIFIER));
        }

        //it might have defined its own classifier
        if (dep.containsKey(EXT)) {
            setExtension(dep.get(EXT));;
        }
        
        setSourceNode(dep.getSourceNode());
    }

    
    public ASTNode getSourceNode() {
        return sourceNode;
    }

    
    public void setSourceNode(ASTNode sourceNode) {
        this.sourceNode = sourceNode;
    }
    
}
