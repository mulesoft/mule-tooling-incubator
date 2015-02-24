package org.mule.tooling.devkit.export;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.mule.tooling.devkit.DevkitUIPlugin;

public class UpdateSiteExportWizard extends Wizard implements IExportWizard {

    /** Dialog settings for wizard */
    private static final String UPDATE_SITE_SECTION = "UpdateSiteExportWizard";

    /** Project associated with current selection */
    private IProject selected;

    /** Project export page */
    private DevkitExportPage mainPage;

    public UpdateSiteExportWizard() {
        super();
        setNeedsProgressMonitor(true);
        IDialogSettings workbenchSettings = DevkitUIPlugin.getDefault().getDialogSettings();
        IDialogSettings wizardSettings = workbenchSettings.getSection(UPDATE_SITE_SECTION);
        if (wizardSettings == null) {
            wizardSettings = workbenchSettings.addNewSection(UPDATE_SITE_SECTION);
        }
        setDialogSettings(wizardSettings);
        setWindowTitle("Export an Anypoint Connector as an Update Site");
    }

    public void addPages() {
        super.addPages();
        mainPage = new DevkitExportPage(selected);
        addPage(mainPage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        try {
            Object oSelected = selection.getFirstElement();
            if (oSelected != null) {
                IProject selectedProject = null;
                if (oSelected instanceof IJavaElement) {
                    selectedProject = ((IJavaElement) oSelected).getJavaProject().getProject();
                } else if (oSelected instanceof IResource) {
                    selectedProject = ((IResource) oSelected).getProject();
                }
                if ((selectedProject != null) && (selectedProject.isAccessible())) {
                    this.selected = selectedProject;
                }
            }
        } catch (Exception e) {
            DevkitUIPlugin.openError(getShell(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {
        return mainPage.performFinish();
    }

    /**
     * @return the selected
     */
    public synchronized IProject getSelectedProject() {
        return selected;
    }

    /**
     * @param selected
     *            the selected to set
     */
    public synchronized void setSelectedProject(IProject selected) {
        this.selected = selected;
    }
}