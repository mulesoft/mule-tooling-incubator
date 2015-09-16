package org.mule.tooling.devkit.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.mule.tooling.devkit.DevkitImages;

public class NewDevkitComponentWizard extends Wizard implements INewWizard {

    NewDevkitComponentWizardPage page;

    public NewDevkitComponentWizard() {
        super();
        this.setWindowTitle("New DevKit Component");
        setNeedsProgressMonitor(true);
        this.setDefaultPageImageDescriptor(DevkitImages.getManaged("", "mulesoft-logo.png"));
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        if (page == null) {
            page = new NewDevkitComponentWizardPage("org.mule.modules.component");
        }
        page.init(selection);
    }

    @Override
    public void addPages() {
        if (page == null) {
            page = new NewDevkitComponentWizardPage("org.mule.modules.component");
        }
        addPage(page);
    }

    @Override
    public IWizardPage getNextPage(IWizardPage currentPage) {
        return null;
    }

    @Override
    public boolean performFinish() {
        // TODO Auto-generated method stub
        return page.performFinish();
    }

}
