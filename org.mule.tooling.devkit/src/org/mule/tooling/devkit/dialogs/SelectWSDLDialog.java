package org.mule.tooling.devkit.dialogs;

import java.io.File;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.ui.MuleUiConstants;

public class SelectWSDLDialog extends TitleAreaDialog {

    private static final String TITTLE = "Select WSDL Location";
    private static final String SUBTITLE = "Specify a file, folder or URL where the wsdl is located.";
    private Text wsdlLocation;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SelectWSDLDialog(Shell parentShell, String path) {
        super(parentShell);
        this.path = path;
    }

    @Override
    public void create() {
        super.create();
        setTitle(TITTLE);
        setMessage(SUBTITLE);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        GridData gdata = new GridData();
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;

        container.setLayoutData(gdata);

        final Label wsdlLabel = new Label(container, SWT.NULL);
        wsdlLabel.setText("Location:");
        wsdlLabel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).span(2, 1).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());

        wsdlLocation = new Text(container, SWT.BORDER);
        wsdlLocation.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).create());
        wsdlLocation.setText(path);
        wsdlLocation.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                isValid();
            }

        });
        Composite pickButtons = new Composite(container, SWT.NULL);
        final Button buttonPickFile = new Button(pickButtons, SWT.NONE);
        buttonPickFile.setText("File");
        buttonPickFile.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.END).create());
        buttonPickFile.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                dialog.setText("Select WSDL file");
                dialog.setFilterExtensions(new String[] { "*.wsdl", "*.*" });
                String path = wsdlLocation.getText();
                if (path.length() == 0) {
                    path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
                }
                dialog.setFilterPath(path);

                String result = dialog.open();
                if (result != null) {
                    wsdlLocation.setText(result);
                }
            }
        });

        final Button buttonPickFolder = new Button(pickButtons, SWT.NONE);
        buttonPickFolder.setText("Folder");
        buttonPickFolder.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.END).create());
        buttonPickFolder.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
                dialog.setText("Select Directory containing one WSDL");
                String path = wsdlLocation.getText();
                if (path.length() == 0) {
                    path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
                }
                dialog.setFilterPath(path);

                String result = dialog.open();
                if (result != null) {
                    wsdlLocation.setText(result);
                }
            }

        });

        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(pickButtons);
        GridLayoutFactory.fillDefaults().applyTo(container);
        GridLayoutFactory.swtDefaults().applyTo(container);
        return container;
    }

    @Override
    protected void okPressed() {
        if (isValid()) {
            super.okPressed();
        }
    }

    private boolean isValid() {
        boolean isValid = false;
        if (wsdlLocation.getText().isEmpty()) {
            path = wsdlLocation.getText();
            this.setMessage(SUBTITLE);
            isValid = true;
        } else {
            if (wsdlLocation.getText().startsWith("http")) {

                UrlValidator validator = new UrlValidator();
                isValid = validator.isValid(wsdlLocation.getText());
                if (!isValid) {
                    this.setMessage("The url provided is not valid.", IMessageProvider.ERROR);
                } else {
                    path = wsdlLocation.getText();
                    this.setMessage(SUBTITLE);
                    isValid = true;
                }
            } else {
                File file = new File(wsdlLocation.getText());
                if (file.exists()) {
                    if (file.isDirectory()) {
                        // Check that the folder has a wsdl.
                        String wsdlFileName = "";
                        String[] files = file.list(new SuffixFileFilter(".wsdl"));
                        for (int i = 0; i < files.length; i++) {
                            File temp = new File(files[i]);
                            wsdlFileName = temp.getName();
                        }
                        if (wsdlFileName.isEmpty()) {
                            this.setMessage("The selected directory does not contains a '.wsdl' file.", IMessageProvider.ERROR);
                        } else {
                            path = wsdlLocation.getText();
                            this.setMessage(wsdlFileName + " will be used to generate the scaffolding.");
                            isValid = true;
                        }
                    } else {
                        path = wsdlLocation.getText();
                        this.setMessage(file.getName() + " will be used to generate the scaffolding.");
                        isValid = true;
                    }
                } else {
                    setMessage("The selected value could not be used to get a wsdl file.", IMessageProvider.ERROR);
                }
            }
        }
        return isValid;
    }

}
