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

    private Button sdk;
    private Button soap;

    public NewDevkitProjectWizardApiPage() {
        super("apiWizardPage");
        setTitle(NewDevkitProjectWizard.WIZZARD_PAGE_TITTLE);
        setDescription("Select an Anypoint Connector project type.");
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayoutFactory.fillDefaults().numColumns(2).spacing(6, 6).applyTo(container);

        // Group mavenGroupBox = UiUtils.createGroupWithTitle(container, "Connector Type", 2);
        sdk = initButton(container, "SDK Based", SWT.RADIO);
        sdk.setImage(DevkitImages.getManagedImage("", "cloud-tools.png"));
        sdk.setSelection(true);

        Label textField = new Label(container, SWT.WRAP);
        textField.setText("To build a connector using Java library,  Apache CXF (WSDL) or Jersey client (REST API), choose this option.");
        GridDataFactory.swtDefaults().grab(true, false).hint(300, SWT.DEFAULT).applyTo(textField);

        Label separator = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).span(2, 1).applyTo(separator);

        soap = initButton(container, "SOAP Connect", SWT.RADIO);
        soap.setImage(DevkitImages.getManagedImage("", "cloud-envelope-open.png"));

        Label soapText = new Label(container, SWT.WRAP);
        soapText.setText("To package multiple WSDL files and API versions into a connector with minimum coding, choose this option.");
        GridDataFactory.swtDefaults().grab(true, false).hint(300, SWT.DEFAULT).applyTo(soapText);

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(container);

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
        if (sdk.getSelection()) {
            return "JDK";
        }
        return "SOAP";
    }

}