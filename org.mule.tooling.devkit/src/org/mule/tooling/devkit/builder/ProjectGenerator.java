package org.mule.tooling.devkit.builder;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.mule.tooling.devkit.common.DevkitUtils;

public class ProjectGenerator {

    protected ProjectGenerator() {

    }

    public List<IClasspathEntry> generateProjectEntries(IProgressMonitor monitor, IProject project) throws CoreException {
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
        entries.add(createEntry(project.getFolder(DevkitUtils.MAIN_JAVA_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(DevkitUtils.MAIN_RESOURCES_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(DevkitUtils.TEST_RESOURCES_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(DevkitUtils.TEST_JAVA_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(DevkitUtils.GENERATED_SOURCES_FOLDER), monitor));
        entries.add(JavaRuntime.getDefaultJREContainerEntry());
        return entries;
    }

    public IClasspathEntry createEntry(final IResource resource, IProgressMonitor monitor) throws CoreException {
        create(resource, monitor);
        return JavaCore.newSourceEntry(resource.getFullPath());
    }

    public void create(final IResource resource, IProgressMonitor monitor) throws CoreException {
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
}
