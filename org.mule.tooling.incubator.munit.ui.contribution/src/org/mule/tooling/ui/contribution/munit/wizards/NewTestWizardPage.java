package org.mule.tooling.ui.contribution.munit.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.mule.tooling.core.builder.MuleNature;
import org.mule.tooling.core.impl.model.MuleProjectImpl;
import org.mule.tooling.core.model.IMuleProject;

public class NewTestWizardPage extends WizardPage {

    private Text productionFileText;

    private Text fileText;

    private ISelection selection;

    public NewTestWizardPage(ISelection selection) {
        super("wizardPage");
        setTitle("Munit Create test");
        setDescription("This wizard creates a new munit test associated with a production file");
        this.selection = selection;
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        Label label = new Label(container, SWT.NULL);
        label.setText("&Flow file to be tested:");

        productionFileText = new Text(container, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        productionFileText.setLayoutData(gd);
        productionFileText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        Button button = new Button(container, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                handleBrowse();
            }
        });

        label = new Label(container, SWT.NULL);
        label.setText("&Test name:");

        fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fileText.setLayoutData(gd);
        fileText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        initialize();
        dialogChanged();
        setControl(container);
    }

    private void initialize() {
        if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            if (ssel.size() > 1)
                return;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IResource) {
                IContainer project;
                if (obj instanceof IContainer)
                    project = (IContainer) obj;
                else
                    project = ((IResource) obj).getParent();
                if(project.isAccessible())
                    productionFileText.setText(project.getFullPath().toString());
            }
        }
        fileText.setText("new_munit_test.xml");
    }

    private void handleBrowse() {
        try {
            List<IResource> resources = new ArrayList<IResource>();
            IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
            for (IProject project : projects) {
                if (project.isOpen() && project.hasNature(MuleNature.NATURE_ID)) {
                    IMuleProject muleProject = new MuleProjectImpl();
                    muleProject.initialize(JavaCore.create(project));
                    IResource[] appsFiles = muleProject.getMuleAppsFolder().members(false);
                    for (IResource appFile : appsFiles) {
                        if ("xml".equals(appFile.getFileExtension())) {
                            resources.add(appFile);
                        }

                    }

                }
            }

            ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new LabelProvider());
            dialog.setElements(resources.toArray());

            if (dialog.open() == ElementListSelectionDialog.OK) {
                Object[] result = dialog.getResult();
                if (result.length == 1) {
                    productionFileText.setText((((IResource) result[0]).getFullPath()).toString());
                }
            }
        } catch (CoreException e) {
            
        }
    }

    private void dialogChanged() {

        String fileName = getFileName();
        String containerName = getContainerName();
        if (containerName != null && containerName.length() == 0) {
            updateStatus("The file to be tested must be specified");
            return;
        }
        IResource container = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(containerName));
        if (container == null || (container.getType() & IResource.FILE) == 0) {
            updateStatus("The file to be tested must exist");
            return;
        }
        if (!container.isAccessible()) {
            updateStatus("The file to be tested must be writable");
            return;
        }
        if (fileName.length() == 0) {
            updateStatus("Test name must be specified");
            return;
        }
        if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
            updateStatus("Test name must be valid");
            return;
        }
        int dotLoc = fileName.lastIndexOf('.');
        if (dotLoc != -1) {
            String ext = fileName.substring(dotLoc + 1);
            if (ext.equalsIgnoreCase("xml") == false) {
                updateStatus("Test extension must be \"xml\"");
                return;
            }
        }
        updateStatus(null);
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getContainerName() {
        return productionFileText.getText();
    }

    public String getFileName() {
        return fileText.getText();
    }
}