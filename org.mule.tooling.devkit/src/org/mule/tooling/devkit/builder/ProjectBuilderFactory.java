package org.mule.tooling.devkit.builder;

public class ProjectBuilderFactory {

    private ProjectBuilderFactory() {

    }

    public static ProjectBuilder newInstance() {
        return new ProjectBuilder();
    }

}
