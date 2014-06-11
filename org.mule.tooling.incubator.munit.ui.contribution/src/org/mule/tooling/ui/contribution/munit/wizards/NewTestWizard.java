package org.mule.tooling.ui.contribution.munit.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.mule.tooling.core.impl.model.MuleProjectImpl;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;
import org.mule.tooling.ui.contribution.munit.MunitResourceUtils;

public class NewTestWizard extends Wizard implements INewWizard {

    private NewTestWizardPage page;
    private ISelection selection;

    public NewTestWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    public void addPages() {
        page = new NewTestWizardPage(selection);
        addPage(page);
    }

    public boolean performFinish() {
        final String containerName = page.getContainerName();
        final String fileName = page.getFileName();
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    doFinish(containerName, fileName, monitor);
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

    private void doFinish(String containerName, String fileName, IProgressMonitor monitor) throws CoreException {
        monitor.beginTask("Creating " + fileName, 2);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        final IResource resource = root.getFile(new Path(containerName));
        if (!resource.exists() || !(resource instanceof IFile)) {
            throwCoreException("The production file \"" + containerName + "\" does not exist.");
        }
        final IFile container = (IFile) resource;

        getShell().getDisplay().syncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    IMuleProject muleProject = new MuleProjectImpl();
                    muleProject.initialize(JavaCore.create(container.getProject()));
                    MunitResourceUtils.configureProjectForMunit(muleProject);
                    IFolder munitFolder = MunitResourceUtils.createMunitFolder(muleProject);
                    IFile munitFile = MunitResourceUtils.createXMLConfigurationFromTemplate(muleProject, page.getFileName(), resource.getName(), munitFolder);
                    MunitResourceUtils.open(munitFile);
                } catch (IOException e1) {

                } catch (CoreException e) {

                }
            }
        });

        monitor.worked(1);
    }

    private void throwCoreException(String message) throws CoreException {
        throw new CoreException(new Status(IStatus.ERROR, MunitPlugin.PLUGIN_ID, IStatus.OK, message, null));
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
}