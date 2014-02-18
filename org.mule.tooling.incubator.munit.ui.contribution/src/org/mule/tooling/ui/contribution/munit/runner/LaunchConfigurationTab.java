package org.mule.tooling.ui.contribution.munit.runner;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * <p>
 * The tab for the Munit launch configuration where the test and project is set, UI only
 * </p>
 */
public class LaunchConfigurationTab extends AbstractLaunchConfigurationTab {

    private Label fProjLabel;
    private Text fProjText;

    private Text fTestText;
    private Label fTestLabel;

    public void createControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        setControl(comp);

        GridLayout topLayout = new GridLayout();
        topLayout.numColumns = 2;
        comp.setLayout(topLayout);

        createSingleTestSection(comp);
        createSpacer(comp);
        Dialog.applyDialogFont(comp);
        validatePage();
    }

    private void createSpacer(Composite comp) {
        Label label = new Label(comp, SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        label.setLayoutData(gd);
    }

    private void createSingleTestSection(Composite comp) {
        GridData gd = new GridData();
        gd.horizontalSpan = 2;

        fProjLabel = new Label(comp, SWT.NONE);
        fProjLabel.setText("Project");
        gd = new GridData();
        gd.horizontalIndent = 25;
        fProjLabel.setLayoutData(gd);

        fProjText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        fProjText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        fTestLabel = new Label(comp, SWT.NONE);
        gd = new GridData();
        gd.horizontalIndent = 25;
        fTestLabel.setLayoutData(gd);
        fTestLabel.setText("Test");

        fTestText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        fTestText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        fProjText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent evt) {
                validatePage();
                updateLaunchConfigurationDialog();
            }
        });

        fTestText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent evt) {
                validatePage();
                updateLaunchConfigurationDialog();
            }
        });
    }

    public void initializeFrom(ILaunchConfiguration config) {
        updateProjectFromConfig(config);
        updateTestFromConfig(config);
        validatePage();
    }

    private void updateProjectFromConfig(ILaunchConfiguration config) {
        String projectName = "";
        try {
            projectName = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
        } catch (CoreException ce) {
        }

        fProjText.setText(projectName);
    }

    private void updateTestFromConfig(ILaunchConfiguration config) {
        String testName = "";
        try {
            testName = config.getAttribute("resource", "");
        } catch (CoreException ce) {
        }
        fTestText.setText(testName);
    }

    protected void updateLaunchConfigurationDialog() {
        if (getLaunchConfigurationDialog() != null) {
            getLaunchConfigurationDialog().updateButtons();
            getLaunchConfigurationDialog().updateMessage();
        }
    }

    public void performApply(ILaunchConfigurationWorkingCopy config) {
        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, fProjText.getText());
        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, fTestText.getText());
        config.setAttribute(MunitLaunchConfigurationConstants.TEST_RESOURCE, fTestText.getText());
        config.setAttribute(MunitLaunchConfigurationConstants.MUNIT_TEST_PATH, "/" + fProjText.getText() + "/src/test/munit/" + fTestText.getText());
    }

    public void dispose() {
        super.dispose();
    }

    public Image getImage() {
        return null;
    }

    private IWorkspaceRoot getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    public boolean isValid(ILaunchConfiguration config) {
        validatePage();
        return true;
    }

    protected void setErrorMessage(String errorMessage) {
        super.setErrorMessage(errorMessage);
    }

    private void validatePage() {
        setErrorMessage(null);
        setMessage(null);
        String projectName = fProjText.getText().trim();

        if (!isProjectConfigured(projectName))
            return;
        if (!isValidProjectName(projectName))
            return;

        IProject project = getWorkspaceRoot().getProject(projectName);

        if (!doesProjectExtist(project))
            return;
        if (!checkTestFileIsCorrect(project))
            return;

    }

    private boolean checkTestFileIsCorrect(IProject project) {
        IJavaProject javaProject = JavaCore.create(project);

        try {
            String className = fTestText.getText().trim();
            if (className.length() == 0) {
                setErrorMessage("Test file is not specified. You must enter the name of the file in the munit folder that you want to run");
                return false;
            }
            IType type = javaProject.findType(className);
            if (type == null) {
                return false;
            }

        } catch (CoreException e) {
            setErrorMessage("File not found in munit folder");
            return false;
        }

        return true;
    }

    private boolean doesProjectExtist(IProject project) {
        if (!project.exists()) {
            setErrorMessage("Project does not Exists.");
            return false;
        }

        return true;
    }

    private boolean isValidProjectName(String projectName) {
        IStatus status = ResourcesPlugin.getWorkspace().validatePath(IPath.SEPARATOR + projectName, IResource.PROJECT);
        if (!status.isOK() || !Path.ROOT.isValidSegment(projectName)) {
            setErrorMessage("Invalid Project name");
            return false;
        }

        return true;
    }

    private boolean isProjectConfigured(String projectName) {
        if (projectName.length() == 0) {
            setErrorMessage("Project not specified");
            return false;
        }
        return true;
    }

    public void setDefaults(ILaunchConfigurationWorkingCopy config) {
        IJavaElement javaElement = getContext();
        if (javaElement != null) {
            initializeJavaProject(javaElement, config);
        } else {
            config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
            config.setAttribute(MunitLaunchConfigurationConstants.ATTR_TEST_CONTAINER, "");
        }
        initializeTestAttributes(javaElement, config);
    }

    private void initializeTestAttributes(IJavaElement javaElement, ILaunchConfigurationWorkingCopy config) {
        if (javaElement != null && javaElement.getElementType() < IJavaElement.COMPILATION_UNIT) {

            initializeTestContainer(javaElement, config);
        }
    }

    private void initializeTestContainer(IJavaElement javaElement, ILaunchConfigurationWorkingCopy config) {
        config.setAttribute(MunitLaunchConfigurationConstants.ATTR_TEST_CONTAINER, javaElement.getHandleIdentifier());
        initializeName(config, javaElement.getElementName());
    }

    private void initializeName(ILaunchConfigurationWorkingCopy config, String name) {
        if (name == null) {
            name = "";
        }
        if (name.length() > 0) {
            int index = name.lastIndexOf('.');
            if (index > 0) {
                name = name.substring(index + 1);
            }
            name = getLaunchConfigurationDialog().generateName(name);
            config.rename(name);
        }
    }

    public String getName() {
        return MunitLaunchConfigurationConstants.CONFIGURATION_TAB_NAME;
    }

    private IJavaElement getContext() {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            return null;
        }
        IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
        if (page != null) {
            ISelection selection = page.getSelection();
            if (selection instanceof IStructuredSelection) {
                IStructuredSelection ss = (IStructuredSelection) selection;
                if (!ss.isEmpty()) {
                    Object obj = ss.getFirstElement();
                    if (obj instanceof IJavaElement) {
                        return (IJavaElement) obj;
                    }
                    if (obj instanceof IResource) {
                        IJavaElement je = JavaCore.create((IResource) obj);
                        if (je == null) {
                            IProject pro = ((IResource) obj).getProject();
                            je = JavaCore.create(pro);
                        }
                        if (je != null) {
                            return je;
                        }
                    }
                }
            }
            IEditorPart part = page.getActiveEditor();
            if (part != null) {
                IEditorInput input = part.getEditorInput();
                return (IJavaElement) input.getAdapter(IJavaElement.class);
            }
        }
        return null;
    }

    private void initializeJavaProject(IJavaElement javaElement, ILaunchConfigurationWorkingCopy config) {
        IJavaProject javaProject = javaElement.getJavaProject();
        String name = null;
        if (javaProject != null && javaProject.exists()) {
            name = javaProject.getElementName();
        }
        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, name);
    }

    public String getId() {
        return MunitLaunchConfigurationConstants.MUNIT_TEST_ID;
    }

}
