package org.mule.tooling.devkit.ui;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
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
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.wizards.ProjectObserver;
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

    private static final Pattern CONNECTOR_NAME_REGEXP = Pattern.compile("[A-Z]+[a-zA-Z0-9]+");
    private static final Pattern VALID_NAME_REGEX = Pattern.compile("[A-Za-z]+[a-zA-Z0-9\\-_]*");

    private Text name;
    private Text projectName;
    private Text connectorNamespace;
    private Text location;

    private Button browse;
    private Button useDefaultValuesCheckbox;
    private Button generateEmptyProjectCheckbox;
    private boolean addEmptyProjectCheckbox = false;

    private ProjectObserver notifier;

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
                dialogChanged();
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
                dialogChanged();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                updateProjectComponentsEnablement();
                if (useDefaultValuesCheckbox.getSelection()) {
                    name.setText(name.getText());
                } else {
                    location.setText("");
                }
                dialogChanged();
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
                dialogChanged();
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
                    dialogChanged();
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    dialogChanged();
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
        if (useDefaultValuesCheckbox.getSelection())
            return Platform.getLocation().append(getProjectName()).toOSString();
        return Path.fromOSString(location.getText().trim()).append(this.getProjectName()).toOSString();
    }

    protected String getDefaultPath(String name) {
        final IPath path = Platform.getLocation().append(name);
        return path.toOSString();
    }

    private void dialogChanged() {
        updateProjectComponentsEnablement();
        notifier.broadcastChange();
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

    public IStatus validate() {
        if (StringUtils.isBlank(this.getName())) {
            return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "The Connector Name must be specified.");
        } else if (this.getName().equals("Test")) {
            return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "The Connector Name cannot be Test.");
        } else if (this.getName().endsWith("Connector")) {
            return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "There is no need for you to add the Connector word at the end.");
        } else if (DevkitUtils.isReserved(this.getName().toLowerCase())) {
            return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "Cannot use Java language keywords for the name");
        } else if (!CONNECTOR_NAME_REGEXP.matcher(this.getName()).matches()) {
            return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "The Name must start with an upper case character followed by other alphanumeric characters.");
        }
        if (!this.getName().equals(StringUtils.trim(this.getName()))) {
            return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "Name cannot contain spaces.");
        }
        if (!VALID_NAME_REGEX.matcher(this.getConnectorNamespace()).matches()) {
            return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "Namespace must start with a letter, and might be followed by a letter, number, _, or -.");
        }
        if (StringUtils.isBlank(this.location.getText())) {
            return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "You need to specify a project location");
        }

        final String projectName = getProjectName();

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // check whether project already exists
        final IProject handle = workspace.getRoot().getProject(this.getName());
        if (handle.exists()) {
            return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "A project with the name [" + projectName + "] already exists in your workspace folder.");
        }

        IPath projectLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(projectName);
        if (projectLocation.toFile().exists()) {
            try {
                // correct casing
                String canonicalPath = projectLocation.toFile().getCanonicalPath();
                projectLocation = new Path(canonicalPath);
            } catch (IOException e) {
                DevkitUIPlugin.log(e);
            }

            String existingName = projectLocation.lastSegment();
            if (!existingName.equals(projectName)) {
                return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "Invalid name");
            }

        }
        final String location = this.getLocation();
        if (!Path.EMPTY.isValidPath(location)) {
            return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "Invalid project contents directory");
        }

        IPath projectPath = null;
        if (!this.useDefaultValuesCheckbox.getSelection()) {
            projectPath = Path.fromOSString(location);
            if (!projectPath.toFile().exists()) {
                // check non-existing external location
                if (!canCreate(projectPath.toFile())) {
                    return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "Cannot create project content at the given external location.");
                }
            }
        }

        // validate the location
        final IStatus locationStatus = workspace.validateProjectLocation(handle, projectPath);
        if (!locationStatus.isOK()) {
            return locationStatus;
        }

        final IStatus nameStatus = ResourcesPlugin.getWorkspace().validateName(projectName, IResource.PROJECT);
        if (!nameStatus.isOK()) {
            return nameStatus;
        }

        if (root.exists(Path.fromOSString(projectName))) {
            return new Status(IStatus.ERROR, DevkitUIPlugin.PLUGIN_ID, "A project with the name [" + projectName + "] already exists in your workspace folder.");
        }
        return Status.OK_STATUS;
    }

    public String getConnectorNamespace() {
        return connectorNamespace.getText();
    }

    public boolean generateDefaultBody() {
        boolean result = false;
        if (generateEmptyProjectCheckbox != null) {
            result = generateEmptyProjectCheckbox.getSelection();
        }
        return result;
    }

    private boolean canCreate(File file) {
        while (!file.exists()) {
            file = file.getParentFile();
            if (file == null)
                return false;
        }
        return file.canWrite();
    }

    public ProjectObserver getNotifier() {
        return notifier;
    }

    public void setNotifier(ProjectObserver notifier) {
        this.notifier = notifier;
    }

    public boolean useDefaultValues() {
        return useDefaultValuesCheckbox.getSelection();
    }

    public void setFocus() {
        name.setFocus();
    }
}
