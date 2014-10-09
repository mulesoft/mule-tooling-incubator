package org.mule.tooling.incubator.gradle.editors.completion.model;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;

import java.util.HashMap;


/**
 * This model class is to simplfy auto completion, it is not meant to be used directly.
 * @author juancavallotti
 *
 */
public class SimplifiedGradleProject extends GroovyObjectSupport {
    
    private String group;
    private String version;
    
    public void apply(HashMap<String, String> arg) {
        throw new UnsupportedOperationException();
    }
    
    public void dependencies(Closure<Void> depsDsl) {
        throw new UnsupportedOperationException();
    }
    
    public void repositories(Closure<Void> reposDsl) {
        
    }
    
    public String getGroup() {
        return group;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }

    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
}
