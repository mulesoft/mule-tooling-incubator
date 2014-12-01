package org.mule.tooling.ui.extension.aop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.ui.utils.UiUtils;


public class EnableAopHandler  implements IObjectActionDelegate {

    @Override
    public void run(IAction action) {
        IMuleProject muleProject = UiUtils.getSelectedMuleProjectInPackageExplorer();
        IFolder muleAppsFolder = muleProject.getMuleAppsFolder();
        IResource deployPropertiesResourse = muleAppsFolder.findMember("mule-deploy.properties");
        if ( deployPropertiesResourse != null && deployPropertiesResourse instanceof IFile ){
            IFile file = (IFile) deployPropertiesResourse;
            Properties properties = new Properties();
            try {
                properties.load(file.getContents());
                String configBuilder = (String) properties.get("config.builder");
                if ( configBuilder != null && "org.mule.modules.aop.mule.AspectXmlConfigurationBuilder".equals(configBuilder) ){
                    properties.remove("config.builder");
                    MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Aop Deactivated", "Mule Aop is no longer active");
                }
                else{
                    
                    properties.setProperty("config.builder", "org.mule.modules.aop.mule.AspectXmlConfigurationBuilder");
                    MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Aop Activated", "Mule Aop is now active");

                }
                NullProgressMonitor monitor = new NullProgressMonitor();
                file.setContents(doSave(properties, monitor), IFile.FORCE, monitor);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public ByteArrayInputStream doSave(Properties props, IProgressMonitor monitor)
    {
        try
        {
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            props.store(out, "");

            // use the created buffer as inputStream for the IFile.setContents method.
            //  
            ByteArrayInputStream inputStream=new ByteArrayInputStream(out.toByteArray());

            return inputStream;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        // TODO Auto-generated method stub

    }


}
