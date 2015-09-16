package org.mule.tooling.devkit.wizards;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.mule.tooling.devkit.builder.IModelPopulator;
import org.mule.tooling.devkit.builder.ProjectBuilder;
import org.mule.tooling.devkit.common.ApiType;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.ui.ConnectorProjectWidget;
import org.mule.tooling.devkit.ui.wsdl.WSDLChooserGroup;

public class NewDevkitWsdlBasedProjectWizardPage extends WizardPage implements Observer, IModelPopulator<ProjectBuilder> {

    private ConnectorProjectWidget project;
    private WSDLChooserGroup group;

    public NewDevkitWsdlBasedProjectWizardPage() {
        super("wizardPage");
        setTitle("Create an Anypoint SOAP Connect Project");
        setDescription("Create an Anypoint SOAP Connect project in the workspace or in an external location.");
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
        layout.verticalSpacing = 6;

        project = new ConnectorProjectWidget();
        ProjectObserver broadcaster = new ProjectObserver();
        broadcaster.addObserver(this);
        project.setNotifier(broadcaster);
        project.createControl(container);

        group = new WSDLChooserGroup();
        group.setNotifier(broadcaster);
        group.createControl(container);
        setControl(container);
        project.setFocus();
    }

    public String getProjectName() {
        return project.getProjectName();
    }

    public String getNamespace() {
        return project.getNameSpace();
    }

    public String getLocation() {
        return project.getLocation();
    }

    public Map<String, String> getWsdlPath() {

        return group.getWsdlFiles();
    }

    @Override
    public void update(Observable o, Object arg) {
        IStatus status = project.validate();
        if (!Status.OK_STATUS.equals(status)) {
            setPageComplete(false);
            setErrorMessage(status.getMessage());
        } else {
            setPageComplete(!group.hasErrors());
            setErrorMessage(null);
        }
    }

    @Override
    public void populate(ProjectBuilder model) {
        model.withApiType(ApiType.WSDL).withGenerateDefaultBody(false).withProjectName(getProjectName()).withModuleName(getNamespace())
                .withConnectorClassName(DevkitUtils.createConnectorNameFrom(getConnectorName())).withWsdlFiles(getWsdlPath()).withConnectorName(getConnectorName());
        if (!project.useDefaultValues()) {
            model.withProjectLocation(getLocation());
        }
    }

    public String getConnectorName() {
        return project.getName();
    }

}