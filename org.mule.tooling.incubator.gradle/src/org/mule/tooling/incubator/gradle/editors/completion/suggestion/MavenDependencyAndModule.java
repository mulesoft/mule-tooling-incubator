package org.mule.tooling.incubator.gradle.editors.completion.suggestion;

import org.mule.tooling.core.module.ExternalContributionMuleModule;
import org.mule.tooling.maven.dependency.MavenDependency;


public class MavenDependencyAndModule {
    
    private final MavenDependency mavenDependency;
    private final ExternalContributionMuleModule module;
    public MavenDependencyAndModule(MavenDependency mavenDependency, ExternalContributionMuleModule module) {
        super();
        this.mavenDependency = mavenDependency;
        this.module = module;
    }
    
    public MavenDependency getMavenDependency() {
        return mavenDependency;
    }
    
    public ExternalContributionMuleModule getModule() {
        return module;
    }
    
}
