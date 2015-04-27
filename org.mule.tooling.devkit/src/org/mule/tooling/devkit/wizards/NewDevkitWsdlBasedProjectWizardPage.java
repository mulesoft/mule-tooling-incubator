package org.mule.tooling.devkit.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.mule.tooling.devkit.common.ConnectorMavenModel;
import org.mule.tooling.devkit.ui.ConnectorIconPanel;
import org.mule.tooling.devkit.ui.ConnectorProjectWidget;
import org.mule.tooling.devkit.ui.WsdlChooser;
import org.mule.tooling.maven.runner.SyncGetResultCallback;
import org.mule.tooling.maven.ui.MavenUIPlugin;
import org.mule.tooling.maven.ui.actions.MavenInstallationTester;
import org.mule.tooling.maven.ui.preferences.MavenPreferences;

public class NewDevkitWsdlBasedProjectWizardPage extends WizardPage {

    private ConnectorMavenModel model;
    private ConnectorProjectWidget project;
    private WsdlChooser wsdlChooser;
    private ConnectorIconPanel iconPanel;

    public NewDevkitWsdlBasedProjectWizardPage(ConnectorMavenModel model) {
        super("wizardPage");
        setTitle("Create an Anypoint Wsdl Based Connector");
        setDescription("Create an Anypoint Wsdl Based Connector project.");
        this.model = model;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 6;

        project = new ConnectorProjectWidget();
        project.createControl(container);

        wsdlChooser = new WsdlChooser();
        wsdlChooser.createControl(container);

        iconPanel = new ConnectorIconPanel();
        iconPanel.createControl(container);

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

    }

    private void initialize() {

    }

    public List<String> getWsdlPath() {
        List<String> urls = new ArrayList<String>();
        String path = this.wsdlChooser.getWsdlPath();
        if (path.startsWith("http")) {
            urls.addAll(Arrays.asList(path.split(",")));
        }
        return urls;
    }
}