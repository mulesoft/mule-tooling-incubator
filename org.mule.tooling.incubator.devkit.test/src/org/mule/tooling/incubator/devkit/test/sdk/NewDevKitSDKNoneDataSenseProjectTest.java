package org.mule.tooling.incubator.devkit.test.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_JAVA_FOLDER;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import org.mule.tooling.devkit.builder.ProjectBuilder;
import org.mule.tooling.devkit.builder.ProjectBuilderFactory;
import org.mule.tooling.devkit.common.ApiType;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.incubator.devkit.test.AbstractNewDevkitProjectTest;

public class NewDevKitSDKNoneDataSenseProjectTest extends AbstractNewDevkitProjectTest {

    @Override
    public void doBuildProject() {
        IProgressMonitor monitor = new NullProgressMonitor();
        ProjectBuilder builder = ProjectBuilderFactory.newInstance();
        try {
            project = builder.withGroupId("org.mule.modules").withArtifactId("cloud-connector").withConnectorClassName("CloudConnector").withConfigClassName("ConnectorConfig")
                    .withVersion("1.0.0-SNAPSHOT").withCategory(DevkitUtils.CATEGORY_COMMUNITY).withApiType(ApiType.GENERIC).withProjectName("cloud-connector")
                    .withConnectorName("Cloud").withPackageName(packageName).withModuleName("cloud").withGitUrl("http://github.com/mulesoft/cloud").withDataSenseEnabled(true)
                    .withHasQuery(true).withAuthenticationType(AuthenticationType.NONE).build(monitor);
        } catch (CoreException e) {
            fail(e.getMessage());
        }
    }

    @Override
    protected String getVerificationFolder() {
        return "sdk-none-metadata";
    }

    @Test
    public void verifyDataSenseResolver() throws IOException, CoreException {
        initProject();

        // Check DataSense file
        assertEquals(
                "DataSense Resolver files differ!",
                FileUtils.readFileToString(new File("resources/sdk-none-metadata/DataSenseResolver.java"), "utf-8").replace("\r\n", "\n"),
                FileUtils.readFileToString(project.getFolder(MAIN_JAVA_FOLDER + "/" + packageAsPath).getFile("DataSenseResolver.java").getLocation().toFile(), "utf-8").replace(
                        "\r\n", "\n"));

    }

}
