package org.mule.tooling.devkit.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.builder.DevkitBuilder;
import org.mule.tooling.devkit.builder.DevkitNature;
import org.mule.tooling.devkit.builder.ProjectBuilder;
import org.mule.tooling.devkit.builder.ProjectBuilderFactory;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.maven.MavenInfo;
import org.mule.tooling.devkit.maven.ScanProject;
import org.mule.tooling.devkit.maven.UpdateProjectClasspathWorkspaceJob;
import org.mule.tooling.maven.runner.SyncGetResultCallback;
import org.mule.tooling.maven.ui.MavenUIPlugin;
import org.mule.tooling.maven.ui.actions.MavenInstallationTester;
import org.mule.tooling.maven.ui.preferences.MavenPreferences;

/**
 * Page for importing a Mule project from a Folder.
 */
public class ConnectorImportWizzardPage extends WizardPage {

    /** the history limit */
    protected static final int MAX_HISTORY = 10;

    /** Unique page id */
    public static final String PAGE_ID = "connectorProjectImportPage";

    /** Projects dropdown */
    protected ComboViewer projects;

    protected Text name;

    private Map<String, List<Combo>> fieldsWithHistory;

    protected Combo rootDirectoryCombo;

    protected CheckboxTreeViewer projectTreeViewer;

    private MavenInfo root;

    /** dialog settings to store input history */
    protected IDialogSettings dialogSettings;

    private boolean mavenFailure = false;

    public ConnectorImportWizzardPage() {
        super(PAGE_ID);
        setTitle("Import Anypoint Connector Project");
        setDescription("Import an existing Anypoint Connector");

        fieldsWithHistory = new HashMap<String, List<Combo>>();
        root = new MavenInfo();
        initDialogSettings();
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);

        GridLayout layout = new GridLayout(3, false);
        composite.setLayout(layout);

        addRootComboSection(composite);

        final Label projectsLabel = new Label(composite, SWT.NONE);
        projectsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        projectsLabel.setText("Projects");

        projectTreeViewer = new CheckboxTreeViewer(composite, SWT.BORDER);
        projectTreeViewer.addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                projectTreeViewer.setSubtreeChecked(event.getElement(), event.getChecked());
                for (MavenInfo item : ((MavenInfo) event.getElement()).getModules()) {
                    projectTreeViewer.setChecked(item, event.getChecked());
                }
                updatePageComplete();
            }
        });

        projectTreeViewer.setContentProvider(new ITreeContentProvider() {

            public Object[] getElements(Object element) {

                return root.getModules().toArray();
            }

            public Object[] getChildren(Object parentElement) {
                MavenInfo current = (MavenInfo) parentElement;
                return current.getModules().toArray();
            }

            public Object getParent(Object element) {
                return null;
            }

            public boolean hasChildren(Object parentElement) {
                MavenInfo current = (MavenInfo) parentElement;
                return current.hasChilds();
            }

            public void dispose() {
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
        });
        projectTreeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new ProjectLabelProvider()));
        projectTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (selection.getFirstElement() != null) {
                    if (existsInWorkspace((MavenInfo) selection.getFirstElement())) {
                        projectTreeViewer.setChecked(selection.getFirstElement(), false);
                        setMessage("Project " + ((MavenInfo) selection.getFirstElement()).getArtifactId() + " is already in the workspace", IMessageProvider.WARNING);
                    } else {
                        setMessage(null, IMessageProvider.WARNING);
                    }
                    updatePageComplete();
                } else {
                    setMessage(null, IMessageProvider.WARNING);
                }
            }
        });
        final Tree projectTree = projectTreeViewer.getTree();
        GridData projectTreeData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3);
        projectTreeData.heightHint = 250;
        projectTreeData.widthHint = 500;
        projectTree.setLayoutData(projectTreeData);

        final Button selectAllButton = new Button(composite, SWT.NONE);
        selectAllButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        selectAllButton.setText("Select All");
        selectAllButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                projectTreeViewer.expandAll();
                selectecAll();
                updatePageComplete();
            }
        });

        final Button deselectAllButton = new Button(composite, SWT.NONE);
        deselectAllButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        deselectAllButton.setText("Deselect all");
        deselectAllButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                setMessage(null, IMessageProvider.WARNING);
                setAllChecked(false);
                projectTreeViewer.setSubtreeChecked(projectTreeViewer.getInput(), false);
                MavenInfo elements = (MavenInfo) projectTreeViewer.getInput();
                for (MavenInfo item : elements.getModules()) {
                    projectTreeViewer.setChecked(item, false);
                }
                updatePageComplete();
            }
        });

        setControl(composite);
        projectTreeViewer.setInput(root);
        projectTreeViewer.refresh();
        projectTreeViewer.expandAll();
        testMaven();
        setPageComplete(false);
    }

    private void addRootComboSection(Composite composite) {
        final Label projectRootLabel = new Label(composite, SWT.NULL);
        projectRootLabel.setText("Select root directory:");
        rootDirectoryCombo = new Combo(composite, SWT.NONE);
        rootDirectoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        rootDirectoryCombo.setFocus();
        addFieldWithHistory("rootDirectory", rootDirectoryCombo);
        loadInputHistory();

        ComboListener listener = new ComboListener();
        rootDirectoryCombo.addModifyListener(listener);
        rootDirectoryCombo.addFocusListener(listener);
        rootDirectoryCombo.addSelectionListener(listener);

        final Button browseButton2 = new Button(composite, SWT.NONE);
        browseButton2.setText("Browse");
        browseButton2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        browseButton2.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.NONE);
                dialog.setText("Import Folder");
                String path = rootDirectoryCombo.getText();
                if (path.length() == 0) {
                    path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
                }
                dialog.setFilterPath(path);

                String result = dialog.open();
                if (result != null) {
                    rootDirectoryCombo.setText(result);
                    scanProject();
                    updatePageComplete();
                }

            }
        });
    }

    /**
     * Update the indicator for whether the page is complete.
     */
    protected void updatePageComplete() {
        if (mavenFailure) {
            setErrorMessage("Maven home is not properly configured. Check your maven preferences.");
            setPageComplete(false);
            return;
        }

        Object[] checkedElements = projectTreeViewer.getCheckedElements();
        boolean complete = checkedElements != null && checkedElements.length > 0 && StringUtils.isEmpty(getErrorMessage());
        if (complete) {
            Object[] elements = projectTreeViewer.getCheckedElements();
            for (int i = 0; i < elements.length; i++) {
                Object element = elements[i];
                if (element instanceof MavenInfo) {
                    final MavenInfo mavenProject = (MavenInfo) element;
                    if (existsInWorkspace(mavenProject)) {
                        projectTreeViewer.setChecked(element, false);
                        setMessage("Project " + mavenProject.getArtifactId() + " is already in the workspace", IMessageProvider.WARNING);
                        complete = false;
                    }
                }
            }

            if (complete)
                setMessage(null, IMessageProvider.WARNING);
        }

        setPageComplete(complete);
    }

    /**
     * Gets a safe project name based on a given name.
     * 
     * @param input
     * @return
     */
    protected String getSafeName(String input) {
        return input.toLowerCase().replace(' ', '_');
    }

    /**
     * Perform the import operation.
     * 
     * @param window
     * 
     * @return
     */
    public boolean performFinish() {
        final Object[] items = getSelectedItems();
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                monitor.beginTask("Importing modules", items.length);
                for (int index = 0; index < items.length; index++) {
                    final MavenInfo mavenProject = (MavenInfo) items[index];

                    final File folder = mavenProject.getProjectRoot();

                    try {
                        if (folder != null && folder.exists()) {
                            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                            try {
                                IProject project = createProject(folder.getName(), monitor, root, folder);

                                IJavaProject javaProject = JavaCore.create(root.getProject(folder.getName()));
                                ProjectBuilder generator = ProjectBuilderFactory.newInstance();

                                List<IClasspathEntry> classpathEntries = generator.generateProjectEntries(project, monitor);
                                javaProject.setRawClasspath(classpathEntries.toArray(new IClasspathEntry[] {}), monitor);
                                if (mavenProject.getPackaging() != null && mavenProject.getPackaging().equals("mule-module")) {
                                    DevkitUtils.configureDevkitAPT(javaProject);
                                }

                                UpdateProjectClasspathWorkspaceJob job = new UpdateProjectClasspathWorkspaceJob(javaProject, new String[] { "clean", "compile", "eclipse:eclipse" });
                                job.runInWorkspace(monitor);
                                project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                            } catch (CoreException e) {
                                e.printStackTrace();
                            }
                        }
                    } finally {
                        monitor.worked(1);
                    }
                }
                monitor.done();
            }
        };
        return runInContainer(op);
    }

    private IProject createProject(String artifactId, IProgressMonitor monitor, IWorkspaceRoot root, File folder) throws CoreException {

        IProjectDescription projectDescription = getProjectDescription(root, artifactId, folder);

        return getProjectWithDescription(artifactId, monitor, root, projectDescription);
    }

    protected IProject getProjectWithDescription(String artifactId, IProgressMonitor monitor, IWorkspaceRoot root, IProjectDescription projectDescription) throws CoreException {
        IProject project = root.getProject(artifactId);
        if (!project.exists()) {
            project.create(projectDescription, monitor);
            project.open(monitor);
            project.setDescription(projectDescription, monitor);
        }
        return project;
    }

    private IProjectDescription getProjectDescription(IWorkspaceRoot root, String artifactId, File folder) throws CoreException {

        File projectDescriptionFile = new File(folder, ".project");
        IProjectDescription currentDescription = null;
        ICommand[] commands = null;
        int commandsLength = 0;
        if (projectDescriptionFile.exists()) {
            currentDescription = root.getWorkspace().loadProjectDescription(Path.fromOSString(new File(folder, ".project").getAbsolutePath()));
            commands = currentDescription.getBuildSpec();
            commandsLength = commands.length;
            for (int i = 0; i < commands.length; ++i) {
                if (commands[i].getBuilderName().equals(DevkitBuilder.BUILDER_ID)) {
                    return currentDescription;
                }
            }
        }
        ICommand[] newCommands = new ICommand[commandsLength + 1];
        if (commands != null) {
            System.arraycopy(commands, 0, newCommands, 0, commandsLength);
        }
        IProjectDescription projectDescription = root.getWorkspace().newProjectDescription(artifactId);
        projectDescription.setNatureIds(new String[] { JavaCore.NATURE_ID, DevkitNature.NATURE_ID });

        ICommand command = projectDescription.newCommand();
        command.setBuilderName(DevkitBuilder.BUILDER_ID);
        newCommands[newCommands.length - 1] = command;

        projectDescription.setBuildSpec(newCommands);

        projectDescription.setLocation(Path.fromOSString(folder.getAbsolutePath()));

        return projectDescription;
    }

    protected void testMaven() {
        mavenFailure = false;
        MavenPreferences preferencesAccessor = MavenUIPlugin.getDefault().getPreferences();
        final MavenInstallationTester mavenInstallationTester = new MavenInstallationTester(preferencesAccessor.getMavenInstallationHome());
        mavenInstallationTester.test(new SyncGetResultCallback() {

            @Override
            public void finished(final int result) {
                super.finished(result);
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        onTestFinished(result);
                    }
                });
            }
        });

    }

    void onTestFinished(final int result) {
        mavenFailure = result != 0;
        updatePageComplete();
    }

    private boolean runInContainer(final IRunnableWithProgress work) {
        try {
            getContainer().run(true, true, work);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }

    /** Loads the dialog settings using the page name as a section name. */
    private void initDialogSettings() {
        IDialogSettings pluginSettings;

        // This is strictly to get SWT Designer working locally without blowing up.
        if (DevkitUIPlugin.getDefault() == null) {
            pluginSettings = new DialogSettings("Workbench");
        } else {
            pluginSettings = DevkitUIPlugin.getDefault().getDialogSettings();
        }

        dialogSettings = pluginSettings.getSection(getName());
        if (dialogSettings == null) {
            dialogSettings = pluginSettings.addNewSection(getName());
            pluginSettings.addSection(dialogSettings);
        }
    }

    /** Loads the input history from the dialog settings. */
    private void loadInputHistory() {
        for (Map.Entry<String, List<Combo>> e : fieldsWithHistory.entrySet()) {
            String id = e.getKey();
            String[] items = dialogSettings.getArray(id);
            if (items != null) {
                for (Combo combo : e.getValue()) {
                    String text = combo.getText();
                    combo.setItems(items);
                    if (text.length() > 0) {
                        // setItems() clears the text input, so we need to restore it
                        combo.setText(text);
                    }
                }
            }
        }
    }

    /** Saves the input history into the dialog settings. */
    private void saveInputHistory() {
        for (Map.Entry<String, List<Combo>> e : fieldsWithHistory.entrySet()) {
            String id = e.getKey();

            Set<String> history = new LinkedHashSet<String>(MAX_HISTORY);

            for (Combo combo : e.getValue()) {
                String lastValue = combo.getText();
                if (lastValue != null && lastValue.trim().length() > 0) {
                    history.add(lastValue);
                }
            }

            Combo combo = e.getValue().iterator().next();
            String[] items = combo.getItems();
            for (int j = 0; j < items.length && history.size() < MAX_HISTORY; j++) {
                history.add(items[j]);
            }

            dialogSettings.put(id, history.toArray(new String[history.size()]));
        }
    }

    /** Adds an input control to the list of fields to save. */
    protected void addFieldWithHistory(String id, Combo combo) {
        if (combo != null) {
            List<Combo> combos = fieldsWithHistory.get(id);
            if (combos == null) {
                combos = new ArrayList<Combo>();
                fieldsWithHistory.put(id, combos);
            }
            combos.add(combo);
        }
    }

    /** Saves the history when the page is disposed. */
    public void dispose() {
        saveInputHistory();
        super.dispose();
    }

    private boolean existsInWorkspace(MavenInfo mavenProject) {
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        final File folder = mavenProject.getProjectRoot();
        for (int index = 0; index < projects.length; index++) {
            if (projects[index].getLocation().toFile().equals(folder)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ProjectLabelProvider
     */
    class ProjectLabelProvider extends LabelProvider implements IColorProvider, DelegatingStyledCellLabelProvider.IStyledLabelProvider {

        public String getText(Object element) {

            if (element instanceof MavenInfo) {
                MavenInfo info = (MavenInfo) element;
                return info.toString();
            }
            return super.getText(element);
        }

        public org.eclipse.swt.graphics.Color getForeground(Object element) {
            if (element instanceof MavenInfo)
                if (existsInWorkspace((MavenInfo) element)) {
                    return Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
                }
            return null;
        }

        public org.eclipse.swt.graphics.Color getBackground(Object element) {
            return null;
        }

        public StyledString getStyledText(Object element) {
            if (element instanceof MavenInfo) {
                MavenInfo info = (MavenInfo) element;
                StyledString ss = new StyledString();
                ss.append(info.getArtifactId() + "/pom.xml ");
                ss.append(info.getGroupId() + ":" + info.getArtifactId(), StyledString.DECORATIONS_STYLER);
                if (!info.getVersion().isEmpty()) {
                    ss.append(" - " + info.getVersion(), StyledString.QUALIFIER_STYLER);
                }
                return ss;
            }
            return null;
        }

    }

    private void scanProject() {
        projectTreeViewer.setInput(null);
        root.setModules(new ArrayList<MavenInfo>());
        root.setProjectRoot(new File(rootDirectoryCombo.getText()));
        ScanProject job = new ScanProject("Scaningn project", rootDirectoryCombo.getText(), root);
        try {
            job.runInWorkspace(null);
            if (root.getModules().size() == 1) {
                if (root.getModules().get(0).hasChilds()) {
                    setErrorMessage("Multi Module projects are not supported.");
                    setPageComplete(false);
                } else {
                    setErrorMessage(null);
                    setPageComplete(true);
                }
            } else {
                setErrorMessage(null);
            }
            projectTreeViewer.setInput(root);
            projectTreeViewer.refresh();
            projectTreeViewer.expandAll();
            selectecAll();
        } catch (CoreException e1) {
            e1.printStackTrace();
        }
    }

    void setAllChecked(boolean state) {
        MavenInfo input = (MavenInfo) projectTreeViewer.getInput();
        for (MavenInfo mavenProjectInfo : input.getModules()) {
            projectTreeViewer.setSubtreeChecked(mavenProjectInfo, state);
        }
    }

    private void selectecAll() {
        setAllChecked(true);
        projectTreeViewer.setSubtreeChecked(projectTreeViewer.getInput(), true);
        MavenInfo elements = (MavenInfo) projectTreeViewer.getInput();
        for (MavenInfo item : elements.getModules()) {
            projectTreeViewer.setChecked(item, true);
        }
    }

    private class ComboListener implements ModifyListener, FocusListener, SelectionListener {

        private void scanAndUpdate() {
            scanProject();
            updatePageComplete();
        }

        @Override
        public void modifyText(ModifyEvent e) {
            scanAndUpdate();
        }

        @Override
        public void focusGained(FocusEvent e) {
            scanAndUpdate();
        }

        @Override
        public void focusLost(FocusEvent e) {

        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            // in runnable to have the combo popup collapse before disabling controls.
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    scanAndUpdate();
                }
            });
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            scanAndUpdate();
        }
    }

    public Object[] getSelectedItems() {
        return projectTreeViewer.getCheckedElements();
    }
}