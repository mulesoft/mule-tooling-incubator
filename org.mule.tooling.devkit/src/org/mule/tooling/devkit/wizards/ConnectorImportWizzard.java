package org.mule.tooling.devkit.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.mule.tooling.maven.ui.MavenUIPlugin;
import org.mule.tooling.maven.ui.wizards.ConfigureMavenWizardPage;

public class ConnectorImportWizzard extends AbstractDevkitProjectWizzard implements IImportWizard {

    private ConnectorImportWizzardPage importPage;
    private ConfigureMavenWizardPage configureMavenPage;

    public ConnectorImportWizzard() {
        super();
        setNeedsProgressMonitor(true);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("Anypoint Connector Import from Folder");
    }

    public void addPages() {
        if (!MavenUIPlugin.getDefault().getPreferences().isGlobalMavenSupportEnabled()) {
            configureMavenPage = new ConfigureMavenWizardPage();
            addPage(configureMavenPage);
        }
        importPage = new ConnectorImportWizzardPage();
        addPage(importPage);
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof ConfigureMavenWizardPage) {
            configureMavenPage.nextPressed();
        }
        return super.getNextPage(page);
    }

    public boolean performCancel() {
        return true;
    }

    @Override
    public boolean performFinish() {
        return importPage.performFinish();
    }
}
