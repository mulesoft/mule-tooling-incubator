package org.mule.tooling.incubator.gradle.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;


public abstract class AbstractGradleAwareActionDelegate implements IObjectActionDelegate {
    
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        
        try {
            IMuleProject muleProject = CoreUtils.getMuleProjectForSelection((IStructuredSelection) selection);
            
            if (muleProject == null) {
                action.setEnabled(false);
                return;
            }
            
            IProject project = muleProject.getJavaProject().getProject();
            
            action.setEnabled(isGradleProject(project));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * TODO - consider moving to a better location or to improve the way this gets cached.
     * @param project
     * @return
     */
    private static boolean isGradleProject(IProject project) {
        
        boolean ret = GradlePluginUtils.shallowCheckIsGradleproject(project);
        return ret;
    
    }
}
