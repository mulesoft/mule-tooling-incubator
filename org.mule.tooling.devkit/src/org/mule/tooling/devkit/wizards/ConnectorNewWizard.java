package org.mule.tooling.devkit.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.INewWizard;

import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.maven.BaseDevkitGoalRunner;
import org.mule.tooling.devkit.maven.MavenDevkitProjectDecorator;
import org.mule.tooling.devkit.template.replacer.ClassReplacer;
import org.mule.tooling.devkit.template.replacer.ConnectorClassReplacer;

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
        page = new ConnectorNewWizardPage(selection);
        addPage(page);
    }

    protected String getType() {
        return "connector";
    }
    
    @Override
    public boolean performFinish() {
    	ConnectorNewWizardPage connectorPage = (ConnectorNewWizardPage) page;; 
        final IPackageFragment packageFragment = connectorPage.getPackageFragment();
        final String moduleName = connectorPage.getName();
        final boolean isOAuthEnabled = connectorPage.isOAuthEnabled();
        final boolean isMetadataEnabled = connectorPage.isMetadataEnabled();
        final boolean isFromWsdl = connectorPage.isFromWsdl();
        final String wsdlFile = connectorPage.getWsdlFile(); 
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
//                try {
//           		 String className = getClassNameFrom(moduleName);
//                 String packageName = packageFragment.getElementName();
//                	if ( isFromWsdl ){
//                		IProject project = packageFragment.getJavaProject().getProject();
//                		Properties properties = new Properties();
//                		properties.setProperty("friendlyName", moduleName);
//                		properties.setProperty("schemaVersion", "1.0");
//                		properties.setProperty("name", moduleName);
//                		WsdlToJavaOperation wsdlToJavaOperation = new WsdlToJavaOperation();
//                		wsdlToJavaOperation.setWsdlFile(new File(wsdlFile));
//                		wsdlToJavaOperation.setOutputFolder(project.getFolder("/src/main/java"));
//                		wsdlToJavaOperation.setPackageName(packageName + ".api");
//                		try {
//							wsdlToJavaOperation.run(monitor);
//							new BaseDevkitGoalRunner(new String[] { "clean", "package", "-DskipTests", "-Ddevkit.studio.package.skip=true" }).run(MavenDevkitProjectDecorator.decorate(packageFragment.getJavaProject()).getPomFile(), monitor);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//                 		new WsdlProjectGenerator().generate(project.getLocation().toFile(), packageName + ".api", className, wsdlFile, properties);
//
//                	}
//                	else{

//                         ClassReplacer classReplacer = new ConnectorClassReplacer(packageName, moduleName, className, isMetadataEnabled, isOAuthEnabled);
//                         	
//                         doFinish(packageFragment, moduleName, monitor, classReplacer, className, packageName);
//                	}
                	  
//                } catch (CoreException e) {
//                    throw new InvocationTargetException(e);
//                } finally {
//                    monitor.done();
//                }
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
    
    

}