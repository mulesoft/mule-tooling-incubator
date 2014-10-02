package org.mule.tooling.devkit.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;

public class ConnectorImportWizzard extends Wizard implements IImportWizard {

    ConnectorImportWizzardPage importPage;
    IWorkbenchWindow window = null;

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        window = workbench.getActiveWorkbenchWindow();
    }

    public void addPages() {
        importPage = new ConnectorImportWizzardPage(null);
        addPage(importPage);
    }

    @Override
    public boolean performFinish() {
        return importPage.performFinish();
       
    }

}
