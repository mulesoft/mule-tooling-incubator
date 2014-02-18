package org.mule.tooling.ui.contribution.munit.runtime;

public class MunitLibrary {

    private String path;
    private MunitMavenConfiguration mavenConfiguration;

    public MunitLibrary(String path) {
        this.path = path;
    }

    public MunitMavenConfiguration getMavenConfiguration() {
        return mavenConfiguration;
    }

    public void setMavenConfiguration(MunitMavenConfiguration mavenConfiguration) {
        this.mavenConfiguration = mavenConfiguration;
    }

    public String getPath() {
        return path;
    }

    public boolean hasMavenSupport() {
        return mavenConfiguration != null;
    }
}
