package org.mule.tooling.devkit.wizards;

import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_JAVA_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.TEST_JAVA_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.TEST_RESOURCES_FOLDER;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.template.ImageWriter;
import org.mule.tooling.devkit.template.TemplateFileWriter;
import org.mule.tooling.devkit.template.replacer.ClassReplacer;

public class ModuleNewWizard extends Wizard implements INewWizard {

    protected ModuleNewWizardPage page;
    protected ISelection selection;

    private static final String MAIN_TEMPLATE_PATH = "/templates/module_main.tmpl";
    private static final String TEST_TEMPLATE_PATH = "/templates/connector_test.tmpl";
    private static final String TEST_RESOURCE_PATH = "/templates/module-test-resource.tmpl";

    /**
     * Constructor for ConnectorNewWizard.
     */
    public ModuleNewWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    /**
     * Adding the page to the wizard.
     */

    public void addPages() {
        page = new ModuleNewWizardPage(selection);
        addPage(page);
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We will create an operation and run it using wizard as execution context.
     */
    public boolean performFinish() {
        final IPackageFragment packageFragment = page.getPackageFragment();
        final String moduleName = page.getName();
        final boolean isMetadataEnabled = page.isMetadataEnabled();
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    String className = getClassNameFrom(moduleName);
                    String packageName = packageFragment.getElementName();
                    ClassReplacer classReplacer = new ClassReplacer(packageName, moduleName, className, isMetadataEnabled);

                    doFinish(packageFragment, moduleName, monitor, classReplacer, className, packageName);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            getContainer().run(true, false, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }

    protected void doFinish(IPackageFragment packageFragment, String moduleName, IProgressMonitor monitor, ClassReplacer classReplacer, String className, String packageName) throws CoreException {


        IProject project = packageFragment.getJavaProject().getProject();

        create(moduleName, monitor, getMainTemplatePath(), getTestResourcePath(), className, packageName, project, classReplacer);

    }

    protected String getClassNameFrom(String moduleName) {
        return DevkitUtils.createModuleNameFrom(moduleName);
    }

    protected String getTestResourcePath() {
        return TEST_RESOURCE_PATH;
    }

    protected String getMainTemplatePath() {
        return MAIN_TEMPLATE_PATH;
    }

    protected void create(String moduleName, IProgressMonitor monitor, String mainTemplatePath, String testResourceTemplatePath, String className, String packageName,
            IProject project, ClassReplacer classReplacer) throws CoreException {
        monitor.beginTask("Creating " + moduleName, 2);

        TemplateFileWriter fileWriter = new TemplateFileWriter(project, monitor);
        ImageWriter imageWriter = new ImageWriter(project, monitor);
        fileWriter.apply(mainTemplatePath, buildMainTargetFilePath(packageName, className), classReplacer);
        fileWriter.apply(testResourceTemplatePath, getResourceExampleFileName(moduleName), classReplacer);
        fileWriter.apply(TEST_TEMPLATE_PATH, buildTestTargetFilePath(packageName, className), classReplacer);
        fileWriter.apply("/templates/example.tmpl", getExampleFileName(moduleName), classReplacer);

        imageWriter.apply("/templates/extension-icon-24x16.png", getIcon24FileName(moduleName));
        imageWriter.apply("/templates/extension-icon-48x32.png", getIcon48FileName(moduleName));

        monitor.worked(1);
    }

    private String getResourceExampleFileName(String connectorName) {
        return TEST_RESOURCES_FOLDER + "/" + connectorName.toLowerCase() + "-config.xml";
    }

    private String getIcon48FileName(String connectorName) {
        return "icons/" + connectorName.toLowerCase() + "-connector-48x32.png";
    }

    private String getIcon24FileName(String connectorName) {
        return "icons/" + connectorName.toLowerCase() + "-connector-24x16.png";
    }

    private String getExampleFileName(String connectorName) {
        return "doc" + "/" + connectorName.toLowerCase() + "-" + getType() + ".xml.sample";
    }

    protected String buildMainTargetFilePath(String packageName, String className) {
        return MAIN_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + className + ".java";
    }

    private String buildTestTargetFilePath(String packageName, String className) {
        return TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + className + "Test.java";
    }

    protected String getType() {
        return "module";
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
}