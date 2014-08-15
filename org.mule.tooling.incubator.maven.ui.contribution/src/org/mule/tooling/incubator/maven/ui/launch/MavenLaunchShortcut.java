package org.mule.tooling.incubator.maven.ui.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.mule.tooling.ui.MuleUIPlugin;

public class MavenLaunchShortcut implements ILaunchShortcut {

    public static final String MAVEN_LAUNCH_CONFIGURATION_TYPE = "org.mule.tooling.maven.mavenLaunchType";

    /**
     * Get the launch configuration type.
     * 
     * @return
     */
    protected ILaunchConfigurationType getConfigurationType() {
        return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(MAVEN_LAUNCH_CONFIGURATION_TYPE);
    }

    @Override
    public void launch(ISelection selection, String mode) {
        if (selection instanceof IStructuredSelection) {
            Object first = ((IStructuredSelection) selection).getFirstElement();
            if (first instanceof IJavaElement) {
                IJavaElement el = (IJavaElement) first;
                first = el.getResource();
            }
            if (first instanceof IResource) {
                IProject project = ((IResource) first).getProject();
                launch(project, mode);

            }
        }

    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        IEditorInput editorInput = editor.getEditorInput();
        if (editorInput instanceof FileEditorInput) {
            FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
            IProject project = fileEditorInput.getFile().getProject();
            if (project != null) {
                launch(project, mode);
            }
        }

    }

    protected void launch(IProject project, String mode) {
        ILaunchConfiguration config = findLaunchConfiguration(project, getConfigurationType());
        launch(config, mode);
    }

    protected void launch(ILaunchConfiguration config, String mode) {
        if (config != null) {
            DebugUITools.launch(config, mode);
        }
    }

    protected ILaunchConfiguration findLaunchConfiguration(IProject project, ILaunchConfigurationType configType) {
        List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>();
        try {
            ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
            for (int i = 0; i < configs.length; i++) {
                ILaunchConfiguration config = configs[i];
                String projectName = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
                if (project.getName().equals(projectName)) {
                    candidateConfigs.add(config);
                }
            }
        } catch (CoreException e) {
            MuleUIPlugin.openError(getShell(), e.getStatus());
            return null;
        }

        int candidateCount = candidateConfigs.size();

        // If no matches, create a new configuration.
        if (candidateCount < 1) {
            return createConfiguration(project);
        } else if (candidateCount == 1) {
            return (ILaunchConfiguration) candidateConfigs.get(0);
        } else {
            // Prompt the user to choose a config. A null result means the user
            // canceled the dialog, in which case this method returns null,
            // since canceling the dialog should also cancel launching anything.
            ILaunchConfiguration config = chooseConfiguration(candidateConfigs);
            if (config != null) {
                return config;
            }
        }
        return null;
    }

    protected Shell getShell() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            return window.getShell();
        }
        return null;
    }

    /**
     * Show a selection dialog that allows the user to choose one of the specified launch configurations. Return the chosen config, or <code>null</code> if the user canceled the
     * dialog.
     */
    protected ILaunchConfiguration chooseConfiguration(List<?> configList) {
        IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
        ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
        dialog.setElements(configList.toArray());
        dialog.setTitle("Select Launch Configuration");
        dialog.setMessage("Selection the launch configuration you wish to use.");
        dialog.setMultipleSelection(false);
        int result = dialog.open();
        labelProvider.dispose();
        if (result == Window.OK) {
            return (ILaunchConfiguration) dialog.getFirstResult();
        }
        return null;
    }

    /**
     * Create a new configuration based on the Mule project.
     * 
     * @param project
     * @return
     */
    protected ILaunchConfiguration createConfiguration(IProject project) {
        try {
            ILaunchConfigurationType configType = getConfigurationType();
            String projectName = project.getName();
            ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(projectName));
            wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);

            // needed for some examples to run
            wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-XX:PermSize=128M -XX:MaxPermSize=256M");

            return wc.doSave();

        } catch (CoreException e) {
            MuleUIPlugin.openError(getShell(), e.getStatus());
            return null;
        }
    }

    /**
     * Get the launch manager.
     * 
     * @return
     */
    protected ILaunchManager getLaunchManager() {
        return DebugPlugin.getDefault().getLaunchManager();
    }
}
