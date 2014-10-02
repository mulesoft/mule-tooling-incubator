package org.mule.tooling.incubator.gradle.parser;

import java.util.List;

/**
 * Models the Gradle script and more specifically applied to mule.
 * @author juancavallotti
 *
 */
public class GradleScriptDescriptor {
    
    private List<ScriptLine> muleComponentsSection;
    
    public List<ScriptLine> getMuleComponentsSection() {
        return muleComponentsSection;
    }
    
    public void setMuleComponentsSection(List<ScriptLine> muleComponentsSection) {
        this.muleComponentsSection = muleComponentsSection;
    }
    
    public void visit(GradleScriptDescriptorVisitor visitor) {
        //basically, walk all the model.
        for (ScriptLine line : muleComponentsSection) {
            visitor.visitComponentsSection(line);
        }
    }
    
}
