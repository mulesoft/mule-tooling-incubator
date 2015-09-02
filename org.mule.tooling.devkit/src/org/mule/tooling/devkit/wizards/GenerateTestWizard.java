package org.mule.tooling.devkit.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.mule.tooling.devkit.DevkitImages;
import org.mule.tooling.devkit.popup.dto.TestDataModelDto;

public class GenerateTestWizard extends AbstractDevkitProjectWizzard implements INewWizard {

    private GenerateTestWizardPage initPage;
    private IProject selectedProject;
    private TestDataModelDto dataModel;

    public GenerateTestWizard(IProject project) {
        super();
        setWindowTitle("Connector's Tests Scaffolding Generation");
        setNeedsProgressMonitor(true);
        setDefaultPageImageDescriptor(DevkitImages.getManaged("", "mulesoft-logo.png"));
        dataModel = new TestDataModelDto();
        selectedProject = project;
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {

    }

    @Override
    public void addPages() {
        initPage = new GenerateTestWizardPage(selectedProject, dataModel);
        addPage(initPage);
    }

    @Override
    public boolean performFinish() {

        TestDataModelDto basicData = initPage.getDataModel();
        dataModel = new TestDataModelDto(basicData);

        return true;
    }

    public TestDataModelDto getRunConfig() {
        return dataModel;
    }
}
