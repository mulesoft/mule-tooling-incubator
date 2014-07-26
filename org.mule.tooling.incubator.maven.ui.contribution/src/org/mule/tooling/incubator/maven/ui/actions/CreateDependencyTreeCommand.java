package org.mule.tooling.incubator.maven.ui.actions;

import java.io.File;
import java.io.IOException;
import java.io.PipedOutputStream;

import org.apache.maven.model.Dependency;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.core.utils.VMUtils;
import org.mule.tooling.maven.ui.MavenUIPlugin;
import org.mule.tooling.maven.ui.preferences.MavenPreferences;
import org.mule.tooling.incubator.maven.core.DependencyParser;
import org.mule.tooling.maven.MavenPlugin;
import org.mule.tooling.incubator.maven.core.TreeNode;
import org.mule.tooling.maven.cmdline.MavenCommandLine;
import org.mule.tooling.maven.runner.MavenRunner;
import org.mule.tooling.maven.runner.MavenRunnerBuilder;
import org.mule.tooling.maven.runner.SyncGetResultCallback;

public class CreateDependencyTreeCommand extends AbstractHandler {

    private MavenRunner mavenRunner;

    private IProject project;
    
    public IProject getProject() {
        return project;
    }

    
    public void setProject(IProject project) {
        this.project = project;
    }

    private String otherOptions;
    private TreeNode<Dependency> depResult;

    public TreeNode<Dependency> getDepResult() {
        return depResult;
    }

    public void setDepResult(TreeNode<Dependency> depResult) {
        this.depResult = depResult;
    }

    public CreateDependencyTreeCommand(IProject project) {
        this.project = project;
    }

    public void cancelBuild() {
        mavenRunner.cancelBuild();
    }

    public void run(IFile pomFile, IProgressMonitor monitor) {
        final MavenRunnerBuilder mavenRunnerBuilder = new MavenRunnerBuilder();
        MavenPreferences preferencesAccessor = MavenUIPlugin.getDefault().getPreferences();
        mavenRunnerBuilder.setMavenInstallationHome(preferencesAccessor.getMavenInstallationHome());
        mavenRunnerBuilder.addMavenOpts(preferencesAccessor.getMavenOpts());
        mavenRunnerBuilder.setJavaHome(VMUtils.getDefaultJvmHome(JavaCore.create(project)));
        final PipedOutputStream pipedOutputStream = new PipedOutputStream();

        mavenRunner = mavenRunnerBuilder.build();
        SyncGetResultCallback callback = new SyncGetResultCallback();
        File temp = null;
        try {
            temp = File.createTempFile("dependencies", "txt");
            if (pomFile == null) {
                runCommand(otherOptions, pipedOutputStream, callback);
            } else {
                runCommand(pomFile.getLocation().toFile(), temp, pipedOutputStream, callback);
            }

            while (callback.getResult(100) == SyncGetResultCallback.STILL_NOT_FINISHED) {
                if (monitor.isCanceled()) {
                    this.cancelBuild();
                    break;
                }
            }
            depResult = DependencyParser.parseFile(temp);

        } catch (InterruptedException e) {
            MavenPlugin.logWarning("Maven build interrupted", e);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (monitor != null)
                monitor.done();
        }
    }

    protected void runCommand(File pomFile, File temp, final PipedOutputStream pipedOutputStream, SyncGetResultCallback callback) {
        StringBuilder commandString = new StringBuilder();
        commandString.append(" dependency:tree  -f " + pomFile.getAbsolutePath());
        commandString.append("  -DoutputFile=" + temp.getAbsolutePath());
        mavenRunner.runBare(MavenCommandLine.fromString(commandString.toString()), callback, pipedOutputStream);

    }

    protected void runCommand(String command, final PipedOutputStream pipedOutputStream, SyncGetResultCallback callback) {
        mavenRunner.runBare(MavenCommandLine.fromString(command), callback, pipedOutputStream);
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (project != null) {
            IFile pomFile = project.getFile("pom.xml");
            if (pomFile.exists()) {
                run(pomFile, null);
            }
            return null;
        }
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

        if (selection != null && selection instanceof IStructuredSelection) {
            Object selected = ((IStructuredSelection) selection).getFirstElement();

            if (selected instanceof IJavaElement) {
                final IProject selectedProject = ((IJavaElement) selected).getJavaProject().getProject();
                if (selectedProject != null) {
                    IFile pomFile = selectedProject.getFile("pom.xml");
                    if (pomFile.exists()) {
                        run(pomFile, null);
                    }
                }
            }
        }
        return null;
    }

}
