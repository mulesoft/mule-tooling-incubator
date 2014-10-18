package org.mule.tooling.incubator.gradle.editors.completion.model;


public interface SimplifiedFileTreePojo {
    
    public String getDir();
    
    public String getInclude();
    
    public String[] getIncludes();
    
    public String getExclude();
    
    public String[] getExcludes();
}
