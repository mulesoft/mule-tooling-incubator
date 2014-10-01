package org.mule.tooling.incubator.gradle.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.commons.lang.ArrayUtils;


public class CollectDependenciesVisitor extends DefaultGradleScriptDescriptorVisitor {
    
    private static final String[] DSL_MODULES_METHOD_NAMES = {"module", "connector", "plugin" };
    
    private static final String GROUP = "group";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final String NOEXT = "noExt";
    private static final String NOCLASSIFIER = "noClassifier";
    
    private List<Dependency> dependencies;
    
    
    public CollectDependenciesVisitor() {
        dependencies = new LinkedList<Dependency>();
    }
    
    @Override
    public void visitComponentsSection(ScriptLine line) {
        
        String lineContent = line.getContent();
        
        //get and remove the first word.
        int methodDelimiter = lineContent.indexOf(' ');
        
        if (methodDelimiter <= 0) {
            //it could be an open parenthesis as well.
            methodDelimiter = lineContent.indexOf('(');
        }
        
        //the line fails to meet our expectations.
        if (methodDelimiter <= 0) {
            return;
        }
        
        String dslMethodName = lineContent.substring(0, methodDelimiter);
        
        //this should be one of 
        if (!ArrayUtils.contains(DSL_MODULES_METHOD_NAMES, dslMethodName)) {
            //not what we would expect
            return;
        }
        
        //get the hash map part
        lineContent = lineContent.substring(methodDelimiter + 1);
        
        StringTokenizer tokenizer = new StringTokenizer(lineContent);
        
        HashMap<String, String> depProperties = new HashMap<String, String>();
        
        while(true) {
            try {
                //we should be able to read the first key.
                String key = tokenizer.nextToken(":");
                String value = tokenizer.nextToken(",");
                
                //remove any junk
                key = key.replace(",", "");
                key = key.trim();
                
                value = value.replace(":", "");
                value = value.replace(")", "");
                value = value.replace("'", "");
                value = value.replace("\"", "");
                value = value.trim();
                
                depProperties.put(key, value);
                
            } catch (NoSuchElementException ex) {
                //no more tokens
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        }
        
        //populate the dependencies;
        Dependency dep = new Dependency();
        dep.setArtifact(depProperties.get(NAME));
        dep.setVersion(depProperties.get(VERSION));
        dep.setGroup(depProperties.get(GROUP));
        dep.setScriptLine(line);
        
        //the classifier might be zip or jar in this case
        if ("true".equals(depProperties.get(NOEXT))) {
            dep.setExtension("jar");
        } else {
            dep.setExtension("zip");
        }
        
        if ("true".equals(depProperties.get(NOCLASSIFIER))) {
            dep.setClassifier(null);
        } else {
            dep.setClassifier("plugin");
        }
        
        if ("connector".equals(dslMethodName) || "module".equals(dslMethodName)) {
            dep.setGroup(Dependency.DEFAULT_GROUP);
        }

        dependencies.add(dep);
    }

    
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    
}
