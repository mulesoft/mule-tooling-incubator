package org.mule.tooling.devkit.popup.actions;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.dialogs.InteropRunOptionsSelectionDialog;
import org.mule.tooling.devkit.maven.MavenUtils;
import org.mule.tooling.devkit.popup.dto.InteropConfigDto;

public class RunAsRemoteInteropCommand extends AbstractMavenCommandRunner {

    private static final String SUREFIRE_XML = "target/surefire-reports/TEST-suite.ConnectorsInteropTestSuite.xml";
    private static final String GENERATED_REPORTS_PATH = "interop-ce-project/ce-interop-testsuite/target/surefire-reports";
    
    private InteropConfigDto runnerConfig;
    private String projectPath;
    private String projectTarget;
    private IProject selectedProject;

    @Override
    protected void doCommandJobOnProject(IProject selectedProject) {
        this.selectedProject = selectedProject;
        this.projectPath = selectedProject.getLocationURI().getPath();
        this.projectTarget = projectPath + "/target/";
        this.runnerConfig = new InteropConfigDto();

        setTestdataFilesInProjectAsDefault(selectedProject);

        setProjectInformation();
       
        if (getConfigurationAndContinue()) {
            if (!runnerConfig.runAsLocal()) {
                runAsRemoteTest();
            } else {
                runAsLocalTest(selectedProject);
            }
        }        
    }
 
    private void setProjectInformation() {
        
        runnerConfig.setRepository(MavenUtils.getProjectScmUrl(new File(projectPath+"/pom.xml")));
        for(String branch: DevkitUtils.getProjectBranches(selectedProject, ListMode.REMOTE)){
            runnerConfig.addBranch(branch);
        }

    }

    private Boolean getConfigurationAndContinue() {

        InteropRunOptionsSelectionDialog dialog = new InteropRunOptionsSelectionDialog(Display.getCurrent().getActiveShell(), runnerConfig);
        int returnStatus = dialog.open();

        runnerConfig = dialog.getConfig();

        return returnStatus == 0;
    }

    
    private void runAsLocalTest(final IProject selectedProject) {
        String jobMsg = "Generating Test Project...";

        String[] mavenCommand = new String[] { "org.mule.connectors.interop:interop-ce-runtime-generation:create"};
        
        System.out.println("** Command :: " + StringUtils.join(mavenCommand, " "));
        
        MavenUtils.runMavenGoalJob(selectedProject, mavenCommand, jobMsg, DevkitUtils.refreshFolder(selectedProject.getFolder(DevkitUtils.TEST_RESOURCES_FOLDER), null));
        

        mavenCommand = new String[] {"install",
                                     "-f", projectPath + "/target/interop-ce-project/pom.xml",
                                     "-Dsuite.testData=" + runnerConfig.getTestDataPath(), 
                                     "-Dsuite.testDataOverride=" + runnerConfig.getTestDataOverridePath(), 
                                     "-Dsuite.testConnect=" + runnerConfig.getRunConnectivityTest(),
                                     "-Dsuite.testDMapper=" + runnerConfig.getRunDMapperTest(),
                                     "-Dsuite.testXml=" + runnerConfig.getRunXmlTest()};
        
        jobMsg = "Running Interop Test";
        MavenUtils.runMavenGoalJob(selectedProject, mavenCommand, jobMsg, new DevkitCallback() {
   
                @Override
                public int execute(int previousResult) {
                    try {
                        File report = new File(projectTarget + GENERATED_REPORTS_PATH);
                        File reportTarget = new File(projectTarget + "surefire-reports");
                        
                        FileUtils.deleteDirectory(reportTarget);
                        FileUtils.moveDirectory(report, reportTarget);
                        System.out.println("Moved Report");
                        
                        FileUtils.deleteDirectory(new File(projectTarget + "interop-ce-project"));
                        System.out.println("Delete dir");
                    } catch (IOException e) {
                        e.printStackTrace();
                        return Status.ERROR;
                    }

                    DevkitUtils.refreshFolder(selectedProject.getFolder("target"),null).execute(Status.OK);

                    try {
                        // Workaround for refresh delay of folder resource on viewPart
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {}

                    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

                        @Override
                        public void run() {
                            IWorkbenchPage page = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage();
                            IViewPart viewPart = page.findView("org.eclipse.jdt.ui.PackageExplorer");

                            Viewer selectionService = (Viewer) viewPart.getSite().getSelectionProvider();
                            TreePath[] tp = new TreePath[]{new TreePath(new Object[] { selectedProject.getFile(SUREFIRE_XML)})};
                            TreeSelection selection = new TreeSelection(tp);
                            selectionService.setSelection(selection, true);
                        }
                    });
                    
                    return Status.OK;
                }
            });
    }

    private void runAsRemoteTest() {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost httppost = createPostRequest();

            System.out.println("executing request " + httppost.getRequestLine());

            HttpResponse response = client.execute(httppost);
            showResponseInformationDialog(response);

            System.out.println(response.getEntity().toString());

        } catch (ConnectException e) {
            e.printStackTrace();
            MessageDialog.openError(Display.getCurrent().getActiveShell(), "Unexpected error occurred", e.getLocalizedMessage());
            throw new RuntimeException(e.getMessage());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            MessageDialog.openError(Display.getCurrent().getActiveShell(), "Unexpected error occurred", e.getLocalizedMessage());
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            MessageDialog.openError(Display.getCurrent().getActiveShell(), "Unexpected error occurred", e.getLocalizedMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

        
    private void setTestdataFilesInProjectAsDefault(final IProject selectedProject) {
        try {

            final String testDataNameFormat = ".*test(d|D)ata\\.xml";
            final String testDataOverrideNameFormat = ".*test(d|D)ata.*(o|O)verride\\.xml";

            String testDataPath = "";
            String testDataOverridePath = "";

            IContainer testResourcesFolder = selectedProject.getFolder(DevkitUtils.TEST_RESOURCES_FOLDER);
            IContainer generatedResourcesFolder = testResourcesFolder.getFolder(new Path("generated"));

            testDataPath = DevkitUtils.findResourceInFolder(testResourcesFolder, testDataNameFormat);
            testDataOverridePath = DevkitUtils.findResourceInFolder(testResourcesFolder, testDataOverrideNameFormat);

            if (testDataPath.equals("")) {
                testDataPath = DevkitUtils.findResourceInFolder(generatedResourcesFolder, testDataNameFormat);                
            }

            if (testDataOverridePath.equals("")) {
                testDataOverridePath = DevkitUtils.findResourceInFolder(generatedResourcesFolder, testDataOverrideNameFormat);
            }

            runnerConfig.setTestDataPath(testDataPath);
            runnerConfig.setTestDataOverridePath(testDataOverridePath);

        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    private HttpPost createPostRequest() throws UnsupportedEncodingException {

        HttpPost httppost = new HttpPost(runnerConfig.getServerURL());
        File testData = new File(runnerConfig.getTestDataPath());
        File override = new File(runnerConfig.getTestDataOverridePath());

        MultipartEntityBuilder mpBuilder = MultipartEntityBuilder.create();

        mpBuilder.addTextBody("email", runnerConfig.getDestinationEmail());
        mpBuilder.addTextBody("debug", runnerConfig.getSelectedDebug().toString());
        mpBuilder.addTextBody("logLevel", getLogLevel());
        mpBuilder.addTextBody("connectorRepository", runnerConfig.getRepository());
        mpBuilder.addTextBody("ooss", getSelectedOS());

        mpBuilder.addBinaryBody("testDataFile", testData, ContentType.APPLICATION_XML, testData.getName());
        mpBuilder.addBinaryBody("testDataOverrideFile", override, ContentType.APPLICATION_XML, override.getName());

        httppost.setEntity(mpBuilder.build());

        return httppost;
    }

    private void showResponseInformationDialog(HttpResponse response) throws IOException {
        String message = EntityUtils.toString(response.getEntity());

        switch (response.getStatusLine().getStatusCode()) {
        case HttpStatus.SC_OK:
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Server response", message);
            break;

        case HttpStatus.SC_NOT_FOUND:
            MessageDialog.openError(Display.getCurrent().getActiveShell(), "Server response", "An unexpected error occurred: " + response.getStatusLine());
            break;

        case HttpStatus.SC_REQUEST_TIMEOUT:
            MessageDialog.openError(Display.getCurrent().getActiveShell(), "Server Timeout", "The connection timed out");
            break;

        default:
            MessageDialog.openError(Display.getCurrent().getActiveShell(), "Unexpected error", "An unexpected error occurred: " + response.getStatusLine());
            break;
        }
    }

    private String getSelectedOS() {

        Boolean selectedLinux = new Boolean(runnerConfig.getSelectedLinux());
        Boolean selectedWindows = new Boolean(runnerConfig.getSelectedWindows());
        
        return (selectedLinux && selectedWindows ? "both" : (selectedLinux ? "linux" : "windows"));
    }

    private String getLogLevel() {
        return (new Boolean(runnerConfig.getSelectedVerbose()) ? "verbose" : "info");
    }

}
