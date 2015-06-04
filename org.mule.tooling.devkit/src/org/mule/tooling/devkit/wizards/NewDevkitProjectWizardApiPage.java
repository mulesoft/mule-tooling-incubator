package org.mule.tooling.devkit.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.mule.tooling.devkit.DevkitImages;

public class NewDevkitProjectWizardApiPage extends WizardPage {

    private Button jdk;
    private Button soap;

    public NewDevkitProjectWizardApiPage() {
        super("apiWizardPage");
        setTitle(NewDevkitProjectWizard.WIZZARD_PAGE_TITTLE);
        setDescription("Create an Anypoint Connector project.");
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayoutFactory.fillDefaults().numColumns(2).spacing(6, 6).applyTo(container);

        // Group mavenGroupBox = UiUtils.createGroupWithTitle(container, "Connector Type", 2);
        jdk = initButton(container, "JDK", SWT.RADIO);
        jdk.setImage(DevkitImages.getManagedImage("", "cloud-tools.png"));
        jdk.setSelection(true);

        Label textField = new Label(container, SWT.WRAP);
        textField
                .setText("Create an Anypoint Connector project using an SDK. Choose this options if you have an SDK or want to create a client using CXF or Jersey.");
        GridDataFactory.swtDefaults().grab(true, false).hint(300, SWT.DEFAULT).applyTo(textField);

        Label separator = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).span(2, 1).applyTo(separator);

        soap = initButton(container, "SOAP", SWT.RADIO);
        soap.setImage(DevkitImages.getManagedImage("", "cloud-envelope-open.png"));

        Label soapText = new Label(container, SWT.WRAP);
        soapText.setText("Create an Anypoint Connector project using SOAP Connect feature. Choose this options if you have one or more WSDLs files want to use .");
        GridDataFactory.swtDefaults().grab(true, false).hint(300, SWT.DEFAULT).applyTo(soapText);

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(container);

        setPageComplete(true);
        
        setControl(container);
    }

    private Button initButton(Composite mavenGroupBox, String title, int buttonType) {
        Button cbCreatePomCheckbox = new Button(mavenGroupBox, buttonType);
        cbCreatePomCheckbox.setSelection(false);
        cbCreatePomCheckbox.setText(" " + title);
        cbCreatePomCheckbox.setLayoutData(GridDataFactory.swtDefaults().create());
        cbCreatePomCheckbox.setSelection(false);
        return cbCreatePomCheckbox;
    }

    public String getConnectorType() {
        if (jdk.getSelection()) {
            return "JDK";
        }
        return "SOAP";
    }

}