package org.mule.tooling.devkit.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.mule.tooling.devkit.DevkitImages;
import org.mule.tooling.devkit.builder.ProjectBuilder;
import org.mule.tooling.devkit.builder.ProjectBuilderFactory;
import org.mule.tooling.devkit.common.ApiType;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.ui.ConnectorIconPanel;

public class NewDevkitWsdlBasedProjectWizard extends Wizard implements INewWizard {

    NewDevkitWsdlBasedProjectWizardPage configPage;
    NewDevkitWsdlBasedProjectWizardAdvancePage secondPage;

    public NewDevkitWsdlBasedProjectWizard() {
        super();
        this.setWindowTitle("New Anypoint SOAP Connect Project");
        setNeedsProgressMonitor(true);
        this.setDefaultPageImageDescriptor(DevkitImages.getManaged("", "mulesoft-logo.png"));
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {

    }

    @Override
    public void addPages() {
        configPage = new NewDevkitWsdlBasedProjectWizardPage();
        secondPage = new NewDevkitWsdlBasedProjectWizardAdvancePage();
        addPage(configPage);
        addPage(secondPage);
    }

    @Override
    public IWizardPage getNextPage(IWizardPage currentPage) {
        if (currentPage instanceof NewDevkitWsdlBasedProjectWizardPage) {
            return secondPage;
        }
        return null;
    }

    @Override
    public boolean performFinish() {

        final Map<String, String> wsdlFiles = configPage.getWsdlPath();
        final String name = configPage.getName();
        final String projectName = configPage.getProjectName();
        final String namespace = configPage.getNamespace();
        final String location = configPage.getLocation();
        final AuthenticationType authenticationType = secondPage.getAuthenticationType();
        final ConnectorIconPanel panel = secondPage.getIconPanel();
        final File tempDir = FileUtils.getTempDirectory();
        final String smallIcon = new File(tempDir, "small.png").getAbsolutePath();
        final String bigIcon = new File(tempDir, "big.png").getAbsolutePath();
        panel.saveTo(smallIcon, bigIcon);
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                monitor.beginTask("Parsing WSDL", wsdlFiles.size() + 1);
                ProjectBuilder builder = ProjectBuilderFactory.newInstance();
                String packageName = "org.mule.modules.cloud";
                try {
                    builder.withGroupId("org.mule.modules." + name.toLowerCase()).withArtifactId(namespace).withConnectorClassName(DevkitUtils.createConnectorNameFrom(name))
                            .withConfigClassName("ConnectorConfig").withVersion("1.0.0-SNAPSHOT").withCategory(DevkitUtils.CATEGORY_COMMUNITY).withApiType(ApiType.WSDL)
                            .withProjectName(projectName).withConnectorName(name).withPackageName(packageName).withModuleName(namespace)
                            .withGitUrl("http://github.com/mulesoft/" + namespace).withWsdlFiles(wsdlFiles).withProjectLocation(location)
                            .withAuthenticationType(authenticationType).withSmallIcon(smallIcon).withBigIcon(bigIcon).build(monitor);

                } catch (CoreException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        };
        if (!runInContainer(op)) {
            return false;
        }

        return true;
    }

    private boolean runInContainer(final IRunnableWithProgress work) {
        try {
            getContainer().run(true, true, work);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }

        return true;
    }
}