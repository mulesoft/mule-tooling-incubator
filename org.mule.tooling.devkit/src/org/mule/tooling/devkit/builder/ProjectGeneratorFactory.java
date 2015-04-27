package org.mule.tooling.devkit.builder;

import org.eclipse.core.resources.IProject;
import org.mule.tooling.devkit.common.ConnectorMavenModel;

public class ProjectGeneratorFactory {

    private ProjectGeneratorFactory() {

    }

    public static ProjectGenerator newInstance() {
        return newInstance(null);
    }

    public static ProjectGenerator newInstance(ConnectorMavenModel mavenModel) {
        return new ProjectGenerator(mavenModel);
    }
}
