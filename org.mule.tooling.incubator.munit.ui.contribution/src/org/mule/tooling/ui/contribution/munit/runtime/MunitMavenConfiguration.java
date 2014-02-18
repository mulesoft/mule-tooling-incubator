package org.mule.tooling.ui.contribution.munit.runtime;

import org.mule.tooling.maven.dependency.MavenDependency.Scope;
import org.mule.tooling.maven.dependency.PojoMavenDependency;

public class MunitMavenConfiguration {

    private String groupId;
    private String artifactId;
    private String version;

    public MunitMavenConfiguration(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public PojoMavenDependency asPojoDependency() {
        return new PojoMavenDependency(groupId, artifactId, version, Scope.TEST);
    }

}
