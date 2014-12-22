package org.mule.tooling.devkit.maven;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ScanProject extends WorkspaceJob {

    File projectRoot;
    MavenInfo mavenRoot;
    SAXParser parser;

    public ScanProject(String name, String projectRoot, MavenInfo mavenRoot) {
        super(name);
        this.projectRoot = new File(projectRoot);
        this.mavenRoot = mavenRoot;
        this.mavenRoot.setProjectRoot(this.projectRoot);

    }

    class XMLHandler extends DefaultHandler {

        boolean skip = false;
        boolean build = false;
        boolean modules = false;
        boolean parent = false;

        StringBuffer accumulator = new StringBuffer();

        MavenInfo project;

        public void startDocument() {
            project = new MavenInfo();
            project.setProjectRoot(projectRoot);
        }

        public void characters(char[] buffer, int start, int length) {
            if (skip)
                return;
            accumulator.append(buffer, start, length);
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            accumulator.setLength(0);
            if (qName.equals("dependencies")) {
                skip = true;
            }
            if (qName.equals("build")) {
                build = true;
            }
            if (qName.equals("scm")) {
                skip = true;
            }
            if (qName.equals("parent")) {
                parent = true;
            }
            if (qName.equals("modules")) {
                modules = true;
            }
        }

        public void endElement(String uri, String localName, String qName) {

            if (qName.equals("modules")) {
                modules = false;
            }
            if (qName.equals("dependencies")) {
                skip = false;
                return;
            }
            if (qName.equals("build")) {
                build = false;
                return;
            }
            if (qName.equals("scm")) {
                skip = false;
                return;
            }

            if (skip || build)
                return;

            String value = accumulator.toString().trim();
            if (modules) {
                File file = new File(projectRoot, value);
                ScanProject innerJob = new ScanProject("InnerJob", file.getAbsolutePath(), project);
                try {
                    innerJob.runInWorkspace(null);
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
            if (qName.equals("parent")) {
                parent = false;
            }
            if (parent) {
                if ("groupId".equals(qName)) {
                    project.setGroupId(value);
                }
                return;
            }
            if (qName.equals("module")) {
                return;
            }
            if ("groupId".equals(qName)) {
                project.setGroupId(value);
            } else if ("artifactId".equals(qName)) {
                project.setArtifactId(value);
            } else if ("version".equals(qName)) {
                project.setVersion(value);
            } else if ("packaging".equals(qName)) {
                project.setPackaging(value);
            }

        }

        public void endDocument() {
            mavenRoot.addModule(project);
        }

    }

    @Override
    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
        try {
            parser = SAXParserFactory.newInstance().newSAXParser();
            File file = new File(projectRoot, "pom.xml");
            if (!file.exists())
                return Status.OK_STATUS;
            XMLHandler handler = new XMLHandler();
            try {
                parser.parse(file, handler);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return Status.OK_STATUS;
    }

}
