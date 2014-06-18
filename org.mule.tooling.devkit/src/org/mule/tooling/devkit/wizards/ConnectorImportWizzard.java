package org.mule.tooling.devkit.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.BuildAction;
import org.mule.tooling.devkit.builder.DevkitBuilder;
import org.mule.tooling.devkit.builder.DevkitNature;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.maven.MavenInfo;
import org.mule.tooling.devkit.maven.UpdateProjectClasspathWorkspaceJob;

public class ConnectorImportWizzard extends AbstractDevkitProjectWizzard implements IImportWizard {

    ConnectorImportWizzardPage importPage;
    IWorkbenchWindow window = null;

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        window = workbench.getActiveWorkbenchWindow();
    }

    public void addPages() {
        importPage = new ConnectorImportWizzardPage(null);
        addPage(importPage);
    }

    @Override
    public boolean performFinish() {
        final Object[] items = importPage.getSelectedItems();
        IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                monitor.beginTask("Importing modules", items.length);
                for (int index = 0; index < items.length; index++) {
                    final MavenInfo mavenProject = (MavenInfo) items[index];

                    final File folder = mavenProject.getProjectRoot();

                    try {
                        if (folder != null && folder.exists()) {
                            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                            try {
                                IProject project = createProject(folder.getName(), monitor, root, folder);

                                IJavaProject javaProject = JavaCore.create(root.getProject(folder.getName()));

                                List<IClasspathEntry> classpathEntries = generateProjectEntries(monitor, project);
                                javaProject.setRawClasspath(classpathEntries.toArray(new IClasspathEntry[] {}), monitor);
                                if (mavenProject.getPackaging() != null && mavenProject.getPackaging().equals("mule-module")) {
                                    configureDevkitAPT(javaProject);
                                }
                                
                                boolean autoBuilding = ResourcesPlugin.getWorkspace().isAutoBuilding();

                                if (!autoBuilding) {
                                    UpdateProjectClasspathWorkspaceJob job = new UpdateProjectClasspathWorkspaceJob(javaProject);
                                    job.run(monitor);
                                    ProjectSubsetBuildAction projectBuild = new ProjectSubsetBuildAction(window, IncrementalProjectBuilder.CLEAN_BUILD, new IProject[] { project });
                                    projectBuild.run();
                                    project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                                }
                            } catch (CoreException e) {
                                e.printStackTrace();
                            }
                        }
                    } finally {
                        monitor.worked(1);
                    }
                }
                monitor.done();
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

    private IProject createProject(String artifactId, IProgressMonitor monitor, IWorkspaceRoot root, File folder) throws CoreException {

        IProjectDescription projectDescription = getProjectDescription(root, artifactId, folder);

        return getProjectWithDescription(artifactId, monitor, root, projectDescription);
    }

    private IProjectDescription getProjectDescription(IWorkspaceRoot root, String artifactId, File folder) throws CoreException {

        File projectDescriptionFile = new File(folder, ".project");
        IProjectDescription currentDescription = null;
        ICommand[] commands = null;
        int commandsLength = 0;
        if (projectDescriptionFile.exists()) {
            currentDescription = root.getWorkspace().loadProjectDescription(Path.fromOSString(new File(folder, ".project").getAbsolutePath()));
            commands = currentDescription.getBuildSpec();
            commandsLength = commands.length;
            for (int i = 0; i < commands.length; ++i) {
                if (commands[i].getBuilderName().equals(DevkitBuilder.BUILDER_ID)) {
                    return currentDescription;
                }
            }
        }
        ICommand[] newCommands = new ICommand[commandsLength + 1];
        if (commands != null) {
            System.arraycopy(commands, 0, newCommands, 0, commandsLength);
        }
        IProjectDescription projectDescription = root.getWorkspace().newProjectDescription(artifactId);
        projectDescription.setNatureIds(new String[] { JavaCore.NATURE_ID, DevkitNature.NATURE_ID });

        ICommand command = projectDescription.newCommand();
        command.setBuilderName(DevkitBuilder.BUILDER_ID);
        newCommands[newCommands.length - 1] = command;

        projectDescription.setBuildSpec(newCommands);

        projectDescription.setLocation(Path.fromOSString(folder.getAbsolutePath()));

        return projectDescription;
    }

    private class ProjectSubsetBuildAction extends BuildAction {

        private IProject[] projectsToBuild = new IProject[0];

        public ProjectSubsetBuildAction(IShellProvider shellProvider, int type, IProject[] projects) {
            super(shellProvider, type);
            this.projectsToBuild = projects;
        }

        @Override
        protected List getSelectedResources() {
            return Arrays.asList(this.projectsToBuild);
        }
    }

}
