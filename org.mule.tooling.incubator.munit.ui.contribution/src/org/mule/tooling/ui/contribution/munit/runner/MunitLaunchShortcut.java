package org.mule.tooling.ui.contribution.munit.runner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;

public class MunitLaunchShortcut implements ILaunchShortcut2 {

    public void launch(IEditorPart editor, String mode) {
        ITypeRoot element = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
        if (element != null) {
            IMethod selectedMethod = resolveSelectedMethodName(editor, element);
            if (selectedMethod != null) {
                launch(new Object[] { selectedMethod }, mode);
            } else {
                launch(new Object[] { element }, mode);
            }
        } else {
            showNoTestsFoundDialog();
        }
    }

    private IMethod resolveSelectedMethodName(IEditorPart editor, ITypeRoot element) {

        return null;
    }

    public void launch(ISelection selection, String mode) {
        if (selection instanceof IStructuredSelection) {
            launch(((IStructuredSelection) selection).toArray(), mode);
        } else {
            showNoTestsFoundDialog();
        }
    }

    private void launch(Object[] elements, String mode) {
        try {
            File elementToLaunch = null;

            if (elements.length == 1) {
                Object selected = elements[0];
                if (selected instanceof File) {
                    elementToLaunch = (File) selected;
                }
            }
            if (elementToLaunch == null) {
                showNoTestsFoundDialog();
                return;
            }
            performLaunch(elementToLaunch, mode);
        } catch (InterruptedException e) {
            // OK, silently move on
        } catch (CoreException e) {
            MessageDialog.openError(getShell(), "Munit Launch", "Launching of JUnit tests unexpectedly failed. Check log for details.");
        }
    }

    private void showNoTestsFoundDialog() {
        MessageDialog.openInformation(getShell(), "Munit Launch", "No Munit tests found.");
    }

    private void performLaunch(File element, String mode) throws InterruptedException, CoreException {
        ILaunchConfigurationWorkingCopy temparary = createLaunchConfiguration(element);
        ILaunchConfiguration config = temparary.doSave();
        DebugUITools.openLaunchConfigurationDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), config, DebugUITools.getLaunchGroup(config, mode)
                .getIdentifier(), null);
    }

    private Shell getShell() {
        return MunitPlugin.getActiveWorkbenchShell();
    }

    private ILaunchManager getLaunchManager() {
        return DebugPlugin.getDefault().getLaunchManager();
    }

    protected String getLaunchConfigurationTypeId() {
        return "org.eclipse.jdt.munit.launchconfig";
    }

    protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(IResource resource) throws CoreException {
        final String testName = resource.getName();
        String resources = testName;

        ILaunchConfigurationType configType = getLaunchManager().getLaunchConfigurationType(getLaunchConfigurationTypeId());
        ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(testName));

        wc.setAttribute(MunitLaunchConfigurationConstants.TEST_RESOURCE, resources);
        wc.setAttribute(MunitLaunchConfigurationConstants.MUNIT_TEST_PATH, resource.getFullPath().toString());

        wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, resource.getProject().getName());
        String userVm = "";
        wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, userVm + " -Dmunit.resource=" + resources);

        return wc;
    }

    protected String[] getAttributeNamesToCompare() {
        return new String[] { IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, MunitLaunchConfigurationConstants.ATTR_TEST_CONTAINER,
                IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, MunitLaunchConfigurationConstants.ATTR_TEST_METHOD_NAME };
    }

    private static boolean hasSameAttributes(ILaunchConfiguration config1, ILaunchConfiguration config2, String[] attributeToCompare) {
        try {
            for (int i = 0; i < attributeToCompare.length; i++) {
                String val1 = config1.getAttribute(attributeToCompare[i], "");
                String val2 = config2.getAttribute(attributeToCompare[i], "");
                if (!val1.equals(val2)) {
                    return false;
                }
            }
            return true;
        } catch (CoreException e) {
            // ignore access problems here, return false
        }
        return false;
    }

    private List<ILaunchConfiguration> findExistingLaunchConfigurations(ILaunchConfigurationWorkingCopy temporary) throws CoreException {
        ILaunchConfigurationType configType = temporary.getType();

        ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(configType);
        String[] attributeToCompare = getAttributeNamesToCompare();

        List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
        for (int i = 0; i < configs.length; i++) {
            ILaunchConfiguration config = configs[i];
            if (hasSameAttributes(config, temporary, attributeToCompare)) {
                candidateConfigs.add(config);
            }
        }
        return candidateConfigs;
    }

    public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            if (ss.size() == 1) {
                return findExistingLaunchConfigurations(ss.getFirstElement());
            }
        }
        return null;
    }

    public ILaunchConfiguration[] getLaunchConfigurations(final IEditorPart editor) {
        final ITypeRoot element = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
        if (element != null) {
            IMethod selectedMethod = null;
            if (Display.getCurrent() == null) {
                final IMethod[] temp = new IMethod[1];
                Runnable runnable = new Runnable() {

                    public void run() {
                        temp[0] = resolveSelectedMethodName(editor, element);
                    }
                };
                Display.getDefault().syncExec(runnable);
                selectedMethod = temp[0];
            } else {
                selectedMethod = resolveSelectedMethodName(editor, element);
            }
            Object candidate = element;
            if (selectedMethod != null) {
                candidate = selectedMethod;
            }
            return findExistingLaunchConfigurations(candidate);
        }
        return null;
    }

    private ILaunchConfiguration[] findExistingLaunchConfigurations(Object candidate) {
        if (!(candidate instanceof IJavaElement) && candidate instanceof IAdaptable) {
            candidate = ((IAdaptable) candidate).getAdapter(IJavaElement.class);
        }
        if (candidate instanceof IJavaElement) {
            IJavaElement element = (IJavaElement) candidate;
            IJavaElement elementToLaunch = null;
            try {
                switch (element.getElementType()) {
                case IJavaElement.JAVA_PROJECT:
                case IJavaElement.PACKAGE_FRAGMENT_ROOT:
                case IJavaElement.PACKAGE_FRAGMENT:
                case IJavaElement.TYPE:
                case IJavaElement.METHOD:
                    elementToLaunch = element;
                    break;
                case IJavaElement.CLASS_FILE:
                    elementToLaunch = ((IClassFile) element).getType();
                    break;
                case IJavaElement.COMPILATION_UNIT:
                    elementToLaunch = ((ICompilationUnit) element).findPrimaryType();
                    break;
                }
                if (elementToLaunch == null) {
                    return null;
                }
                ILaunchConfigurationWorkingCopy workingCopy = createLaunchConfiguration(elementToLaunch);
                List<ILaunchConfiguration> list = findExistingLaunchConfigurations(workingCopy);
                return (ILaunchConfiguration[]) list.toArray(new ILaunchConfiguration[list.size()]);
            } catch (CoreException e) {
            }
        }
        return null;
    }

    protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(IJavaElement element) throws CoreException {
        final String testName;
        final String mainTypeQualifiedName;

        switch (element.getElementType()) {
        case IJavaElement.JAVA_PROJECT:
        case IJavaElement.PACKAGE_FRAGMENT_ROOT:
        case IJavaElement.PACKAGE_FRAGMENT: {
            String name = JavaElementLabels.getTextLabel(element, JavaElementLabels.ALL_FULLY_QUALIFIED);
            mainTypeQualifiedName = "";
            testName = name.substring(name.lastIndexOf(IPath.SEPARATOR) + 1);
        }
            break;
        case IJavaElement.TYPE: {
            mainTypeQualifiedName = ((IType) element).getFullyQualifiedName('.'); // don't replace, fix for binary inner types
            testName = element.getElementName();
        }
            break;
        case IJavaElement.METHOD: {
            IMethod method = (IMethod) element;
            mainTypeQualifiedName = method.getDeclaringType().getFullyQualifiedName('.');
            testName = method.getDeclaringType().getElementName() + '.' + method.getElementName();
        }
            break;
        default:
            throw new IllegalArgumentException("Invalid element type to create a launch configuration: " + element.getClass().getName()); //$NON-NLS-1$
        }

        ILaunchConfigurationType configType = getLaunchManager().getLaunchConfigurationType(getLaunchConfigurationTypeId());
        ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(testName));

        wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainTypeQualifiedName);
        wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, element.getJavaProject().getElementName());
        return wc;
    }

    public IResource getLaunchableResource(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            if (ss.size() == 1) {
                Object selected = ss.getFirstElement();
                if (!(selected instanceof IJavaElement) && selected instanceof IAdaptable) {
                    selected = ((IAdaptable) selected).getAdapter(IJavaElement.class);
                }
                if (selected instanceof IJavaElement) {
                    return ((IJavaElement) selected).getResource();
                }
            }
        }
        return null;
    }

    public IResource getLaunchableResource(IEditorPart editor) {
        ITypeRoot element = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
        if (element != null) {
            try {
                return element.getCorrespondingResource();
            } catch (JavaModelException e) {
            }
        }
        return null;
    }

}
