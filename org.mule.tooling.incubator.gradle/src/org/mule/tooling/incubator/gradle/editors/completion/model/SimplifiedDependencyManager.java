package org.mule.tooling.incubator.gradle.editors.completion.model;


public interface SimplifiedDependencyManager {
    
    public void runtime(SimplifiedDependencyPojo pojo);
    
    public void compile(SimplifiedDependencyPojo pojo);
    
    public void testCompile(SimplifiedDependencyPojo pojo);
    
    public void testRuntime(SimplifiedDependencyPojo pojo);
    
    public void providedCompile(SimplifiedDependencyPojo pojo);
    
    public void providedRuntime(SimplifiedDependencyPojo pojo);
    
    public void providedTestCompile(SimplifiedDependencyPojo pojo);
    
    public void providedTestRuntime(SimplifiedDependencyPojo pojo);
    
    public void fileTree(SimplifiedFileTreePojo pojo);
    
}
