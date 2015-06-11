package org.mule.tooling.devkit.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.mule.tooling.devkit.DevkitImages;
import org.mule.tooling.devkit.builder.IModelPopulator;
import org.mule.tooling.devkit.builder.ProjectBuilder;
import org.mule.tooling.devkit.builder.ProjectBuilderFactory;
import org.mule.tooling.devkit.builder.ProjectSubsetBuildAction;
import org.mule.tooling.devkit.maven.MavenRunBuilder;
import org.mule.tooling.devkit.maven.UpdateProjectClasspathWorkspaceJob;
import org.mule.tooling.maven.ui.wizards.ConfigureMavenWizardPage;

public class NewDevkitProjectWizard extends AbstractDevkitProjectWizzard implements INewWizard {

    public static final String WIZZARD_PAGE_TITTLE = "Create an Anypoint Connector";

    private List<IWizardPage> sdkPages;
    private List<IWizardPage> soapPages;

    private NewDevkitProjectWizardPage page;
    private NewDevkitProjectWizardApiPage apiPage;
    private ConfigureMavenWizardPage configureMavenPage;
    private NewDevkitProjectWizardPageAdvance advancePage;
    private NewDevkitWsdlBasedProjectWizardPage soapFirstPage;
    private NewDevkitWsdlBasedProjectWizardAdvancePage soapSecondPage;

    private boolean wasCreated;

    public NewDevkitProjectWizard() {
        super();
        this.setWindowTitle("New Anypoint Connector Project");
        setNeedsProgressMonitor(true);
        this.setDefaultPageImageDescriptor(DevkitImages.getManaged("", "mulesoft-logo.png"));
    }

    @Override
    public void addPages() {
        // if (!MavenUIPlugin.getDefault().getPreferences().isGlobalMavenSupportEnabled()) {
        // configureMavenPage = new ConfigureMavenWizardPage();
        // addPage(configureMavenPage);
        // }
        apiPage = new NewDevkitProjectWizardApiPage();
        page = new NewDevkitProjectWizardPage();
        advancePage = new NewDevkitProjectWizardPageAdvance();
        soapFirstPage = new NewDevkitWsdlBasedProjectWizardPage();
        soapSecondPage = new NewDevkitWsdlBasedProjectWizardAdvancePage();

        sdkPages = new ArrayList<IWizardPage>();
        soapPages = new ArrayList<IWizardPage>();

        sdkPages.add(page);
        sdkPages.add(advancePage);
        sdkPages.add(soapSecondPage);

        soapPages.add(soapFirstPage);
        soapPages.add(advancePage);
        soapPages.add(soapSecondPage);

        addPage(apiPage);
        addPage(page);
        addPage(soapFirstPage);
        addPage(advancePage);
        addPage(soapSecondPage);
    }

    @Override
    public IWizardPage getNextPage(IWizardPage currentPage) {
        if (currentPage instanceof ConfigureMavenWizardPage) {
            configureMavenPage.nextPressed();
            return apiPage;
        } else if (currentPage instanceof NewDevkitProjectWizardApiPage) {
            if (apiPage.getConnectorType() == "JDK") {
                return page;
            } else {
                return soapFirstPage;
            }
        } else if ((currentPage instanceof NewDevkitProjectWizardPage) || (currentPage instanceof NewDevkitWsdlBasedProjectWizardPage)) {
            advancePage.setConnectorName(getConnectorName());
            advancePage.refresh();
            return advancePage;
        } else if (currentPage instanceof NewDevkitProjectWizardPageAdvance) {
            if (apiPage.getConnectorType() == "JDK") {
                soapSecondPage.hideScurityGroup();
            } else {
                soapSecondPage.showScurityGroup();
            }
            return soapSecondPage;
        }
        return null;
    }

    @Override
    public boolean performFinish() {
        wasCreated = false;
        final ProjectBuilder builder = ProjectBuilderFactory.newInstance();

        populate(builder);

        final IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    monitor.beginTask("Creating Anypoint Connector Project", 1000);
                    builder.build(monitor);

                    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

                    IJavaProject javaProject = JavaCore.create(root.getProject(builder.getProjectName()));

                    downloadJavadocForAnnotations(javaProject, monitor);

                    boolean autoBuilding = ResourcesPlugin.getWorkspace().isAutoBuilding();
                    UpdateProjectClasspathWorkspaceJob job = new UpdateProjectClasspathWorkspaceJob(javaProject);
                    monitor.subTask("Downloading dependencies");
                    job.runInWorkspace(monitor);
                    if (!autoBuilding) {

                        ProjectSubsetBuildAction projectBuild = new ProjectSubsetBuildAction(new IShellProvider() {

                            @Override
                            public Shell getShell() {
                                return page.getShell();
                            }
                        }, IncrementalProjectBuilder.CLEAN_BUILD, new IProject[] { javaProject.getProject() });
                        projectBuild.run();

                    }
                    javaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);

                    openConnectorClass(builder.getConnectorClassName(), javaProject.getProject());

                    wasCreated = true;
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
        if (!runInContainer(op)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean performCancel() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        if (wasCreated && StringUtils.isEmpty(page.getProjectName())) {
            IProject project = root.getProject(page.getProjectName());
            if (project != null) {
                try {
                    project.delete(true, new NullProgressMonitor());
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * Runs a work inside the container so that the logs are shown in the UI wizard
     * 
     * @param work
     *            Work with progress
     * @return true is the result was successfully
     */
    private boolean runInContainer(final IRunnableWithProgress work) {
        try {
            getContainer().run(true, true, work);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }

        return true;
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {

    }

    private void downloadJavadocForAnnotations(IJavaProject javaProject, IProgressMonitor monitor) {
        MavenRunBuilder
                .newMavenRunBuilder()
                .withProject(javaProject)
                .withArgs(
                        new String[] { "dependency:resolve", "-Dclassifier=javadoc", "-DexcludeTransitive=false", "-DincludeGroupIds=org.mule.tools.devkit",
                                "-DincludeArtifactIds=mule-devkit-annotations" }).withTaskName("Downloading annotations sources...").build().run(monitor);
    }

    @Override
    public boolean canFinish() {
        List<IWizardPage> currentPageList;
        if (apiPage.getConnectorType() == "JDK") {
            currentPageList = sdkPages;
        } else {
            currentPageList = soapPages;
        }

        for (int i = 0; i < currentPageList.size(); i++) {
            if (!currentPageList.get(i).isPageComplete()) {
                return false;
            }
        }
        return true;
    }

    private void openConnectorClass(final String connectorClassName, final IProject project) {

        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                try {
                    IFile connectorFile = project.getFile(connectorClassName);
                    if (connectorFile.exists()) {
                        IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), connectorFile);
                    } else {
                        // Inform severe error
                    }
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void populate(ProjectBuilder builder) {
        List<IWizardPage> currentPageList;
        if (apiPage.getConnectorType() == "JDK") {
            currentPageList = sdkPages;
        } else {
            currentPageList = soapPages;
        }

        for (Object populator : currentPageList) {
            if (populator instanceof IModelPopulator) {
                @SuppressWarnings("unchecked")
                IModelPopulator<ProjectBuilder> modelPopulator = (IModelPopulator<ProjectBuilder>) populator;
                modelPopulator.populate(builder);
            }
        }

    }

    private String getConnectorName() {
        String name = "";
        if (apiPage.getConnectorType() == "JDK") {
            name = page.getConnectorName();
        } else {
            name = soapFirstPage.getConnectorName();
        }
        return name;
    }
}
