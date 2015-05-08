package org.mule.tooling.devkit.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.ui.ConnectorIconPanel;

public class NewDevkitWsdlBasedProjectWizardAdvancePage extends WizardPage {

    private ConnectorIconPanel iconPanel;

    private Button none;
    private Button httpBasic;
    private Button httpBasicAtTransport;

    public NewDevkitWsdlBasedProjectWizardAdvancePage() {
        super("wizardPage");
        setTitle("Create an Anypoint SOAP Connect Project");
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

        setIconPanel(new ConnectorIconPanel());
        getIconPanel().createControl(container);

        Group group = new Group(container, SWT.NONE);
        group.setText("Security");

        none = new Button(group, SWT.RADIO);
        none.setSelection(true);
        none.setText(AuthenticationType.NONE.label());
        none.setBounds(10, 5, 75, 30);

        httpBasic = new Button(group, SWT.RADIO);
        httpBasic.setText(AuthenticationType.HTTP_BASIC.label());
        httpBasic.setBounds(10, 30, 75, 30);

        httpBasicAtTransport = new Button(group, SWT.RADIO);
        httpBasicAtTransport.setText(AuthenticationType.WS_SECURITY_USERNAME_TOKEN_PROFILE.label());
        httpBasicAtTransport.setBounds(10, 55, 75, 30);

        GridLayoutFactory.swtDefaults().applyTo(group);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, false).applyTo(group);

        setControl(container);

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

    public ConnectorIconPanel getIconPanel() {
        return iconPanel;
    }

    public void setIconPanel(ConnectorIconPanel iconPanel) {
        this.iconPanel = iconPanel;
    }
}