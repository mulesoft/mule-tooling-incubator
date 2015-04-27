package org.mule.tooling.devkit.builder;

import static org.mule.tooling.devkit.common.DevkitUtils.DEMO_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.DOCS_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.ICONS_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_JAVA_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.TEST_JAVA_FOLDER;

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
import org.mule.tooling.devkit.common.ConnectorMavenModel;
import org.mule.tooling.devkit.common.DevkitUtils;

public class ProjectGenerator {

    ConnectorMavenModel mavenModel;

    protected ProjectGenerator(ConnectorMavenModel mavenModel) {
        this.mavenModel = mavenModel;
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

    public void createProjectFolders(IProject project, IProgressMonitor monitor) throws CoreException {
        create(project.getFolder(DOCS_FOLDER), monitor);
        create(project.getFolder(ICONS_FOLDER), monitor);
        create(project.getFolder(DEMO_FOLDER), monitor);
        create(project.getFolder(MAIN_JAVA_FOLDER + "/" + mavenModel.getPackage().replaceAll("\\.", "/")), monitor);
        create(project.getFolder(TEST_JAVA_FOLDER + "/" + mavenModel.getPackage().replaceAll("\\.", "/")), monitor);

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
