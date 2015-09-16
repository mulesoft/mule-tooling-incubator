package org.mule.tooling.devkit.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.mule.tooling.maven.ui.MavenUIPlugin;
import org.mule.tooling.maven.ui.wizards.ConfigureMavenWizardPage;

/**
 * Wizard for importing a Mule project into the workspace from a deployable zip file.
 * 
 */
public class ConnectorZippedProjectImportWizard extends AbstractDevkitProjectWizzard implements IImportWizard {

    private ConnectorZippedProjectImportPage mainPage;
    private ConfigureMavenWizardPage configureMavenPage;
    
    public ConnectorZippedProjectImportWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    public void addPages() {
        if (!MavenUIPlugin.getDefault().getPreferences().isGlobalMavenSupportEnabled()) {
            configureMavenPage = new ConfigureMavenWizardPage();
            addPage(configureMavenPage);
        }
        mainPage = new ConnectorZippedProjectImportPage();
        addPage(mainPage);
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof ConfigureMavenWizardPage) {
            configureMavenPage.nextPressed();
        }
        return super.getNextPage(page);
    }
    
    public void init(final IWorkbench workbench, final IStructuredSelection currentSelection) {
        setWindowTitle("Anypoint Connector Import from Zip Archive");
    }

    public boolean performCancel() {
        return true;
    }

    public boolean performFinish() {
        return mainPage.performFinish();
    }
}
