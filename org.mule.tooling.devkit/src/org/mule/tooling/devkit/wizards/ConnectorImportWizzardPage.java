package org.mule.tooling.devkit.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.mule.tooling.devkit.maven.MavenInfo;
import org.mule.tooling.devkit.maven.ScanProject;

public class ConnectorImportWizzardPage extends WizardPage {

    /** the history limit */
    protected static final int MAX_HISTORY = 10;

    /** Unique page id */
    public static final String PAGE_ID = "muleProjectImportPage";

    /** Projects dropdown */
    protected ComboViewer projects;

    private Composite composite;

    protected Text name;

    private Map<String, List<Combo>> fieldsWithHistory;

    protected Combo rootDirectoryCombo;

    protected CheckboxTreeViewer projectTreeViewer;

    private MavenInfo root;

    /** dialog settings to store input history */
    protected IDialogSettings dialogSettings;

    public ConnectorImportWizzardPage(IJavaProject selected) {
        super(PAGE_ID);
        setTitle("Import AnyPoint Connector Project");
        setDescription("Import an existing AnyPoint Connector");

        fieldsWithHistory = new HashMap<String, List<Combo>>();
        root = new MavenInfo();
        initDialogSettings();
    }

    public void createControl(Composite parent) {
        composite = new Composite(parent, SWT.NULL);

        GridLayout layout = new GridLayout(3, false);
        composite.setLayout(layout);

        rootDirectoryCombo = new Combo(composite, SWT.NONE);
        rootDirectoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        rootDirectoryCombo.setFocus();
        addFieldWithHistory("rootDirectory", rootDirectoryCombo);
        loadInputHistory();

        rootDirectoryCombo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setMessage(null);
                scanProject();
            }
        });
        rootDirectoryCombo.addFocusListener(new FocusAdapter() {

            public void focusLost(FocusEvent e) {
                scanProject();
            }
        });
        rootDirectoryCombo.addSelectionListener(new SelectionAdapter() {

            public void widgetDefaultSelected(SelectionEvent e) {
                setMessage(null);
                scanProject();
            }

            public void widgetSelected(SelectionEvent e) {
                // in runnable to have the combo popup collapse before disabling controls.
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        scanProject();
                    }
                });
            }
        });
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
                }
            }
        });

        final Label projectsLabel = new Label(composite, SWT.NONE);
        projectsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        projectsLabel.setText("Projects");

        projectTreeViewer = new CheckboxTreeViewer(composite, SWT.BORDER);
        projectTreeViewer.addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                projectTreeViewer.setSubtreeChecked(event.getElement(), event.getChecked());
                for (MavenInfo item : ((MavenInfo) event.getElement()).getModules()) {
                    projectTreeViewer.setChecked(item, event.getChecked());
                    if (existsInWorkspace(item) && event.getChecked()) {
                        validate();
                    }
                }
                if (existsInWorkspace((MavenInfo) event.getElement()) && event.getChecked()) {
                    validate();
                }
                setPageComplete();
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
                        setMessage(null);
                    }
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
                setAllChecked(true);
                projectTreeViewer.setSubtreeChecked(projectTreeViewer.getInput(), true);
                MavenInfo elements = (MavenInfo) projectTreeViewer.getInput();
                for (MavenInfo item : elements.getModules()) {
                    projectTreeViewer.setChecked(item, true);

                }
                validate();
            }
        });

        final Button deselectAllButton = new Button(composite, SWT.NONE);
        deselectAllButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        deselectAllButton.setText("Deselect all");
        deselectAllButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                setAllChecked(false);
                projectTreeViewer.setSubtreeChecked(projectTreeViewer.getInput(), false);
                MavenInfo elements = (MavenInfo) projectTreeViewer.getInput();
                for (MavenInfo item : elements.getModules()) {
                    projectTreeViewer.setChecked(item, false);

                }
                setPageComplete(false);
                setMessage(null);
            }
        });

        setControl(composite);
        projectTreeViewer.setInput(root);
        projectTreeViewer.refresh();
        projectTreeViewer.expandAll();
        setPageComplete();
    }

    public Composite getControl() {
        return this.composite;
    }

    /**
     * Execute logic for exporting Mule project as an archive.
     */
    public boolean performFinish() {
        return true;
    }

    public Object[] getSelectedItems() {
        return projectTreeViewer.getCheckedElements();
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

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
         */
        public org.eclipse.swt.graphics.Color getForeground(Object element) {
            if (element instanceof MavenInfo)
                if (existsInWorkspace((MavenInfo) element)) {
                    return Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
                }
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
         */
        public org.eclipse.swt.graphics.Color getBackground(Object element) {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider#getStyledText(java.lang.Object)
         */
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

    void setAllChecked(boolean state) {
        MavenInfo input = (MavenInfo) projectTreeViewer.getInput();
        for (MavenInfo mavenProjectInfo : input.getModules()) {
            projectTreeViewer.setSubtreeChecked(mavenProjectInfo, state);
        }

    }

    private void scanProject() {
        projectTreeViewer.setInput(null);
        root.setModules(new ArrayList<MavenInfo>());
        root.setProjectRoot(new File(rootDirectoryCombo.getText()));
        ScanProject job = new ScanProject("Scaningn project", rootDirectoryCombo.getText(), root);
        try {
            job.runInWorkspace(null);
            projectTreeViewer.setInput(root);
            projectTreeViewer.refresh();
            projectTreeViewer.expandAll();
        } catch (CoreException e1) {
            e1.printStackTrace();
        }
    }

    void setPageComplete() {
        Object[] checkedElements = projectTreeViewer.getCheckedElements();
        setPageComplete(checkedElements != null && checkedElements.length > 0);
        if (checkedElements != null && checkedElements.length > 0) {
            setMessage(null);
        }
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

    private void validate() {

        Object[] elements = projectTreeViewer.getCheckedElements();
        for (int i = 0; i < elements.length; i++) {
            Object element = elements[i];
            if (element instanceof MavenInfo) {
                final MavenInfo mavenProject = (MavenInfo) element;

                if (existsInWorkspace(mavenProject)) {
                    projectTreeViewer.setChecked(element, false);
                    setMessage("Project " + mavenProject.getArtifactId() + " is already in the workspace", IMessageProvider.WARNING);
                }

            }
        }
        setPageComplete();
    }
}
