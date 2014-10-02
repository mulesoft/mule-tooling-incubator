package org.mule.tooling.incubator.gradle.parser;

import java.util.HashMap;


public class DSLMethodAndMap {
    
    private String methodName;
    private HashMap<String, String> arguments;
    
    public DSLMethodAndMap() {
        
    }
    
    public DSLMethodAndMap(String methodName, HashMap<String, String> arguments) {
        this.methodName = methodName;
        this.arguments = arguments;
    }

    
    public String getMethodName() {
        return methodName;
    }

    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    
    public HashMap<String, String> getArguments() {
        return arguments;
    }

    
    public void setArguments(HashMap<String, String> arguments) {
        this.arguments = arguments;
    }
    
}
