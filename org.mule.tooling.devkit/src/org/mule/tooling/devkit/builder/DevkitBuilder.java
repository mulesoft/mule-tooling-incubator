package org.mule.tooling.devkit.builder;

import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.maven.UpdateProjectClasspath;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class DevkitBuilder extends IncrementalProjectBuilder {

    public static final String BUILDER_ID = "org.mule.tooling.devkit.devkitBuilder";
    private static final String MARKER_TYPE = "org.mule.tooling.devkit.xmlProblem";

    private SAXParserFactory parserFactory;

    void checkXML(IResource resource, IProgressMonitor monitor) {
        if (resource instanceof IFile && resource.getName().equals("pom.xml")) {
            IFile file = (IFile) resource;
            deleteMarkers(file);
            XMLErrorHandler reporter = new XMLErrorHandler(file);
            try {
                getParser().parse(file.getContents(), reporter);
                new UpdateProjectClasspath().execute(JavaCore.create(getProject()), monitor);
            } catch (org.xml.sax.SAXParseException sax) {
                // Ignore since we are already logging the error on the file
            } catch (Exception e) {
                DevkitUIPlugin.log(e);
            }
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

        deleteMuleTargetFolder(monitor);
        
        try {
            getProject().accept(new SampleResourceVisitor(monitor));
        } catch (CoreException e) {
            DevkitUIPlugin.log(e);
        }
    }

    private void deleteMuleTargetFolder(final IProgressMonitor monitor) throws CoreException {
        final IFolder muleFolder = getProject().getFolder("target/generated-sources/mule");

        if (muleFolder.exists()) {
            IResource[] resources = muleFolder.members();
            for (int index = 0; index < resources.length; index++) {
                try {
                    resources[index].delete(true, monitor);
                } catch (CoreException ex) {
                    // Ignore errors
                }
            }
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

    private void addMarker(IFile file, String message, int lineNumber, int severity) {
        try {
            IMarker marker = file.createMarker(MARKER_TYPE);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.SEVERITY, severity);
            if (lineNumber == -1) {
                lineNumber = 1;
            }
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
        } catch (CoreException e) {
        }
    }

    private void deleteMarkers(IFile file) {
        try {
            file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
        } catch (CoreException e) {
            DevkitUIPlugin.log(e);
        }
    }

    class XMLErrorHandler extends DefaultHandler {

        private IFile file;

        public XMLErrorHandler(IFile file) {
            this.file = file;
        }

        private void addMarker(SAXParseException e, int severity) {
            DevkitBuilder.this.addMarker(file, e.getMessage(), e.getLineNumber(), severity);
        }

        public void error(SAXParseException exception) throws SAXException {
            addMarker(exception, IMarker.SEVERITY_ERROR);
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            addMarker(exception, IMarker.SEVERITY_ERROR);
        }

        public void warning(SAXParseException exception) throws SAXException {
            addMarker(exception, IMarker.SEVERITY_WARNING);
        }
    }

    private SAXParser getParser() throws ParserConfigurationException, SAXException {
        if (parserFactory == null) {
            parserFactory = SAXParserFactory.newInstance();
        }
        return parserFactory.newSAXParser();
    }

    protected void clean(IProgressMonitor monitor) throws CoreException {
        // delete markers set and files created
        getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
    }
}
