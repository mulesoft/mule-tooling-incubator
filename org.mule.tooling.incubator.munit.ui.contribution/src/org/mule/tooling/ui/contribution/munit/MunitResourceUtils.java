package org.mule.tooling.ui.contribution.munit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;
import org.mule.tooling.core.classloader.ProjectClasspathUtils;
import org.mule.tooling.core.io.MuleResourceUtils;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.maven.MavenMuleProjectDecorator;
import org.mule.tooling.maven.dependency.MavenDependency;
import org.mule.tooling.maven.dependency.PojoMavenDependency;
import org.mule.tooling.maven.utils.MavenUtils;
import org.mule.tooling.maven.utils.XmlEditingHelper;
import org.mule.tooling.maven.utils.XmlEditionCallable;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.model.messageflow.util.MuleConfigurationNamingSupport;
import org.mule.tooling.ui.contribution.munit.classpath.MunitClassPathContainer;
import org.mule.tooling.ui.contribution.munit.runtime.MunitLibrary;
import org.mule.tooling.ui.contribution.munit.runtime.MunitRuntime;
import org.mule.tooling.ui.contribution.munit.runtime.MunitRuntimeExtension;
import org.mule.tooling.ui.modules.core.dsl.AbstractPipelineBuilder;
import org.mule.tooling.ui.modules.core.dsl.ContainerBuilder;
import org.mule.tooling.ui.modules.core.dsl.MuleConfigurationBuilder;

/**
 * <p>
 * Utility class to handle Munit resources. Use this class to manipulate files, find resources or update class path
 * </p>
 */
public class MunitResourceUtils {

    /**
     * <p>
     * Creates the munit folder in the {@link IMuleProject}. If the folder already exists then it does nothing and adds it to the path if necessary
     * </p>
     * 
     * @param muleProject
     *            The {@link IMuleProject} where we want to create the folder
     * @return The Munit folder
     * @throws CoreException
     *             If the folder could not be created
     */
    public static IFolder createMunitFolder(IMuleProject muleProject) throws CoreException {
        IFolder munitFolder = muleProject.getFolder(MunitPlugin.MUNIT_FOLDER_PATH);
        if (!munitFolder.exists()) {
            munitFolder.create(true, true, new NullProgressMonitor());
            ProjectClasspathUtils.ensureFolderIsASourceFolder(muleProject, munitFolder);
            return munitFolder;
        }
        return munitFolder;
    }

    /**
     * Create the Munit test file from the xml configuration file.
     * 
     * @param testFolder
     *            The munit folder to where the new file is going to be created
     * @param xmlConfigFile
     *            The xml Configuration File
     * @return The new test file
     */
    public static IFile createMunitFileFromXmlConfigFile(IFolder testFolder, IFile xmlConfigFile) {
        String baseName = getBaseName(xmlConfigFile);
        return testFolder.getFile(baseName + "-test.xml");
    }

    /**
     * Returns the base name of the specified file. The name of the file without the extension
     * 
     * @param xmlConfigFile
     *            The xml configuration file.
     * @return The base name.
     */
    public static String getBaseName(IFile xmlConfigFile) {
        final String name = xmlConfigFile.getName();
        final int extensionLength = xmlConfigFile.getFileExtension().length() + 1;// 1 for the .
        return name.substring(0, name.length() - extensionLength);
    }

    /**
     * <p>
     * Creates a new test as a template for user
     * </p>
     * 
     * @param flowEditor
     *            The Flow editor where the test has to be shown
     * @param flowName
     *            The flow name that is being tested
     * @throws CoreException
     *             In case the test could not be created
     */
    public static void createDefaultMunitTest(MessageFlowEditor flowEditor, String flowName) throws CoreException {

        final MuleConfigurationNamingSupport naming = new MuleConfigurationNamingSupport(flowEditor.getMuleConfiguration());
        final MuleConfigurationBuilder muleConfigurationBuilder = new MuleConfigurationBuilder(flowEditor.getMuleConfigurationDecorator());

        // Add Munit Test
        final ContainerBuilder<MuleConfigurationBuilder> munitElement = muleConfigurationBuilder.addContainer("munit_test", "http://www.mulesoft.org/schema/mule/munit/test");
        munitElement.usingProperties().property("description", "Test").property("name", naming.getAvailableName(flowName + "Test")).endProperties();

        // Add Flow ref
        final AbstractPipelineBuilder<ContainerBuilder<MuleConfigurationBuilder>> addFlowRef = munitElement.editNested(1).addFlowRef("Flow-ref to " + flowName);
        addFlowRef.usingProperties().property("name", flowName).endProperties();

        MuleResourceUtils.updateXmlFileFromMuleConfig(flowEditor.getMuleProject(), flowEditor.getMuleConfiguration());
    }

    /**
     * <p>
     * Sets the classpath for a project with Munit, if the project is maven based then it configures the pom
     * </p>
     * 
     * @param muleProject
     *            The project to be configured
     */
    public static void configureProjectForMunit(IMuleProject muleProject) {
        if (MavenUtils.isMavenBased(muleProject)) {
            configureMavenProjectForMunit(muleProject);
        } else {
            configureNormalProjectForMunit(muleProject);
        }
    }

    private static void configureMavenProjectForMunit(IMuleProject muleProject) {
        MavenMuleProjectDecorator mavenProject = MavenMuleProjectDecorator.decorate(muleProject);

        String groupId = "org.mule.modules";
        String artifactId = "mule-interceptor-module";
        String version = "";
        MavenDependency dependency = new PojoMavenDependency(groupId, artifactId, version);

        if (!mavenProject.hasDependency(dependency, false)) {
            MunitResourceUtils.configureMaven(muleProject);
        }
    }

    private static void configureNormalProjectForMunit(IMuleProject muleProject) {
        try {
            if (!(ProjectClasspathUtils.isClasspathEntryInProject(MunitClassPathContainer.CONTAINER_ID, muleProject.getJavaProject()))) {
                MunitResourceUtils.configureProjectClasspath(muleProject);
            }
        } catch (JavaModelException e) {
            MunitPlugin.log(e);
        }
    }

    /**
     * <p>
     * Add the Munit maven configuration to the mule pom
     * </p>
     * 
     * @param muleProject
     *            The project that has to be configured
     */
    public static void configureMaven(IMuleProject muleProject) {
        MavenMuleProjectDecorator mavenProject = MavenMuleProjectDecorator.decorate(muleProject);
        MunitRuntime munitRuntime = MunitRuntimeExtension.getInstance().getMunitRuntimeFor(muleProject);
        if (munitRuntime != null) {
            for (MunitLibrary library : munitRuntime.getLibraries()) {
                if (library.hasMavenSupport()) {
                    mavenProject.addDependency(library.getMavenConfiguration().asPojoDependency());
                }
            }
        } else {
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "No Munit Runtime", "No Munit runtime found for the current mule runtime");
            return;
        }

        mavenProject.addTestResource(MunitPlugin.MUNIT_FOLDER_PATH);
        mavenProject.addPlugin("org.mule.munit.tools", "munit-maven-plugin", munitRuntime.getMunitVersion(), new XmlEditionCallable() {

            @Override
            public boolean editXml(XmlEditingHelper xmlEditorHelper) {
                if (!xmlEditorHelper.hasChild("executions")) {
                    xmlEditorHelper.createElement("executions").createElement("execution").createElement("id").setValue("test").createElement("phase").setValue("test")
                            .createElement("goals").createElement("goal").setValue("test");

                }
                return true;
            }
        });
    }

    /**
     * <p>
     * Adds the {@link MunitClassPathContainer} to the class path
     * </p>
     * 
     * @param muleProject
     *            The project to be configured
     */
    public static void configureProjectClasspath(IMuleProject muleProject) {
        try {
            // Todo shouldn't we check first if the classpath was already set?
            IClasspathEntry[] rawClasspath = muleProject.getJavaProject().getRawClasspath();
            ArrayList<IClasspathEntry> newClassPath = new ArrayList<IClasspathEntry>(Arrays.asList(rawClasspath));
            newClassPath.add(JavaCore.newContainerEntry(MunitClassPathContainer.CONTAINER_ID, true));
            IClasspathEntry[] newClasspathEntries = (IClasspathEntry[]) newClassPath.toArray(new IClasspathEntry[0]);
            muleProject.getJavaProject().setRawClasspath(newClasspathEntries, null);
        } catch (JavaModelException e) {
            MunitPlugin.log(e);
        }
    }

    /**
     * <p>
     * Opens de Munit editor for a particular file
     * </p>
     */
    public static IEditorPart open(IFile munitFile) {
        try {
            IWorkbenchPage page = MunitPlugin.getActivePage();
            if (page != null) {
                return IDE.openEditor(page, munitFile, MunitPlugin.EDITOR_ID, true);
            }
        } catch (PartInitException e) {
        }
        return null;
    }

    /**
     * <p>
     * Shows the Munit runner view part
     * </p>
     */
    public static void openMunitRunner() {
        try {
            IWorkbenchPage activePage = MunitPlugin.getActivePage();
            if (activePage != null) {
                activePage.showView(MunitPlugin.RUNNER_ID).setFocus();
            }
        } catch (WorkbenchException e1) {
        }
    }

    /**
     * <p>
     * Creates the Munit first test template file
     * </p>
     */
    public static IFile createXMLConfigurationFromTemplate(IMuleProject muleProject, String testFileName, String productionFileName, IFolder outputFolder) throws IOException,
            CoreException {
        IFile muleConfigFile = null;
        InputStream inputStream = MunitResourceUtils.class.getClassLoader().getResourceAsStream("templates/munit_config_template.xml");
        StringWriter writer = new StringWriter();
        Reader reader = new InputStreamReader(inputStream, "UTF-8");
        try {

            Velocity.init();
            VelocityContext context = new VelocityContext();

            final boolean isEnterprise = muleProject.getServerDefinition().isEnterpriseRuntime();
            String version = (isEnterprise ? "EE" : "CE") + "-" + muleProject.getServerDefinition().getVersion();

            context.put("version", version);
            context.put("resource", productionFileName);

            Velocity.evaluate(context, writer, "velocity  rendering", reader);
            ByteArrayInputStream source = new ByteArrayInputStream(writer.getBuffer().toString().getBytes());

            muleConfigFile = outputFolder.getFile(testFileName);
            if (muleConfigFile.exists()) {
                muleConfigFile.setContents(source, IFile.KEEP_HISTORY, new NullProgressMonitor());

            } else {
                muleConfigFile.create(source, true, null);
            }
            source.close();

        } catch (Exception e) {
            // NOTHING
        } finally {
            reader.close();
            inputStream.close();
            writer.close();
        }
        return muleConfigFile;
    }
}
