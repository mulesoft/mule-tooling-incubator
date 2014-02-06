package org.mule.tooling.ui.contribution.munit.runner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;


public class MunitLaunchConfigurationConstants {
    private static final String MUNIT_LAUNCH_CONFIG = "org.eclipse.jdt.munit.launchconfig";
    public static final String ATTR_TEST_METHOD_NAME = "methodName";
    public static final String CONFIGURATION_TAB_NAME = "Test";
    public static final String TEST_RESOURCE = "resource";
    public static final String MUNIT_TEST_PATH = "Mpath";
    public static final String ATTR_TEST_CONTAINER = "container";
    public static final String MUNIT_TEST_ID = "Munit Test";
    public static final String ATTR_FAILURES_NAMES = "failures";

    public static IJavaProject getJavaProject(ILaunchConfiguration configuration) {
        try {
            String projectName= configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
            if (projectName != null && projectName.length() > 0) {
                return JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName));
            }
        } catch (CoreException e) {
        }
        return null;
    }

    public static void runTest(IFile inputFile, String mode) {
        final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null) {
            IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
            try {
                activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW);
                ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

                if ( !runPresentContainer(inputFile, launchManager, mode) ){
                    ILaunchConfiguration doSave = crateNewConfig(inputFile, launchManager);
                    DebugUITools.openLaunchConfigurationDialog(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            doSave,
                            DebugUITools.getLaunchGroup(doSave, mode).getIdentifier(),
                            null);
                }

            } catch (PartInitException e) {
                MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Munit could not run", "Munit could not run the tests, " +
                        "try closing the editor, refreshing your workspace and opening the editor again."); 
            } catch (CoreException e) {
                MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Munit could not run", "Munit could not run the tests, " +
                        "try closing the editor, refreshing your workspace and opening the editor again."); 
            }
        }
    }

    private static ILaunchConfiguration crateNewConfig(IFile inputFile, ILaunchManager launchManager) throws CoreException {
        ILaunchConfigurationType launchConfiguration = launchManager.getLaunchConfigurationType(MUNIT_LAUNCH_CONFIG);
        ILaunchConfigurationWorkingCopy newInstance = launchConfiguration.newInstance(null,  launchManager.generateLaunchConfigurationName(inputFile.getName()));
        newInstance.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, inputFile.getProject().getName());
        newInstance.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, inputFile.getName());
        newInstance.setAttribute(MunitLaunchConfigurationConstants.TEST_RESOURCE, inputFile.getName());
        newInstance.setAttribute(MunitLaunchConfigurationConstants.MUNIT_TEST_PATH, inputFile.getLocation().toString());

        ILaunchConfiguration doSave = newInstance.doSave();
        return doSave;
    }

    private static boolean runPresentContainer(IFile inputFile, ILaunchManager launchManager, String mode) throws CoreException {
        ILaunchConfiguration[] launchConfigurations = launchManager.getLaunchConfigurations();
        for ( ILaunchConfiguration configuration : launchConfigurations ){
            if ( !configuration.getAttribute(MunitLaunchConfigurationConstants.TEST_RESOURCE, "").isEmpty() && configuration.getAttribute(MunitLaunchConfigurationConstants.TEST_RESOURCE, "").equals(inputFile.getName())){
                DebugUIPlugin.buildAndLaunch(configuration, mode, new NullProgressMonitor());
                return true;
            }
        }
        return false;
    }

}
