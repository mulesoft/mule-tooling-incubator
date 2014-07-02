package org.mule.tooling.devkit.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Wizard for importing a Mule project into the workspace from a deployable zip file.
 * 
 */
public class ConnectorZippedProjectImportWizard extends Wizard implements IImportWizard {

    private ConnectorZippedProjectImportPage mainPage;

    private IWorkbenchWindow window;
    public ConnectorZippedProjectImportWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    public void addPages() {
        mainPage = new ConnectorZippedProjectImportPage();
        addPage(mainPage);
    }

    public void init(final IWorkbench workbench, final IStructuredSelection currentSelection) {
        setWindowTitle("Anypoint Connector Import from Zip Archive");
        window = workbench.getActiveWorkbenchWindow();
    }

    public boolean performCancel() {
        return true;
    }

    public boolean performFinish() {
        return mainPage.performFinish(window);
    }
}
