package org.mule.tooling.devkit.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.maven.MavenPlugin;
import org.mule.tooling.maven.dependency.MavenDependency;
import org.mule.tooling.maven.dependency.MavenDependency.Scope;
import org.mule.tooling.maven.utils.MavenModelHelper;
import org.mule.tooling.maven.utils.MavenUtils;
import org.mule.tooling.maven.utils.PomDOMUtils;
import org.osgi.service.prefs.BackingStoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MavenDevkitProjectDecorator {

    private static final String STUDIO_PROBLEM_MARKER = "org.mule.tooling.maven.studio_studio_problem_marker";

    private static final boolean MAVEN_SUPPORT_ENABLED_DEFAULT = false;
    public static final String PREFERENCE_KEY_MAVEN_SUPPORT_ENABLED = "mavenSupportEnabled";
    private IEclipsePreferences preferenceNode;
    private IJavaProject project;
    private Model pomModel;
    private boolean pomWellFormed;
    private Document pomDocument;
    private Transformer transformer;

    /**
     * Decorates a mule project that is maven based. To check if a project is maven based, call {@link MavenUtils#isMavenBased(IMuleProject)}
     * 
     * @param project
     * @return the decorator or null if the pom is not parseable
     * 
     * @throws IllegalStateException
     *             if the project is not maven based
     */
    public static MavenDevkitProjectDecorator decorate(IJavaProject project) {
        MavenDevkitProjectDecorator decorator = new MavenDevkitProjectDecorator(project);
        if (!decorator.isMavenBased()) {
            throw new IllegalStateException("Project is not maven based");
        }
        decorator.loadPom();
        return decorator;
    }

    private MavenDevkitProjectDecorator(IJavaProject project) {
        this.project = project;

        // initialize transformer for writing the POM back to the file
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 2);
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (TransformerConfigurationException e) {
            // should not happen
            MavenPlugin.logError("Error initializing DOM transformer", e);
        }
    }

    public boolean isMavenBased() {
        return project.getProject().getFile("pom.xml").exists();
    }

    public File getPomFile() {
        checkPomWellFormed();
        return pomModel.getPomFile();
    }

    public String getGroupId() {
        checkPomWellFormed();
        return pomModel.getGroupId();
    }

    public String getArtifactId() {
        checkPomWellFormed();
        return pomModel.getArtifactId();
    }

    public String getVersion() {
        checkPomWellFormed();
        String parentVersion = null;
        String thisVersion = pomModel.getVersion();

        if (pomModel.getParent() != null)
            parentVersion = pomModel.getParent().getVersion();
        return thisVersion != null ? thisVersion : parentVersion;
    }

    public boolean isMavenSupportEnabled() {
        IEclipsePreferences preferenceNode = getPreferenceNode();
        return preferenceNode.getBoolean(PREFERENCE_KEY_MAVEN_SUPPORT_ENABLED, MAVEN_SUPPORT_ENABLED_DEFAULT);
    }

    public void setMavenSupportEnabled(boolean enabled) {
        IEclipsePreferences preferenceNode = getPreferenceNode();
        preferenceNode.putBoolean(PREFERENCE_KEY_MAVEN_SUPPORT_ENABLED, enabled);
        try {
            preferenceNode.flush();
        } catch (BackingStoreException e) {
            MavenPlugin.logError("Problem storing the project's maven support preferences", e);
        }
    }

    private IEclipsePreferences getPreferenceNode() {
        if (preferenceNode == null) {
            ProjectScope projectScope = new ProjectScope(project.getJavaProject().getProject());
            preferenceNode = projectScope.getNode(MavenPlugin.PLUGIN_ID);
        }
        return preferenceNode;
    }

    /**
     * 
     * @param listener
     */
    public void addMavenSupportEnablementChangeListener(IPreferenceChangeListener listener) {
        getPreferenceNode().removePreferenceChangeListener(listener);
        getPreferenceNode().addPreferenceChangeListener(listener);
    }

    public boolean hasDependency(MavenDependency dependency, boolean checkVersion) {
        checkPomWellFormed();
        List<Dependency> existingDependencies = pomModel.getDependencies();
        Dependency tempDependency = MavenUtils.dependency( //
                dependency.getGroupId(), //
                dependency.getArtifactId(), //
                dependency.getVersion());
        boolean found = false;
        for (Dependency existingDependency : existingDependencies) {
            if (areDependenciesEqual(existingDependency, tempDependency, checkVersion)) {
                found = true;
            }
        }
        return found;
    }

    private void writeChanges() {
        try {
            DOMSource source = new DOMSource(pomDocument);
            StreamResult result = new StreamResult(pomModel.getPomFile());
            transformer.transform(source, result);
            reloadPom();
        } catch (TransformerException e) {
            // Not much about this exception in transform() documentation
            MavenPlugin.logError("Error transforming DOM model to pom.xml file", e);
        }
    }

    public void addDependency(MavenDependency dependency) {
        checkPomWellFormed();
        Dependency newDependency = MavenUtils.dependency( //
                dependency.getGroupId(), //
                dependency.getArtifactId(), //
                dependency.getVersion());
        newDependency.setScope(dependency.getScope().asString());

        // check for the dependency ignoring it's version (whatever the user
        // wants to use, is fine)
        if (!this.hasDependency(dependency, false)) {
            doAddDependency(newDependency);
            this.writeChanges();
        }
    }

    private void doAddDependency(Dependency newDependency) {
        this.addDependencyToPom(newDependency);

        MavenModelHelper mavenModelHelper = new MavenModelHelper(pomModel);

        // TODO this is actually wrong, but we should be adding the distrib mgmt repositories of the external module being added
        // if (mavenModelHelper.hasSection(Section.DISTRIBUTION_MANAGEMENT)) {
        // this.addRepository(mavenModelHelper.getDistributionManagementRepository());
        // this.addRepository(mavenModelHelper.getDistributionManagementSnapshotRepository());
        // }

        boolean isNotProvided = !Scope.PROVIDED.asString().equals(newDependency.getScope());
        if (isNotProvided && !mavenModelHelper.hasInclusion(newDependency))
            this.addInclusion(newDependency);
    }

    private void addInclusion(Dependency newDependency) {
        if (new MavenModelHelper(pomModel).hasMavenMulePlugin()) {
            Node mulePluginElement = null;
            Node buildNode = PomDOMUtils.getElement(pomDocument, "build");
            NodeList pluginsList = PomDOMUtils.getChild(buildNode, "plugins").getChildNodes();

            mulePluginElement = PomDOMUtils.findElementMatching(pluginsList, MavenModelHelper.ORG_MULE_TOOLS, MavenModelHelper.MAVEN_MULE_PLUGIN);

            Node configurationNode = PomDOMUtils.getOrCreateChild(mulePluginElement, "configuration");
            Node inclusionsNode = PomDOMUtils.getOrCreateChild(configurationNode, "inclusions");

            Node newInclusion = PomDOMUtils.appendNewChild(inclusionsNode, "inclusion");
            PomDOMUtils.appendNewChild(newInclusion, "groupId", newDependency.getGroupId());
            PomDOMUtils.appendNewChild(newInclusion, "artifactId", newDependency.getArtifactId());
        }
    }

    private void addDependencyToPom(Dependency newDependency) {
        Node dependenciesNode = PomDOMUtils.getOrCreateChild(getProjectNode(), "dependencies");
        Node newDependencyNode = PomDOMUtils.createDomDependency(pomDocument, newDependency);
        dependenciesNode.appendChild(newDependencyNode);
    }

    private Node getProjectNode() {
        Node projectNode = null;
        projectNode = PomDOMUtils.getChild(pomDocument, "project");
        if (projectNode == null)
            MavenPlugin.logError("pom.xml does not have a project element: " + getPomFile().getAbsolutePath());
        return projectNode; // it MUST exist
    }

    private boolean areDependenciesEqual(Dependency existingDependency, Dependency dependency, boolean checkVersion) {
        boolean groupIdEquals = existingDependency.getGroupId().equals(dependency.getGroupId());
        boolean artifactIdEquals = existingDependency.getArtifactId().equals(dependency.getArtifactId());
        boolean versionEquals = existingDependency.getVersion().equals(dependency.getVersion());
        return groupIdEquals && artifactIdEquals && (versionEquals || !checkVersion);
    }

    private void reloadPom() {
        readPomModel();
    }

    private void loadPom() {
        readPomModel();
        readPomDocument();
    }

    private void readPomDocument() {
        try {
            // initialize DOM Document for pom
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            dBuilder = dbFactory.newDocumentBuilder();
            pomDocument = dBuilder.parse(pomModel.getPomFile());
        } catch (ParserConfigurationException e) {
            // should not happen, we are not configuring anything strange to the factory
            MuleCorePlugin.getLog().log(new Status(Status.ERROR, MuleCorePlugin.PLUGIN_ID, "Unable initialize pom DOM document", e));
        } catch (SAXException e) {
            // the pom should be parseable at this point (dBuilder.parse())
            MuleCorePlugin.getLog().log(new Status(Status.ERROR, MuleCorePlugin.PLUGIN_ID, "Unable initialize pom DOM document", e));
        } catch (IOException e) {
            MuleCorePlugin.getLog().log(new Status(Status.ERROR, MuleCorePlugin.PLUGIN_ID, "Unable to fetch the pom.xml contents", e));
        }
    }

    private void readPomModel() {
        pomWellFormed = false;
        try {
            // create the pom model from the IFile
            IFile pomIFile = project.getProject().getFile("pom.xml");
            InputStream pomFileInputStream = pomIFile.getContents(true);
            pomModel = new MavenXpp3Reader().read(pomFileInputStream);

            pomWellFormed = true;

            // since the pom model does not automatically reference it's file,
            // find it and set it
            File pomFile = pomIFile.getLocation().toFile();
            pomModel.setPomFile(pomFile);
        } catch (CoreException e) {
            MuleCorePlugin.getLog().log(new Status(Status.ERROR, MuleCorePlugin.PLUGIN_ID, "Unable to fetch the pom.xml contents", e));
        } catch (IOException e) {
            MuleCorePlugin.getLog().log(new Status(Status.ERROR, MuleCorePlugin.PLUGIN_ID, "Unable to fetch the pom.xml contents", e));
        } catch (XmlPullParserException e) {
            pomWellFormed = false; // not really necessary, but well, we ARE doing something with this
        }
    }

    public boolean isPomWellFormed() {
        return pomWellFormed;
    }

    private void checkPomWellFormed() {
        if (!this.isPomWellFormed())
            throw new IllegalStateException("Trying to execute an operation over a project which pom file is not well formed");
    }

    public void setStudioGoalProblemMarker(String message) throws CoreException {
        IMarker[] existingMarkers = getExistingStudioErrorMarkers();
        if (existingMarkers.length == 0) {
            // only do this if there was NOT an error marker, this will avoid some unnecessary checking code
            IProject project = this.project.getProject();
            IMarker marker = project.createMarker(STUDIO_PROBLEM_MARKER);

            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
        }

    }

    private IMarker[] getExistingStudioErrorMarkers() throws CoreException {
        IMarker[] existingMarkers = project.getProject().findMarkers(STUDIO_PROBLEM_MARKER, false, IResource.DEPTH_ONE);
        return existingMarkers;
    }

    public void clearStudioGoalProblemMarker() throws CoreException {
        IMarker[] errorMarkers = getExistingStudioErrorMarkers();
        for (IMarker iMarker : errorMarkers) {
            iMarker.delete();
        }
    }

    public IFolder getFolder(String folderPath) {
        return project.getProject().getFolder(folderPath);
    }

    public IJavaProject getProject() {
        return project;
    }
}
