package org.mule.tooling.devkit.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewDevkitProjectWizardPage extends WizardPage {

    private static final String DEFAULT_VERSION = "1.0.0-SNAPSHOT";
    private static final String DEFAULT_ARTIFACT_ID = "hello-module";
    private static final String DEFAULT_GROUP_ID = "org.mule.modules.hello";
    private Text groupId;
    private Text artifactId;
    private Text version;

    public NewDevkitProjectWizardPage(ISelection selection) {
        super("wizardPage");
        setTitle("New devkit project");
        setDescription("This wizard creates a new devkit project");
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 2;
        layout.verticalSpacing = 6;
        Label label = new Label(container, SWT.NULL);
        label.setText("&Group id:");

        groupId = new Text(container, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        groupId.setLayoutData(gd);
        groupId.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        label = new Label(container, SWT.NULL);
        label.setText("&Artifact id:");

        artifactId = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        artifactId.setLayoutData(gd);
        artifactId.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        label = new Label(container, SWT.NULL);
        label.setText("&Version:");

        version = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        version.setLayoutData(gd);
        version.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        initialize();
        dialogChanged();
        setControl(container);
    }

    private void initialize() {
        groupId.setText(DEFAULT_GROUP_ID);
        artifactId.setText(DEFAULT_ARTIFACT_ID);
        version.setText(DEFAULT_VERSION);
    }

    private void dialogChanged() {

        String version = getVersion();

        if (version.length() == 0) {
            updateStatus("Version must be specified");
            return;
        }

        updateStatus(null);
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getGroupId() {
        return groupId.getText();
    }

    public String getArtifactId() {
        return artifactId.getText();
    }

    public String getVersion() {
        return version.getText();
    }

}