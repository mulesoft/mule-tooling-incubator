package org.mule.tooling.incubator.gradle.parser;


public interface GradleScriptDescriptorVisitor {
    
    void visitComponentsSection(ScriptLine line);
    
}
