package org.mule.tooling.devkit.ui;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.ui.MuleUiConstants;
import org.mule.tooling.ui.utils.UiUtils;

public class ConnectorProjectWidget {

    private static final String GROUP_TITLE_CONNECTOR = "";
    private static final String DEFAULT_NAME = "";
    private static final String USE_DEFAULT_LABEL = "Use default values";
    private static final String PROJECT_NAME_LABEL = "Project Name:";
    private static final String CONNECTOR_NAMESPACE_LABEL = "Namespace:";
    private static final String GENERATE_EMPTY_PROJECT_LABEL = "Generate default body for @Connector";
    private static final String LOCATION_LABEL = "Location:";

    private Text name;
    private Text projectName;
    private Text connectorNamespace;
    private Text location;

    private Button browse;
    private Button useDefaultValuesCheckbox;
    private Button generateEmptyProjectCheckbox;
    private boolean addEmptyProjectCheckbox = false;

    public void createControl(Composite parent) {
        Group connectorGroupBox = UiUtils.createGroupWithTitle(parent, GROUP_TITLE_CONNECTOR, 3);
        ModifyListener simple = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }

        };

        ModifyListener connectorNameListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {

            }
        };

        name = initializeTextField(connectorGroupBox, "Connector Name: ", DEFAULT_NAME,
                "This is the name of the connector. There is no need for you to add a \"Connector\" at the end of the name.", 2, connectorNameListener);

        name.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!name.getText().isEmpty()) {
                    char character = name.getText().charAt(0);
                    if (!Character.isUpperCase(character) && !Character.isDigit(character)) {
                        name.setText(org.apache.commons.lang.StringUtils.capitalize(name.getText()));
                        name.setSelection(1, 1);
                        return;
                    }
                }
                dialogChanged();
            }
        });
        name.setFocus();

        useDefaultValuesCheckbox = new Button(connectorGroupBox, SWT.CHECK);
        useDefaultValuesCheckbox.setSelection(true);
        useDefaultValuesCheckbox.setText(" " + USE_DEFAULT_LABEL);
        useDefaultValuesCheckbox.setLayoutData(GridDataFactory.swtDefaults().span(3, 1).create());
        useDefaultValuesCheckbox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateProjectComponentsEnablement();
                if (useDefaultValuesCheckbox.getSelection()) {
                    name.setText(name.getText());
                } else {
                    location.setText("");
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                updateProjectComponentsEnablement();
                if (useDefaultValuesCheckbox.getSelection()) {
                    name.setText(name.getText());
                } else {
                    location.setText("");
                }
            }
        });

        projectName = initializeTextField(connectorGroupBox, PROJECT_NAME_LABEL, DEFAULT_NAME, "Project name.", 2, simple);

        name.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!name.getText().isEmpty() && useDefaultValuesCheckbox.getSelection()) {
                    projectName.setText(DevkitUtils.toConnectorName(name.getText()) + "-connector");
                    location.setText(getDefaultPath(projectName.getText()));
                    connectorNamespace.setText(DevkitUtils.toConnectorName(getName()));
                }
                dialogChanged();
            }
        });

        connectorNamespace = initializeTextField(connectorGroupBox, CONNECTOR_NAMESPACE_LABEL, DEFAULT_NAME,
                "Namespace that will be used when your connector is added into a mule application.", 2, simple);

        location = initializeTextField(connectorGroupBox, LOCATION_LABEL, ResourcesPlugin.getWorkspace().getRoot().getFullPath().toOSString(),
                "Project location in the file system.", 1, simple);

        browse = new Button(connectorGroupBox, SWT.NONE);
        browse.setText("Browse");
        browse.setLayoutData(GridDataFactory.fillDefaults().create());
        browse.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
                dialog.setText("Select project location");
                String path = location.getText();
                if (path.length() == 0) {
                    path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
                }
                dialog.setFilterPath(path);

                String result = dialog.open();
                if (result != null) {
                    location.setText(result);
                }
            }
        });

        if (addEmptyProjectCheckbox) {
            generateEmptyProjectCheckbox = new Button(connectorGroupBox, SWT.CHECK);
            generateEmptyProjectCheckbox.setSelection(true);
            generateEmptyProjectCheckbox.setText(" " + GENERATE_EMPTY_PROJECT_LABEL);
            generateEmptyProjectCheckbox.setLayoutData(GridDataFactory.swtDefaults().span(3, 1).create());
            generateEmptyProjectCheckbox
                    .setToolTipText("This will generate an @Connector with configurables, operations and tests.\nRecommended for users who haven't build connectors before.");
            generateEmptyProjectCheckbox.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {

                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {

                }
            });
        }
        initialize();
        dialogChanged();
    }

    public String getName() {
        return name.getText();
    }

    public String getProjectName() {
        return projectName.getText();
    }

    public String getNameSpace() {
        return connectorNamespace.getText();
    }

    public String getLocation() {
        return location.getText();
    }

    protected String getDefaultPath(String name) {
        final IPath path = Platform.getLocation().append(name);
        return path.toOSString();
    }

    private void dialogChanged() {
        updateProjectComponentsEnablement();
    }

    private Text initializeTextField(Group groupBox, String labelText, String defaultValue, String tooltip, int hSpan, ModifyListener modifyListener) {
        Label label = new Label(groupBox, SWT.NULL);
        label.setText(labelText);
        label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(MuleUiConstants.LABEL_WIDTH, SWT.DEFAULT).create());
        Text textField = new Text(groupBox, SWT.BORDER);
        GridData gData = new GridData(GridData.FILL_HORIZONTAL);
        gData.horizontalSpan = hSpan;
        textField.setLayoutData(gData);
        textField.setText(defaultValue);
        if (modifyListener != null)
            textField.addModifyListener(modifyListener);
        textField.setToolTipText(tooltip);
        return textField;
    }

    private void initialize() {
        location.setText(getDefaultPath(""));
        name.setText(DEFAULT_NAME);
        updateProjectComponentsEnablement();
    }

    private void updateProjectComponentsEnablement() {
        boolean enabled = !useDefaultValuesCheckbox.getSelection();
        projectName.setEnabled(enabled);
        connectorNamespace.setEnabled(enabled);
        location.setEnabled(enabled);
        browse.setEnabled(enabled);
    }

    public boolean isAddEmptyProjectCheckbox() {
        return addEmptyProjectCheckbox;
    }

    public void setAddEmptyProjectCheckbox(boolean addEmptyProjectCheckbox) {
        this.addEmptyProjectCheckbox = addEmptyProjectCheckbox;
    }
}
