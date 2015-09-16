package org.mule.tooling.devkit.ui;

import java.io.File;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.ui.utils.UiUtils;

public class WsdlChooser {

    public static int ALL = 0;
    public static int FILE_OR_URL = 1 << 0;

    private Text wsdlLocation;

    private int mode;

    public WsdlChooser(int mode) {
        this.mode = mode;
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayoutFactory.swtDefaults().numColumns(4).applyTo(container);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, false).span(4, 1).applyTo(container);
        Group apiGroupBox = UiUtils.createGroupWithTitle(container, "WSDL Location", 3);

        Composite compositeRadio = new Composite(apiGroupBox, SWT.NULL);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(compositeRadio);
        GridDataFactory.fillDefaults().span(2, 1).align(GridData.FILL, SWT.CENTER).applyTo(compositeRadio);

        final Button fromFileRadioButton = new Button(compositeRadio, SWT.RADIO);
        fromFileRadioButton.setText("From WSDL file or URL");
        fromFileRadioButton.setSelection(true);
        fromFileRadioButton.setToolTipText("It will import the selected root WSDL from a file or URL");
        fromFileRadioButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
        fromFileRadioButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                wsdlLocation.setText("http://");
                wsdlLocation.setMessage("");
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                wsdlLocation.setText("http://");
                wsdlLocation.setMessage("");
            }
        });

        if (mode == ALL) {
            final Button fromFolderRadioButton = new Button(compositeRadio, SWT.RADIO);
            fromFolderRadioButton.setText("From folder");
            fromFolderRadioButton.setToolTipText("It will import all the root WSDL files and their dependencies");
            fromFolderRadioButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
            fromFolderRadioButton.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    wsdlLocation.setText("");
                    wsdlLocation.setMessage("Select a folder containing WSDL files");
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    wsdlLocation.setText("");
                    wsdlLocation.setMessage("Select a folder containing WSDL files");
                }
            });

        }

        wsdlLocation = new Text(apiGroupBox, SWT.BORDER);
        GridData gData = new GridData(GridData.FILL_HORIZONTAL);
        gData.horizontalSpan = 2;
        wsdlLocation.setLayoutData(gData);
        wsdlLocation.setText("http://");

        final Button buttonPickFile = new Button(apiGroupBox, SWT.NONE);
        buttonPickFile.setText("...");
        buttonPickFile.setLayoutData(GridDataFactory.fillDefaults().create());
        buttonPickFile.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {

                if (fromFileRadioButton.getSelection()) {
                    FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
                    dialog.setText("Select a WSDL file");
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

                } else {
                    DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
                    dialog.setText("Select a Directory containing the WSDL(s)");
                    String path = wsdlLocation.getText();
                    if (path.length() == 0) {
                        path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
                    }
                    dialog.setFilterPath(path);

                    String result = dialog.open();
                    if (result != null) {

                        // Check that the folder has a wsdl.
                        String wsdlFileName = "";
                        String[] files = new File(result).list(new SuffixFileFilter(".wsdl"));
                        File wsdlFile = null;
                        for (int i = 0; i < files.length; i++) {
                            wsdlFile = new File(result, files[i]);
                            wsdlFileName = wsdlFile.getName();
                        }

                        if (wsdlFileName.isEmpty()) {
                        } else {
                            wsdlLocation.setText(result);
                        }
                    }
                }

            }
        });

    }

    public void setWsdlPath(String currentLocation) {
        wsdlLocation.setText(currentLocation);
    }

    public String getWsdlPath() {
        return this.wsdlLocation.getText();
    }
}
