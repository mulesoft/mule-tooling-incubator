/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tooling.ui.contribution.munit.runner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class ClasspathProvider
{


    public URL[] getClassPath(final IJavaProject javaProject) throws JavaModelException, MalformedURLException
    {
        Collection<URL> urls = new HashSet<URL>();
        addClassPath(javaProject, urls);
        urls.add(getClass().getClassLoader().getResource("/lib/gson.jar"));
        urls.add(getClass().getClassLoader().getResource("/lib/munit-runner.jar"));
        return urls.toArray(new URL[urls.size()]);
    }

    protected void addClassPath(final IJavaProject javaProject, Collection<URL> urls) throws JavaModelException, MalformedURLException
    {        /*
		 * This path includes the name of the project. The files it is being
		 * used to attach to already have that, so we remove it in the variable.
		 */
        final IPath baseLocation = javaProject.getProject().getLocation().removeLastSegments(1);

        final List<IClasspathEntry> resolved = new LinkedList<IClasspathEntry>();

        for (IClasspathEntry entry : javaProject.getRawClasspath())
        {
            if (entry != null)
            {
                resolve(entry, javaProject, resolved);
            }
        }
        for (IClasspathEntry entry : javaProject.getReferencedClasspathEntries())
        {
            if (entry != null)
            {
                resolve(entry, javaProject, resolved);
            }
        }
        for (IClasspathEntry entry : resolved)
        {
            switch (entry.getEntryKind())
            {
                case IClasspathEntry.CPE_LIBRARY:
                {
                    File file = entry.getPath().makeAbsolute().toFile();
                    if (!file.exists())
                    {
                        file = baseLocation.append(entry.getPath()).toFile();
                    }
                    urls.add(toURL(file));
                    break;
                }
                case IClasspathEntry.CPE_SOURCE:
                {
                    IPath outdir = entry.getOutputLocation();
                    if (outdir == null)
                    {
                        outdir = javaProject.getOutputLocation();
                    }
                    File file = baseLocation.append(outdir).makeAbsolute().toFile();
                    urls.add(toURL(file));
                    break;
                }
                case IClasspathEntry.CPE_PROJECT:
                    IProject refProject = ResourcesPlugin.getWorkspace().getRoot().getProject(entry.getPath().lastSegment());
                    addClassPath(JavaCore.create(refProject), urls);
                    break;
            }
        }
        String projectRoot = javaProject.getProject().getLocation().toOSString();
        String projectOutputDir = new File(javaProject.getOutputLocation().toOSString()).getName();
        urls.add(new File(projectRoot, projectOutputDir).toURI().toURL());
    }

    private void resolve(IClasspathEntry entry, IJavaProject project, List<IClasspathEntry> resolved) throws JavaModelException
    {
        if (entry == null)
        {
            return;
        }
        switch (entry.getEntryKind())
        {
            case IClasspathEntry.CPE_CONTAINER:
            {
                IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), project);
			/*
			 * ignore system containers
			 */
                if (IClasspathContainer.K_APPLICATION == container.getKind())
                {
                    for (IClasspathEntry containerEntry : container.getClasspathEntries())
                    {
                        resolve(containerEntry, project, resolved);
                    }
                }
                break;
            }
            case IClasspathEntry.CPE_LIBRARY:
            case IClasspathEntry.CPE_SOURCE:
            {
                resolved.add(entry);
                break;
            }
            case IClasspathEntry.CPE_VARIABLE:
            {
                resolve(JavaCore.getResolvedClasspathEntry(entry), project, resolved);
                break;
            }
            case IClasspathEntry.CPE_PROJECT:
            {
                resolved.add(entry);
                break;
            }
        }
    }

    private URL toURL(File file) throws MalformedURLException
    {
        if (file.isDirectory())
        {
			/*
			 * for URLClassLoader presumes that directory URLs end with "/"
			 */
            String url = file.toURI().toURL().toExternalForm();
            if (!url.endsWith("/"))
            {
                url += "/";
            }
            return new URL(url);
        }
        else
        {
            return file.toURI().toURL();
        }
    }
}