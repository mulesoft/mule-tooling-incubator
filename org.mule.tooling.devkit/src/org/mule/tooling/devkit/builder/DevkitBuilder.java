package org.mule.tooling.devkit.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.mule.tooling.devkit.maven.UpdateProjectClasspath;

public class DevkitBuilder extends IncrementalProjectBuilder {

    public static final String BUILDER_ID = "org.mule.tooling.devkit.devkitBuilder";

    void checkXML(IResource resource, IProgressMonitor monitor) {
        if (resource instanceof IFile && resource.getName().equals("pom.xml")) {
            new UpdateProjectClasspath().execute(JavaCore.create(getProject()), monitor);
        }
    }

    protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor) throws CoreException {
        if (kind == FULL_BUILD) {
            fullBuild(monitor);
        } else {
            IResourceDelta delta = getDelta(getProject());
            if (delta == null) {
                fullBuild(monitor);
            } else {
                incrementalBuild(delta, monitor);
            }
        }
        return null;
    }

    protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
        try {
            getProject().accept(new SampleResourceVisitor(monitor));
        } catch (CoreException e) {
        }
    }

    protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
        // the visitor does the work.
        delta.accept(new SampleDeltaVisitor(monitor));
    }

    class SampleDeltaVisitor implements IResourceDeltaVisitor {

        private IProgressMonitor monitor;

        public SampleDeltaVisitor(IProgressMonitor monitor) {
            this.monitor = monitor;
        }

        public boolean visit(IResourceDelta delta) throws CoreException {
            IResource resource = delta.getResource();
            switch (delta.getKind()) {
            case IResourceDelta.ADDED:
                checkXML(resource, monitor);
                break;
            case IResourceDelta.REMOVED:
                // handle removed resource
                break;
            case IResourceDelta.CHANGED:
                // handle changed resource
                checkXML(resource, monitor);
                break;
            }
            // return true to continue visiting children.
            return true;
        }
    }

    class SampleResourceVisitor implements IResourceVisitor {

        private IProgressMonitor monitor;

        public SampleResourceVisitor(IProgressMonitor monitor) {
            this.monitor = monitor;

        }

        public boolean visit(IResource resource) {
            checkXML(resource, monitor);

            return true;
        }
    }
}
