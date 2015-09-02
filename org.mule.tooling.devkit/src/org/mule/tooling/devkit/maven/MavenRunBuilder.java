package org.mule.tooling.devkit.maven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.IJavaProject;

/**
 * Create a Builder to initialize a BaseDevkitGoalRunner
 * 
 */
public class MavenRunBuilder {

    private List<String> arguments;
    private IJavaProject project;
    private String taskName;

    MavenRunBuilder() {
        arguments = new ArrayList<String>();
    }

    public static MavenRunBuilder newMavenRunBuilder() {
        return new MavenRunBuilder();
    }

    /**
     * Returns a Runner with for the specified project and and the arguments specified. If not argument is set the default commands used will be the ones specified at the
     * BaseDevkitGoalRunner.
     * 
     * @return The configured Runner
     */
    public BaseDevkitGoalRunner build() {
        BaseDevkitGoalRunner runner = null;
        if (arguments.isEmpty()) {
            runner = new BaseDevkitGoalRunner(project);
        } else {
            runner = new BaseDevkitGoalRunner(arguments.toArray(new String[0]), project);
        }
        if (!StringUtils.isEmpty(taskName)) {
            runner.setTaskName(taskName);
        }
        return runner;
    }

    /**
     * Adds the argument to the current list of arguments that will be used to run this maven task
     * 
     * @param arg
     *            The argument to be added
     * @return this reference
     */
    public MavenRunBuilder withArg(String arg) {
        arguments.add(arg);
        return this;
    }

    /**
     * Adds all the arguments in this list to the current arguments list
     * 
     * @param args
     * @return this reference
     */
    public MavenRunBuilder withArgs(String[] args) {
        arguments.addAll(Arrays.asList(args));
        return this;
    }

    /**
     * Specifies the Java project in which this project will run
     * 
     * @param project
     *            The project that will be used in the run
     * @return this reference
     * @throws IllegalArgumentException
     *             if the provided project doesn't contains a pom.xml
     */
    public MavenRunBuilder withProject(IJavaProject project) throws IllegalArgumentException {
        if (!project.getProject().getFile("pom.xml").exists())
            throw new IllegalArgumentException("The project[" + project.getProject().getName() + "] doesn't not contain a pom.xml file.");
        this.project = project;

        return this;
    }

    public MavenRunBuilder withTaskName(String taskName) {
        this.taskName = taskName;
        return this;

    }
}
