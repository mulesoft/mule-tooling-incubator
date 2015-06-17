package org.mule.tooling.incubator.devkit.test.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_JAVA_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_RESOURCES_FOLDER;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import org.mule.tooling.incubator.devkit.test.AbstractNewDevkitProjectTest;

public class NewDevKitWsdlBasedWSAuthenticationProjectTest extends AbstractNewDevkitProjectTest {

    @Override
    public void doBuildProject() {
        IProgressMonitor monitor = new NullProgressMonitor();
        ProjectBuilder builder = ProjectBuilderFactory.newInstance();
        try {
            Map<String, String> wsdlFiles = new HashMap<String, String>();
            wsdlFiles.put(new File("resources/enterprise.wsdl").getAbsolutePath(), "Salesforce API");
            project = builder.withGroupId("org.mule.modules").withArtifactId("cloud-connector").withConnectorClassName("CloudConnector").withConfigClassName("ConnectorConfig")
                    .withVersion("1.0.0-SNAPSHOT").withCategory(DevkitUtils.CATEGORY_COMMUNITY).withApiType(ApiType.WSDL).withProjectName("cloud-connector")
                    .withConnectorName("Cloud").withPackageName(packageName).withModuleName("cloud").withGitUrl("http://github.com/mulesoft/cloud").withWsdlFiles(wsdlFiles)
                    .withAuthenticationType(AuthenticationType.WS_SECURITY_USERNAME_TOKEN_PROFILE).build(monitor);
        } catch (CoreException e) {
            fail(e.getMessage());
        }
    }

    @Override
    protected String getVerificationFolder() {
        return "wsdl-ws-security";
    }

    @Test
    public void verifyWsdlWasCopied() throws IOException, CoreException {
        // Check WSDL File was copied
        initProject();
        assertEquals("WSDL files differ!", FileUtils.readFileToString(new File("resources/enterprise.wsdl"), "utf-8"),
                FileUtils.readFileToString(project.getFolder(MAIN_RESOURCES_FOLDER + "/wsdl").getFile("enterprise.wsdl").getLocation().toFile(), "utf-8"));
    }

}
