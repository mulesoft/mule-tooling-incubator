package org.mule.tooling.devkit.ui;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class SelectWSDLDialog extends TitleAreaDialog {

    WsdlChooser chooser;
    String wsdl;

    public SelectWSDLDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Select the WSDL Location");
        setMessage("Specify the location of the WSDL you want to build a connector for", IMessageProvider.INFORMATION);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        chooser = new WsdlChooser();
        chooser.createControl(area);

        return area;
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    private void saveInput() {
        wsdl = chooser.getWsdlPath();
    }

    @Override
    protected void okPressed() {
        saveInput();
        super.okPressed();
    }

    public String getWsdlLocation() {
        return wsdl;
    }

}