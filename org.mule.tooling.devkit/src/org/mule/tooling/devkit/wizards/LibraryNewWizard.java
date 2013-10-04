package org.mule.tooling.devkit.wizards;

import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_MULE_FOLDER;

import org.eclipse.ui.INewWizard;

public class LibraryNewWizard extends ModuleNewWizard implements INewWizard {

    private static final String MAIN_TEMPLATE_PATH = "/templates/library_main.tmpl";
    private static final String TEST_RESOURCE_PATH = "/templates/library-test-resource.tmpl";

    protected String getClassNameFrom(String moduleName) {
        return moduleName;
    }

    protected String getTestResourcePath() {
        return TEST_RESOURCE_PATH;
    }

    protected String getMainTemplatePath() {
        return MAIN_TEMPLATE_PATH;
    }

    public LibraryNewWizard() {
        super();
    }

    public void addPages() {
        page = new ModuleNewWizardPage(selection);
        addPage(page);
    }

    protected String buildMainTargetFilePath(String packageName, String className) {
        return MAIN_MULE_FOLDER + "/" + className + ".xml";
    }

    protected String getType() {
        return "library";
    }

}
