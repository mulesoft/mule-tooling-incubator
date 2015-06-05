package org.mule.tooling.incubator.devkit.test.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_JAVA_FOLDER;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Test;
import org.mule.tooling.devkit.builder.ProjectBuilder;
import org.mule.tooling.devkit.builder.ProjectBuilderFactory;
import org.mule.tooling.devkit.common.ApiType;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.common.DevkitUtils;

public class NewDevKitSDKNoneProjectTest {

    IProject project;

    @Test
    public void createConfiguration() throws IOException, CoreException {
        IProgressMonitor monitor = new NullProgressMonitor();
        ProjectBuilder builder = ProjectBuilderFactory.newInstance();
        try {
            String packageName = "org.mule.modules.cloud";
            
            project = builder.withGroupId("org.mule.modules").withArtifactId("cloud-connector").withConnectorClassName("CloudConnector").withConfigClassName("ConnectorConfig")
                    .withVersion("1.0.0-SNAPSHOT").withCategory(DevkitUtils.CATEGORY_COMMUNITY).withApiType(ApiType.GENERIC).withProjectName("cloud-connector")
                    .withConnectorName("Cloud").withPackageName(packageName).withModuleName("cloud").withGitUrl("http://github.com/mulesoft/cloud")
                    .withAuthenticationType(AuthenticationType.NONE).build(monitor);
            assertNotNull("Project was not created", project);
            verifyFolder(project, DevkitUtils.MAIN_JAVA_FOLDER);
            verifyFolder(project, DevkitUtils.TEST_JAVA_FOLDER);
            verifyFolder(project, DevkitUtils.DOCS_FOLDER);
            verifyFolder(project, DevkitUtils.DEMO_FOLDER);
            verifyFolder(project, DevkitUtils.MAIN_RESOURCES_FOLDER);
            verifyFolder(project, DevkitUtils.TEST_RESOURCES_FOLDER);
            verifyFolder(project, DevkitUtils.ICONS_FOLDER);
            
            String packageAsPath = packageName.replaceAll("\\.", "/");
            
            verifyFolder(project, MAIN_JAVA_FOLDER + "/" + packageAsPath);
            verifyFolder(project, MAIN_JAVA_FOLDER + "/" + packageAsPath + "/" + "config");

            verifyFile(project, DevkitUtils.ICONS_FOLDER, "cloud-connector-24x16.png");
            verifyFile(project, DevkitUtils.ICONS_FOLDER, "cloud-connector-48x32.png");

            // Check pom.xml file
            assertEquals("POM.xml files differ!", FileUtils.readFileToString(new File("resources/pom.txt"), "utf-8"),
                    FileUtils.readFileToString(project.getFile("pom.xml").getLocation().toFile(), "utf-8"));

            // Check README.md content
            assertEquals("REAME.md files differ!", FileUtils.readFileToString(new File("resources/README.txt"), "utf-8"),
                    FileUtils.readFileToString(project.getFile("README.md").getLocation().toFile(), "utf-8"));

            // Check @Connector content
            assertEquals("Connector files differ!", FileUtils.readFileToString(new File("resources/sdk-none/CloudConnector.java"), "utf-8"), FileUtils.readFileToString(
                    project.getFolder(MAIN_JAVA_FOLDER + "/" + packageAsPath).getFile("CloudConnector.java").getLocation().toFile(), "utf-8"));

            // Check @Config content
            assertEquals(
                    "Config files differ!",
                    FileUtils.readFileToString(new File("resources/sdk-none/ConnectorConfig.java"), "utf-8"),
                    FileUtils.readFileToString(project.getFolder(MAIN_JAVA_FOLDER + "/" + packageAsPath + "/config").getFile("ConnectorConfig.java")
                            .getLocation().toFile(), "utf-8"));

        } catch (CoreException e) {
            fail("Something went wrong");
        }
    }

    private void verifyFolder(IProject project, String folderName) {
        assertTrue(folderName + " folder was not created", project.getFolder(folderName).exists());
    }

    private void verifyFile(IProject project, String folderName, String fileName) {
        assertTrue(fileName + " file was not created", project.getFolder(folderName).getFile(fileName).getLocation().toFile().exists());
    }

    @After
    public void tearDown() {
        try {
            if (project != null) {
                project.delete(true, new NullProgressMonitor());
            }
        } catch (CoreException e) {
            // ignore this error.
        }
    }
}
