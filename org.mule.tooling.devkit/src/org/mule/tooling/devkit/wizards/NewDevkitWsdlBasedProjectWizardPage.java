package org.mule.tooling.devkit.wizards;

import java.util.Map;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.mule.tooling.devkit.ui.ConnectorProjectWidget;
import org.mule.tooling.devkit.ui.wsdl.WSDLChooserGroup;
import org.mule.tooling.maven.runner.SyncGetResultCallback;
import org.mule.tooling.maven.ui.MavenUIPlugin;
import org.mule.tooling.maven.ui.actions.MavenInstallationTester;
import org.mule.tooling.maven.ui.preferences.MavenPreferences;

public class NewDevkitWsdlBasedProjectWizardPage extends WizardPage {

    private ConnectorProjectWidget project;
    private WSDLChooserGroup group;

    public NewDevkitWsdlBasedProjectWizardPage() {
        super("wizardPage");
        setTitle("Create an Anypoint SOAP Connect Project");
        setDescription("Create an Anypoint SOAP Connect project in the workspace or in an external location.");
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
        layout.verticalSpacing = 6;

        project = new ConnectorProjectWidget();
        project.createControl(container);

        group = new WSDLChooserGroup();
        group.createControl(container);
        setControl(container);
        initialize();
        testMaven();
    }

    protected void testMaven() {
        MavenPreferences preferencesAccessor = MavenUIPlugin.getDefault().getPreferences();
        final MavenInstallationTester mavenInstallationTester = new MavenInstallationTester(preferencesAccessor.getMavenInstallationHome());
        mavenInstallationTester.test(new SyncGetResultCallback() {

            @Override
            public void finished(final int result) {
                super.finished(result);
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        onTestFinished(result);
                    }
                });
            }
        });
    }

    void onTestFinished(final int result) {
        setPageComplete(result == 0);
    }

    private void initialize() {

    }

    public String getName() {
        return project.getName();
    }

    public String getProjectName() {
        return project.getProjectName();
    }

    public String getNamespace() {
        return project.getNameSpace();
    }

    public String getLocation() {
        return project.getLocation();
    }

    public Map<String, String> getWsdlPath() {

        return group.getWsdlFiles();
    }

    public void populateConnectorModel() {

    }
}