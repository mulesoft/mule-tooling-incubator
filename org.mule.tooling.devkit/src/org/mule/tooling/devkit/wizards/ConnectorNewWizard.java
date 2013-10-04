package org.mule.tooling.devkit.wizards;

import org.eclipse.ui.INewWizard;
import org.mule.tooling.devkit.common.DevkitUtils;

public class ConnectorNewWizard extends ModuleNewWizard implements INewWizard {

    private static final String MAIN_TEMPLATE_PATH = "/templates/connector_main.tmpl";
    private static final String TEST_RESOURCE_PATH = "/templates/connector-test-resource.tmpl";

    protected String getClassNameFrom(String moduleName) {
        return DevkitUtils.createConnectorNameFrom(moduleName);
    }

    protected String getTestResourcePath() {
        return TEST_RESOURCE_PATH;
    }

    protected String getMainTemplatePath() {
        return MAIN_TEMPLATE_PATH;
    }

    public ConnectorNewWizard() {
        super();
    }

    public void addPages() {
        page = new ModuleNewWizardPage(selection);
        addPage(page);
    }

    protected String getType() {
        return "connector";
    }

}