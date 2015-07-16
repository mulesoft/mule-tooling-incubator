package org.mule.tooling.incubator.devkit.test.sdk;

import static org.junit.Assert.fail;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.mule.tooling.devkit.builder.ProjectBuilder;
import org.mule.tooling.devkit.builder.ProjectBuilderFactory;
import org.mule.tooling.devkit.common.ApiType;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.incubator.devkit.test.AbstractNewDevkitProjectTest;

public class NewDevKitRestOAuth2ProjectTest extends AbstractNewDevkitProjectTest {

    @Override
    public void doBuildProject() {
        IProgressMonitor monitor = new NullProgressMonitor();
        ProjectBuilder builder = ProjectBuilderFactory.newInstance();
        try {
            project = builder.withGroupId("org.mule.modules").withArtifactId("cloud-connector").withConnectorClassName("CloudConnector").withConfigClassName("ConnectorConfig")
                    .withVersion("1.0.0-SNAPSHOT").withCategory(DevkitUtils.CATEGORY_COMMUNITY).withApiType(ApiType.REST).withProjectName("cloud-connector")
                    .withConnectorName("Cloud").withPackageName(packageName).withModuleName("cloud").withGitUrl("http://github.com/mulesoft/cloud")
                    .withAuthenticationType(AuthenticationType.OAUTH_V2).build(monitor);
        } catch (CoreException e) {
            fail(e.getMessage());
        }
    }

    @Override
    protected String getVerificationFolder() {
        return "rest-oauth";
    }

    @Override
    protected boolean hasConfig() {
        return false;
    }
}
