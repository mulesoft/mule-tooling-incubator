package org.mule.tooling.devkit.wizards;

import static org.mule.tooling.devkit.common.DevkitUtils.DOCS_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.ICONS_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_FLOWS_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_JAVA_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_MULE_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_RESOURCES_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.POM_FILENAME;
import static org.mule.tooling.devkit.common.DevkitUtils.POM_TEMPLATE_PATH;
import static org.mule.tooling.devkit.common.DevkitUtils.TEST_JAVA_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.TEST_RESOURCES_FOLDER;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.mule.tooling.devkit.builder.DevkitBuilder;
import org.mule.tooling.devkit.builder.DevkitNature;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.common.MavenModel;
import org.mule.tooling.devkit.template.TemplateFileWriter;
import org.mule.tooling.devkit.template.replacer.MavenParameterReplacer;
import org.mule.tooling.devkit.template.replacer.NullReplacer;

public class NewDevkitProjectWizard extends Wizard implements INewWizard {

    private NewDevkitProjectWizardPage page;
    private ISelection selection;

    public NewDevkitProjectWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        page = new NewDevkitProjectWizardPage(selection);
        addPage(page);
    }

    @Override
    public boolean performFinish() {
        final MavenModel mavenModel = new MavenModel(page.getVersion(), page.getGroupId(), page.getArtifactId());

        IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    doFinish(mavenModel, monitor);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
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

    /**
     * The worker method. It will find the container, create the file if missing or just replace its contents, and open the editor on the newly created file.
     */

    private void doFinish(MavenModel mavenModel, IProgressMonitor monitor) throws CoreException {
        String artifactId = mavenModel.getArtifactId();
        String groupId = mavenModel.getGroupId();
        monitor.beginTask("Creating project" + artifactId, 2);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        IProject project = createProject(artifactId, monitor, root);
        IJavaProject javaProject = JavaCore.create(root.getProject(artifactId));

        List<IClasspathEntry> entries = generateProjectEntries(monitor, project);

        create(project.getFolder(DOCS_FOLDER), monitor);
        create(project.getFolder(ICONS_FOLDER), monitor);
        create(project.getFolder(MAIN_JAVA_FOLDER + "/" + groupId.replaceAll("\\.", "/")), monitor);
        create(project.getFolder(TEST_JAVA_FOLDER + "/" + groupId.replaceAll("\\.", "/")), monitor);

        javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[] {}), monitor);

        TemplateFileWriter templateFileWriter = new TemplateFileWriter(project, monitor);
        templateFileWriter.apply(POM_TEMPLATE_PATH, POM_FILENAME, new MavenParameterReplacer(mavenModel));
        templateFileWriter.apply("/templates/README.tmpl", "README.md", new NullReplacer());
        templateFileWriter.apply("/templates/LICENSE.tmpl", "LICENSE.md", new NullReplacer());

        monitor.worked(1);
    }

    private List<IClasspathEntry> generateProjectEntries(IProgressMonitor monitor, IProject project) throws CoreException {
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
        entries.add(createEntry(project.getFolder(MAIN_JAVA_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(MAIN_RESOURCES_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(TEST_RESOURCES_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(TEST_JAVA_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(MAIN_MULE_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(MAIN_FLOWS_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(DevkitUtils.GENERATED_SOURCES_FOLDER), monitor));
        entries.add(JavaRuntime.getDefaultJREContainerEntry());
        return entries;
    }

    private IProject createProject(String artifactId, IProgressMonitor monitor, IWorkspaceRoot root) throws CoreException {

        IProjectDescription projectDescription = getProjectDescription(root, artifactId);

        IProject project = root.getProject(artifactId);
        if (!project.exists()) {
            project.create(projectDescription, monitor);
            project.open(monitor);
        }
        return project;
    }

    private IProjectDescription getProjectDescription(IWorkspaceRoot root, String artifactId) throws CoreException {
        IProjectDescription desc = root.getWorkspace().newProjectDescription(artifactId);
        desc.setNatureIds(new String[] { JavaCore.NATURE_ID, DevkitNature.NATURE_ID });
        ICommand[] commands = desc.getBuildSpec();

        ICommand[] newCommands = new ICommand[commands.length + 1];
        System.arraycopy(commands, 0, newCommands, 0, commands.length);

        ICommand command = desc.newCommand();
        command.setBuilderName(DevkitBuilder.BUILDER_ID);
        newCommands[newCommands.length - 1] = command;

        desc.setBuildSpec(newCommands);
        return desc;
    }

    protected IClasspathEntry createEntry(final IResource resource, IProgressMonitor monitor) throws CoreException {
        create(resource, monitor);
        return JavaCore.newSourceEntry(resource.getFullPath());
    }

    protected void create(final IResource resource, IProgressMonitor monitor) throws CoreException {
        if (resource == null || resource.exists())
            return;
        if (!resource.getParent().exists())
            create(resource.getParent(), monitor);
        switch (resource.getType()) {
        case IResource.FILE:
            ((IFile) resource).create(new ByteArrayInputStream(new byte[0]), true, monitor);
            break;
        case IResource.FOLDER:
            ((IFolder) resource).create(IResource.NONE, true, monitor);
            break;
        case IResource.PROJECT:
            break;
        }
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
}