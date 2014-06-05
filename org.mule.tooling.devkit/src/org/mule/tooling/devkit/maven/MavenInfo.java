package org.mule.tooling.devkit.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MavenInfo {

    public MavenInfo() {
        this("", "", "");
    }

    public MavenInfo(String groupId, String artifactId, String version) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        modules = new ArrayList<MavenInfo>();
    }

    List<MavenInfo> modules;
    String groupId;
    String artifactId;
    String version;
    String packaging;
    private File projectRoot;

    public boolean hasChilds() {
        return !modules.isEmpty();
    }

    public List<MavenInfo> getModules() {
        return modules;
    }

    public void addModule(MavenInfo module) {
        this.modules.add(module);
    }

    public void setModules(List<MavenInfo> modules) {
        this.modules = modules;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String toString() {
        return groupId + ":" + artifactId + ":" + version + "(" + modules.size() + ")";
    }

    public File getProjectRoot() {
        return projectRoot;
    }

    public void setProjectRoot(File projectRoot) {
        this.projectRoot = projectRoot;
    }
}
