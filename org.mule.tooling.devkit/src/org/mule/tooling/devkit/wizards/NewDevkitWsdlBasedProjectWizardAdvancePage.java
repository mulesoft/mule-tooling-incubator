package org.mule.tooling.devkit.wizards;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.mule.tooling.devkit.builder.IModelPopulator;
import org.mule.tooling.devkit.builder.ProjectBuilder;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.ui.ConnectorIconPanel;

public class NewDevkitWsdlBasedProjectWizardAdvancePage extends WizardPage implements IModelPopulator<ProjectBuilder> {

    private ConnectorIconPanel iconPanel;

    private Button none;
    private Button httpBasic;
    private Button httpBasicAtTransport;
    private Group securityGroup;

    public NewDevkitWsdlBasedProjectWizardAdvancePage() {
        super("wizardPage");
        setTitle("Create an Anypoint Connector Project");
        setDescription("Define WSDL security and connector icon.");
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

        iconPanel = new ConnectorIconPanel();
        iconPanel.createControl(container);

        securityGroup = new Group(container, SWT.NONE);
        securityGroup.setText("Security");

        none = new Button(securityGroup, SWT.RADIO);
        none.setSelection(true);
        none.setText(AuthenticationType.NONE.label());
        none.setBounds(10, 5, 75, 30);

        httpBasic = new Button(securityGroup, SWT.RADIO);
        httpBasic.setText(AuthenticationType.HTTP_BASIC.label());
        httpBasic.setBounds(10, 30, 75, 30);

        httpBasicAtTransport = new Button(securityGroup, SWT.RADIO);
        httpBasicAtTransport.setText(AuthenticationType.WS_SECURITY_USERNAME_TOKEN_PROFILE.label());
        httpBasicAtTransport.setBounds(10, 55, 75, 30);

        GridLayoutFactory.swtDefaults().applyTo(securityGroup);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, false).applyTo(securityGroup);

        setPageComplete(true);
        setControl(container);

    }

    public void hideScurityGroup() {
        securityGroup.setVisible(false);
        setDescription("Define connector icon.");
    }

    public void showScurityGroup() {
        securityGroup.setVisible(true);
        setDescription("Define WSDL security and connector icon.");
    }

    public AuthenticationType getAuthenticationType() {
        if (none.getSelection()) {
            return AuthenticationType.NONE;
        }
        if (httpBasic.getSelection()) {
            return AuthenticationType.HTTP_BASIC;
        } else {
            return AuthenticationType.WS_SECURITY_USERNAME_TOKEN_PROFILE;
        }
    }

    @Override
    public void populate(ProjectBuilder model) {
        final ConnectorIconPanel panel = iconPanel;
        final File tempDir = FileUtils.getTempDirectory();
        final String smallIcon = new File(tempDir, "small.png").getAbsolutePath();
        final String bigIcon = new File(tempDir, "big.png").getAbsolutePath();
        panel.saveTo(smallIcon, bigIcon);

        model.withSmallIcon(smallIcon).withBigIcon(bigIcon);

        model.withAuthenticationType(getAuthenticationType());

    }
}