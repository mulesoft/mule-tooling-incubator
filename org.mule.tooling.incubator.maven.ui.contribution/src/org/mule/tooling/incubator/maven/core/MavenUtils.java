package org.mule.tooling.incubator.maven.core;

import org.eclipse.core.resources.IProject;


public class MavenUtils {
    public static boolean isMavenBased(IProject project) {
        if (project == null)
            throw new IllegalArgumentException("project == null");
        return project.getFile("pom.xml").exists();
    }
}
