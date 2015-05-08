package org.mule.tooling.devkit.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.mule.tooling.devkit.common.DevkitUtils;

public abstract class AbstractDevkitProjectWizzard extends Wizard {

    protected IProject getProjectWithDescription(String artifactId, IProgressMonitor monitor, IWorkspaceRoot root, IProjectDescription projectDescription) throws CoreException {
        IProject project = root.getProject(artifactId);
        if (!project.exists()) {
            project.create(projectDescription, monitor);
            project.open(monitor);
            project.setDescription(projectDescription, monitor);
        }
        return project;
    }

    protected String getResourceExampleFileName(String namespace) {
        return DevkitUtils.TEST_RESOURCES_FOLDER + "/" + namespace + "-config.xml";
    }

    protected String getIcon48FileName(String namespace) {
        return "icons/" + namespace + "-connector-48x32.png";
    }

    protected String getIcon24FileName(String namespace) {
        return "icons/" + namespace + "-connector-24x16.png";
    }

    protected String getExampleFileName(String namespace) {
        return "doc" + "/" + namespace + "-connector.xml.sample";
    }
    
    protected String buildMainTargetFilePath(final String packageName, String className) {
        return DevkitUtils.MAIN_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + className + ".java";
    }

    protected String buildTestParentFilePath(final String packageName, String className) {
        return DevkitUtils.TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation/" + className + "TestParent.java";
    }
    
    protected String buildRegressionTestsFilePath(final String packageName, String className) {
        return DevkitUtils.TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation/testrunners/RegressionTestSuite.java";
    }
    
    protected String buildTestTargetFilePath(final String packageName, String className) {
        return DevkitUtils.TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation/testcases/GreetTestCases.java";
    }

    protected String buildQueryTestTargetFilePath(final String packageName, String className) {
        return DevkitUtils.TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation/testcases/QueryProcessorTestCases.java";
    }

    protected String buildDataSenseTestTargetFilePath(final String packageName, String className) {
        return DevkitUtils.TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation/testcases/AddEntityTestCases.java";
    }
}
