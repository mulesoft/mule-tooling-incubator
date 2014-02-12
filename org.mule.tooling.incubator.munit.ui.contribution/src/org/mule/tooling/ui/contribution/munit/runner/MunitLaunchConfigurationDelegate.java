package org.mule.tooling.ui.contribution.munit.runner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.mule.tooling.ui.contribution.debugger.service.MuleDebuggerService;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;
import org.mule.tooling.ui.contribution.munit.coverage.MunitCoverageUpdater;


public class MunitLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate
{


    public synchronized void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException
    {
        if (monitor == null){
            monitor = new NullProgressMonitor();
        }
        
        monitor.beginTask(MessageFormat.format("{0}...", new String[] {configuration.getName()}), 5);

        if (monitor.isCanceled()) return;

        try
        {
            try
            {
                preLaunchCheck(configuration, launch, new SubProgressMonitor(monitor, 2));
            }
            catch (CoreException e)
            {
                if (e.getStatus().getSeverity() == IStatus.CANCEL)
                {
                    monitor.setCanceled(true);
                    return;
                }
                throw e;
            }
            if (monitor.isCanceled())
            {
                return;
            }


            IProject project = getJavaProject(configuration).getProject();
            project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IJavaProject javaProject = getJavaProject(configuration);
            IPath munitOutputFolder = null;
            IClasspathEntry[] entries = javaProject.getResolvedClasspath(true);
            for (int i = 0; i < entries.length; i++)
            {
                IClasspathEntry entry = entries[i];
                if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE)
                {
                    IPath path = entry.getPath();
                    IFolder sourceFolder = root.getFolder(path);
                    if (sourceFolder.getLocation().toString().contains(MunitPlugin.MUNIT_FOLDER_PATH))
                    {
                        munitOutputFolder = entry.getOutputLocation();
                    }
                }
            }


            MunitEclipseUpdater.launch();
            MunitCoverageUpdater.launch();
            
            IVMRunner runner = getVMRunner(configuration, mode);

            File workingDir = verifyWorkingDirectory(configuration);
            String workingDirName = null;
            if (workingDir != null)
            {
                workingDirName = workingDir.getAbsolutePath();
            }

            String[] envp = getEnvironment(configuration);

            List<String> vmArguments = new ArrayList<String>();
            ArrayList<String> programArguments = new ArrayList<String>();
            programArguments.add("-resource");
            programArguments.add(configuration.getAttribute(MunitLaunchConfigurationConstants.TEST_RESOURCE, ""));
            programArguments.add("-path");
            programArguments.add(configuration.getAttribute(MunitLaunchConfigurationConstants.MUNIT_TEST_PATH, ""));
            programArguments.add("-port");
            programArguments.add(String.valueOf(MunitEclipseUpdater.getInstance().getPort()));

            Map<String, Object> vmAttributesMap = getVMSpecificAttributesMap(configuration);

            for (int i = 0; i < entries.length; i++)
            {
                IClasspathEntry entry = entries[i];
                if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE)
                {
                    IPath path = entry.getPath();
                    IFolder sourceFolder = root.getFolder(path);
                    if (!sourceFolder.getLocation().toString().contains("test/munit"))
                    {
                        try
                        {
                            IFolder folder = root.getFolder(entry.getOutputLocation());
                            for (IResource resource : folder.members())
                            {
                                try
                                {
                                    resource.copy(munitOutputFolder, IFolder.SHALLOW, monitor);
                                }
                                catch (Throwable e)
                                {

                                }
                            }

                        }
                        catch (Throwable y)
                        {

                        }

                    }

                }
            }
            String[] classpath = getClasspath(configuration);
            // ClasspathgetC
            List<String> classPathAsList = new ArrayList<String>(Arrays.asList(classpath));
            try
            {
                URL[] urlClasspath = new ClasspathProvider().getClassPath(getJavaProject(configuration));
                for (URL url : urlClasspath)
                {
                    if  ( url != null ){
							if ( "file".equals(url.getProtocol())){
							classPathAsList.add(url.getFile());
							}
						
                    }
                }
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            
  
            
            if ( "debug".equals(launch.getLaunchMode())){
                vmArguments.add("-Dmule.debug.enable=true");    
                vmArguments.add("-Dmule.debug.suspend=true");    
                vmArguments.add("-Dmunit.test.app.name=" + project.getName());    
                launch.setAttribute("mule-launch", "true");
                MuleDebuggerService.getDefault().connect(launch);
            }
//            else{
                vmArguments.add("-Dcobertura.port="+MunitCoverageUpdater.getInstance().getPort());
//            }

            // Create VM config
            VMRunnerConfiguration runConfig = new VMRunnerConfiguration("org.mule.munit.runner.remote.MunitRemoteRunner", classPathAsList.toArray(new String[] {}));
            runConfig.setVMArguments((String[]) vmArguments.toArray(new String[vmArguments.size()]));
            runConfig.setProgramArguments((String[]) programArguments.toArray(new String[programArguments.size()]));
            runConfig.setEnvironment(envp);
            runConfig.setWorkingDirectory(workingDirName);
            runConfig.setVMSpecificAttributesMap(vmAttributesMap);

            // Bootpath
            runConfig.setBootClassPath(getBootpath(configuration));


            // check for cancellation
            if (monitor.isCanceled())
            {
                return;
            }

            // done the verification phase
            monitor.worked(1);

            setDefaultSourceLocator(launch, configuration);
            monitor.worked(1);

            runner.run(runConfig, launch, monitor);


            if (monitor.isCanceled())
            {
                return;
            }
        }
        finally
        {
            monitor.done();
        }
    }


    protected void preLaunchCheck(ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor) throws CoreException
    {
        try{
            IJavaProject javaProject = getJavaProject(configuration);
            if ((javaProject == null) || !javaProject.exists())
            {
                abort("Invalid project", null, IJavaLaunchConfigurationConstants.ERR_NOT_A_JAVA_PROJECT);
            }
        }
        finally
        {
            monitor.done();
        }
    }

    public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException
    {
        return "org.eclipse.jdt.internal.junit.runner.RemoteTestRunner"; 
    }

    
    protected void abort(String message, Throwable exception, int code) throws CoreException
    {
        throw new CoreException(new Status(IStatus.ERROR, MunitPlugin.getPluginId(), code, message, exception));
    }
}

