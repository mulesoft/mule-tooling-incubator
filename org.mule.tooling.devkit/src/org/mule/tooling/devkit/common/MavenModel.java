package org.mule.tooling.devkit.common;

public class MavenModel {

    private String version;
    private String groupId;
    private String artifactId;

    public MavenModel(String version, String groupId, String artifactId) {
        this.version = version;
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

}
