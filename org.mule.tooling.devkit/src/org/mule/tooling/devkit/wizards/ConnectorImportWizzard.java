package org.mule.tooling.devkit.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.mule.tooling.devkit.builder.DevkitBuilder;
import org.mule.tooling.devkit.builder.DevkitNature;
import org.mule.tooling.devkit.builder.ProjectSubsetBuildAction;
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
        final IRunnableWithProgress op = new IRunnableWithProgress() {

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
                                    DevkitUtils.configureDevkitAPT(javaProject);
                                }

                                boolean autoBuilding = ResourcesPlugin.getWorkspace().isAutoBuilding();

                                UpdateProjectClasspathWorkspaceJob job = new UpdateProjectClasspathWorkspaceJob(javaProject, new String[] { "clean","compile", "eclipse:eclipse" });
                                job.run(monitor);
                                project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
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
        Job job = new WorkspaceJob("Importing connector") {

            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                try {
                    op.run(monitor);
                } catch (InterruptedException e) {
                    return Status.OK_STATUS;
                } catch (InvocationTargetException e) {
                    Throwable realException = e.getTargetException();
                    MessageDialog.openError(getShell(), "Error", realException.getMessage());
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
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

}
