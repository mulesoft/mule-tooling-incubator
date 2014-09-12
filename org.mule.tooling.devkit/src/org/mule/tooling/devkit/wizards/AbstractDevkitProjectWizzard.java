package org.mule.tooling.devkit.wizards;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.wizard.Wizard;
import org.mule.tooling.devkit.common.DevkitUtils;

public abstract class AbstractDevkitProjectWizzard extends Wizard {
    
    protected IProject getProjectWithDescription(String artifactId, IProgressMonitor monitor, IWorkspaceRoot root, IProjectDescription projectDescription) throws CoreException {
        IProject project = root.getProject(artifactId);
        if (!project.exists()) {
            project.create(projectDescription, monitor);
            project.open(monitor);
            project.setDescription(projectDescription, monitor);
        }
        return project;
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

    protected IClasspathEntry createEntry(final IResource resource, IProgressMonitor monitor) throws CoreException {
        create(resource, monitor);
        return JavaCore.newSourceEntry(resource.getFullPath());
    }

    protected String getResourceExampleFileName(String connectorName) {
        return DevkitUtils.TEST_RESOURCES_FOLDER + "/" + connectorName.toLowerCase() + "-config.xml";
    }

    protected String getIcon48FileName(String connectorName) {
        return "icons/" + connectorName.toLowerCase() + "-connector-48x32.png";
    }

    protected String getIcon24FileName(String connectorName) {
        return "icons/" + connectorName.toLowerCase() + "-connector-24x16.png";
    }

    protected String getExampleFileName(String connectorName) {
        return "doc" + "/" + connectorName.toLowerCase() + "-connector.xml.sample";
    }

    protected String buildMainTargetFilePath(String packageName, String className) {
        return DevkitUtils.MAIN_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + className + ".java";
    }

    protected String buildTestTargetFilePath(String packageName, String className) {
        return DevkitUtils.TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + className + "Test.java";
    }
    
    protected String buildQueryTestTargetFilePath(String packageName, String className) {
        return DevkitUtils.TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + className + "QueryTest.java";
    }
    
    protected List<IClasspathEntry> generateProjectEntries(IProgressMonitor monitor, IProject project) throws CoreException {
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
        entries.add(createEntry(project.getFolder(DevkitUtils.MAIN_JAVA_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(DevkitUtils.MAIN_RESOURCES_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(DevkitUtils.TEST_RESOURCES_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(DevkitUtils.TEST_JAVA_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(DevkitUtils.GENERATED_SOURCES_FOLDER), monitor));
        entries.add(JavaRuntime.getDefaultJREContainerEntry());
        return entries;
    }
}
