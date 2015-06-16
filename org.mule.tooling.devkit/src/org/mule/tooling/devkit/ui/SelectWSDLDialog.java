package org.mule.tooling.devkit.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.codehaus.plexus.util.FileUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class SelectWSDLDialog extends TitleAreaDialog {

    WsdlChooser chooser;
    String wsdl;
    private int mode;

    public SelectWSDLDialog(Shell parentShell) {
        this(parentShell, WsdlChooser.ALL);
    }

    public SelectWSDLDialog(Shell parentShell, int mode) {
        super(parentShell);
        this.mode = mode;
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
        chooser = new WsdlChooser(mode);
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
        setErrorMessage("");
        if (wsdl.startsWith("http")) {
            if (!isValidURL(wsdl)) {
                setErrorMessage("The provided url is not valid");
                return;
            }
        } else {
            if (!FileUtils.fileExists(wsdl)) {
                if (!new File(wsdl).exists()) {
                    setErrorMessage("The selected file or folder doesn't exists");
                    return;
                }
            }
        }
        super.okPressed();
    }

    public void setWsdlLocation(String currentLocation) {
        chooser.setWsdlPath(currentLocation);
        wsdl = currentLocation;
    }

    public String getWsdlLocation() {
        return wsdl;
    }

    private boolean isValidURL(String url) {
        try {
            URL u = new URL(url);
            u.toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}