package org.mule.tooling.devkit.builder;

public class ProjectGeneratorFactory {

    private ProjectGeneratorFactory() {

    }

    public static ProjectGenerator newInstance() {
        return new ProjectGenerator();
    }
}
