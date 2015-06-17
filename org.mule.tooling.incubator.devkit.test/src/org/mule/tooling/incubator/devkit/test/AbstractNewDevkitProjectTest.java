package org.mule.tooling.incubator.devkit.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_JAVA_FOLDER;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.Test;
import org.mule.tooling.devkit.common.DevkitUtils;


public abstract class AbstractNewDevkitProjectTest {

    protected static IProject project;
    protected static String packageName;
    protected static String packageAsPath;
    
    public void initProject(){
        if(project==null || !project.exists()){
            packageName = "org.mule.modules.cloud";
            packageAsPath = packageName.replaceAll("\\.", "/");
            doBuildProject();
        }
    }
    
    protected abstract void doBuildProject();

    protected void verifyFolder(IProject project, String folderName) {
        initProject();
        assertTrue(folderName + " folder was not created", project.getFolder(folderName).exists());
    }

    protected void verifyFile(IProject project, String folderName, String fileName) {
        initProject();
        assertTrue(fileName + " file was not created", project.getFolder(folderName).getFile(fileName).getLocation().toFile().exists());
    }

    @Test
    public void verifyProjectWasCreated() throws IOException, CoreException {
        initProject();
        assertNotNull("Project was not created", project);
    }
    

    @Test
    public void verifyBasicFoldersAndFiles() throws IOException {
        initProject();
        verifyFolder(project, DevkitUtils.MAIN_JAVA_FOLDER);
        verifyFolder(project, DevkitUtils.TEST_JAVA_FOLDER);
        verifyFolder(project, DevkitUtils.DOCS_FOLDER);
        verifyFolder(project, DevkitUtils.DEMO_FOLDER);
        verifyFolder(project, DevkitUtils.MAIN_RESOURCES_FOLDER);
        verifyFolder(project, DevkitUtils.TEST_RESOURCES_FOLDER);
        verifyFolder(project, DevkitUtils.ICONS_FOLDER);

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

    }

    @Test
    public void verifyConnector() throws IOException {
        initProject();
        // Check @Connector content
        assertEquals("Connector files differ!", FileUtils.readFileToString(new File("resources/"+getVerificationFolder()+"/CloudConnector.java"), "utf-8"),
                FileUtils.readFileToString(project.getFolder(MAIN_JAVA_FOLDER + "/" + packageAsPath).getFile("CloudConnector.java").getLocation().toFile(), "utf-8"));
    }

    protected abstract String getVerificationFolder();

    @Test
    public void verifyConfiguration() throws IOException {
        initProject();
        // Check @Config content
        assertEquals("Config files differ!", FileUtils.readFileToString(new File("resources/" + getVerificationFolder() + "/ConnectorConfig.java"), "utf-8"),
                FileUtils.readFileToString(project.getFolder(MAIN_JAVA_FOLDER + "/" + packageAsPath + "/config").getFile("ConnectorConfig.java").getLocation().toFile(), "utf-8"));
    }
    
    @AfterClass
    public static void tearDown() {
        try {
            if (project != null) {
                project.delete(true, new NullProgressMonitor());
            }
        } catch (CoreException e) {
            // ignore this error.
        }
    }
}
