package org.mule.tooling.devkit.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
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
import org.mule.tooling.devkit.dialogs.PackageSelectionDialog;

public class ModuleNewWizardPage extends WizardPage {

    protected Text packageName;
    protected IPackageFragment packageFragment;
    protected Text name;
    protected IProject project;
    protected ISelection selection;
    protected String nameLabel;

    public ModuleNewWizardPage(ISelection selection) {
        super("wizardPage");
        setTitle("Mule Module Wizard");
        setDescription("This wizard creates a Mule Module template");
        this.selection = selection;
        this.nameLabel = "&Module Name:";
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        Label label = new Label(container, SWT.NULL);
        label.setText("&Package:");

        packageName = new Text(container, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        packageName.setLayoutData(gd);
        packageName.addModifyListener(new ModifyListener() {

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
        label.setText(nameLabel);

        name = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        name.setLayoutData(gd);
        name.addModifyListener(new ModifyListener() {

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
                IContainer container;
                if (obj instanceof IContainer)
                    container = (IContainer) obj;
                else
                    container = ((IResource) obj).getParent();

                project = container.getProject();
                try {
                    IPackageFragment findPackageFragment = JavaCore.create(project).findPackageFragment(container.getFullPath());
                    packageName.setText(findPackageFragment.getElementName());
                    packageFragment = findPackageFragment;

                } catch (JavaModelException e) {
                    packageName.setText("");
                }
            }
            if (obj instanceof IPackageFragment) {
                packageName.setText(((IPackageFragment) obj).getElementName());
                packageFragment = (IPackageFragment) obj;
                project = ((IPackageFragment) obj).getJavaProject().getProject();
            }
        }
        name.setText("");
    }

    /**
     * Uses the standard container selection dialog to choose the new value for the container field.
     */

    private void handleBrowse() {

        PackageSelectionDialog dialog = new PackageSelectionDialog(getShell(), new LabelProvider(), project);
        if (dialog.open() == ElementListSelectionDialog.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                packageName.setText(((IPackageFragment) result[0]).getElementName());
                packageFragment = (IPackageFragment) result[0];
            }
        }
    }

    private void dialogChanged() {

        String fileName = getName();

        if (getPackageName().length() == 0) {
            updateStatus("Package must be specified");
            return;
        }

        if (fileName.length() == 0) {
            updateStatus("Name must be specified");
            return;
        }
        if (!Character.isUpperCase(fileName.charAt(0))) {
            updateStatus("First letter of the name must be Upper Case");
            return;
        }

        updateStatus(null);
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getPackageName() {
        return packageName.getText();
    }

    public String getName() {
        return name.getText();
    }

    public IPackageFragment getPackageFragment() {
        return packageFragment;
    }

}