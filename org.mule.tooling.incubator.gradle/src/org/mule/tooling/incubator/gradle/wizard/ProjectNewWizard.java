package org.mule.tooling.incubator.gradle.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.gradle.tooling.ProjectConnection;
import org.mule.tooling.incubator.gradle.Activator;
import org.mule.tooling.incubator.gradle.GradlePluginConstants;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;
import org.mule.tooling.incubator.gradle.GradleRunner;
import org.mule.tooling.incubator.gradle.model.GradleProject;
import org.mule.tooling.incubator.gradle.template.TemplateFileWriter;
import org.mule.tooling.incubator.gradle.template.VelocityReplacer;

public class ProjectNewWizard extends Wizard implements INewWizard {

    ProjectNewWizardPage page;
    
    public ProjectNewWizard() {
        setNeedsProgressMonitor(true);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addPages() {
        page = new ProjectNewWizardPage();
        this.addPage(page);
    }

    @Override
    public IWizardPage getNextPage(IWizardPage currentPage) {

        return null;
    }

    @Override
    public boolean performFinish() {
        final GradleProject gradleProject = page.getProject();
        final String projectName = page.getProjectName();
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Creating project" + gradleProject.getGroupId(), 2);
                IProject project = null;
                try {
                    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

                    project = root.getProject(projectName);
                    if (!project.exists()) {
                        project.create(monitor);
                        project.open(monitor);
                    }
                    TemplateFileWriter fileWriter = new TemplateFileWriter(project, monitor);
                    fileWriter.apply("/templates/build.gradle.tmpl", GradlePluginConstants.MAIN_BUILD_FILE, new VelocityReplacer(gradleProject));

                    ProjectConnection connection = GradlePluginUtils.buildConnectionForProject(project.getLocation().toFile().getAbsoluteFile()).connect();

                    GradleRunner.run(connection.newBuild().forTasks("initMuleProject", "studio"), monitor);
                    
                    //needs to be refreshed
                    project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                    
                    
                    //programmatically add the src/main/app folder to the build path
                    //this is required until https://github.com/mulesoft-labs/mule-gradle-plugin/issues/13 is fixed
                    //GradlePluginUtils.addSourceFolder(project, monitor, "src/main/app");
                    
                    connection.close();
                
                } catch (RuntimeException e) {
                	deleteFailedProject(project, monitor);
                	Activator.logError("Error while creating project...", e);
                	throw new RuntimeException("Failed to create project", e);
                } catch (CoreException e) {
                    deleteFailedProject(project, monitor);
                	Activator.logError("Error while creating project...", e);
                    throw new RuntimeException("Failed to create project", e);
                }
            }

        };
        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }
    
	private void deleteFailedProject(IProject project, IProgressMonitor monitor) {
		
		try {
			if (project == null) {
				return;
			}
			
			if (project.exists()) {
				project.delete(true, true, monitor);
			}
		} catch (CoreException ex) {
			//TODO - handle exception better.
			ex.printStackTrace();
		}
	}

}
