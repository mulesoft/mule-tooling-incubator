package org.mule.tooling.devkit.wizards;

import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_JAVA_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.POM_FILENAME;
import static org.mule.tooling.devkit.common.DevkitUtils.POM_TEMPLATE_PATH;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.mule.tooling.devkit.DevkitImages;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.builder.DevkitBuilder;
import org.mule.tooling.devkit.builder.DevkitNature;
import org.mule.tooling.devkit.builder.ProjectGenerator;
import org.mule.tooling.devkit.builder.ProjectGeneratorFactory;
import org.mule.tooling.devkit.builder.ProjectSubsetBuildAction;
import org.mule.tooling.devkit.common.ApiType;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.common.ConnectorMavenModel;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.maven.MavenRunBuilder;
import org.mule.tooling.devkit.maven.UpdateProjectClasspathWorkspaceJob;
import org.mule.tooling.devkit.template.ImageWriter;
import org.mule.tooling.devkit.template.TemplateFileWriter;
import org.mule.tooling.devkit.template.replacer.ClassReplacer;
import org.mule.tooling.devkit.template.replacer.NullReplacer;

public class NewDevkitProjectWizard extends AbstractDevkitProjectWizzard implements INewWizard {

    // TODO Re factor this, to many "if"s
    private static final String MAIN_TEMPLATE_PATH = "/templates/connector_main.tmpl";
    private static final String MAIN_NONE_ABSTRACT_TEMPLATE_PATH = "/templates/connector_none_abstract_main.tmpl";
    private static final String TEST_TEMPLATE_PATH = "/templates/connector_test.tmpl";
    private static final String TEST_QUERY_TEMPLATE_PATH = "/templates/connector-query-test.tmpl";
    private static final String TEST_DATASENSE_TEMPLATE_PATH = "/templates/connector-test-datasense.tmpl";
    private static final String TEST_RESOURCE_PATH = "/templates/connector-test-resource.tmpl";
    private static final String LOG4J_PATH = "/templates/devkit-log4j2.xml.tmpl";
    public static final String WIZZARD_PAGE_TITTLE = "Create an Anypoint Connector";
    private NewDevkitProjectWizardPage page;
    private NewDevkitProjectWizardPageAdvance advancePage;
    private ConnectorMavenModel connectorModel;
    private boolean wasCreated;

    public NewDevkitProjectWizard() {
        super();
        this.setWindowTitle("New Anypoint Connector Project");
        setNeedsProgressMonitor(true);
        this.setDefaultPageImageDescriptor(DevkitImages.getManaged("", "mulesoft-logo.png"));
        connectorModel = new ConnectorMavenModel();
    }

    @Override
    public void addPages() {
        page = new NewDevkitProjectWizardPage(connectorModel);
        advancePage = new NewDevkitProjectWizardPageAdvance(connectorModel);
        addPage(page);
        addPage(advancePage);
    }

    @Override
    public IWizardPage getNextPage(IWizardPage currentPage) {
        if (currentPage instanceof NewDevkitProjectWizardPage) {
            advancePage.refresh();
            return advancePage;
        }
        return null;
    }

    @Override
    public boolean performFinish() {
        wasCreated = false;
        final ConnectorMavenModel mavenModel = getPopulatedModel();
        if (mavenModel.getApiType().equals(ApiType.SOAP)) {
            if (!isValidWsdl(getWsdlPath())) {
                return false;
            }
        }
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {

                    final IJavaProject javaProject = doFinish(mavenModel, monitor);
                    downloadJavadocForAnnotations(javaProject, monitor);
                    if (mavenModel.getApiType().equals(ApiType.SOAP)) {
                        MavenRunBuilder.newMavenRunBuilder().withProject(javaProject).withArg("clean").withArg("compile").withArg("-Pconnector-generator")
                                .withTaskName("Generating connector from WSDL...").build().run(monitor);

                        MavenRunBuilder.newMavenRunBuilder().withProject(javaProject).withArg("license:format").withTaskName("Adding headers...").build().run(monitor);

                        javaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
                    }

                    boolean autoBuilding = ResourcesPlugin.getWorkspace().isAutoBuilding();
                    UpdateProjectClasspathWorkspaceJob job = new UpdateProjectClasspathWorkspaceJob(javaProject);
                    monitor.subTask("Downloading dependencies");
                    job.runInWorkspace(monitor);
                    if (!autoBuilding) {

                        ProjectSubsetBuildAction projectBuild = new ProjectSubsetBuildAction(new IShellProvider() {

                            @Override
                            public Shell getShell() {
                                return page.getShell();
                            }
                        }, IncrementalProjectBuilder.CLEAN_BUILD, new IProject[] { javaProject.getProject() });
                        projectBuild.run();

                    }
                    openConnectorClass(mavenModel, javaProject.getProject());
                    javaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
                    wasCreated = true;
                } catch (CoreException e) {

                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
        if (!runInContainer(op)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean performCancel() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        if (wasCreated && StringUtils.isEmpty(page.getProjectName())) {
            IProject project = root.getProject(page.getProjectName());
            if (project != null) {
                try {
                    project.delete(true, new NullProgressMonitor());
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private boolean isValidWsdl(final String wsdlPath) {
        final IRunnableWithProgress parseWsdl = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                if (!canParseWSDL(monitor, wsdlPath)) {
                    throw new IllegalArgumentException("Unable to process the provided WSDL. Please, verify that the wsdl exists and it's well formed.");
                }
            }
        };
        return runInContainer(parseWsdl);
    }

    /**
     * Runs a work inside the container so that the logs are shown in the UI wizard
     * 
     * @param work
     *            Work with progress
     * @return true is the result was successfully
     */
    private boolean runInContainer(final IRunnableWithProgress work) {
        try {
            getContainer().run(true, true, work);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }

        return true;
    }

    private AuthenticationType getAuthenticationType() {
        return page.getAuthenticationType();
    }

    /**
     * The worker method. It will find the container, create the file if missing or just replace its contents, and open the editor on the newly created file.
     * 
     * @param mavenModel
     * @param monitor
     * @return
     * @throws CoreException
     */
    private IJavaProject doFinish(ConnectorMavenModel mavenModel, IProgressMonitor monitor) throws CoreException {

        String wsdlFileName = "Dummy";

        monitor.beginTask("Creating project" + mavenModel.getProjectName(), 20);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        IPath path = StringUtils.isBlank(mavenModel.getProjectLocation()) ? null : Path.fromOSString(mavenModel.getProjectLocation());
        IProject project = createProject(mavenModel.getProjectName(), path, monitor, root);
        IJavaProject javaProject = JavaCore.create(root.getProject(mavenModel.getProjectName()));

        ProjectGenerator generator = ProjectGeneratorFactory.newInstance(mavenModel);

        NullProgressMonitor nullMonitor = new NullProgressMonitor();

        List<IClasspathEntry> entries = generator.generateProjectEntries(nullMonitor, project);

        if (mavenModel.getApiType().equals(ApiType.SOAP)) {
            entries.add(generator.createEntry(project.getFolder(DevkitUtils.CXF_GENERATED_SOURCES_FOLDER), nullMonitor));
        }

        generator.createProjectFolders(project, nullMonitor);

        javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[] {}), nullMonitor);

        ClassReplacer classReplacer = new ClassReplacer(mavenModel);

        TemplateFileWriter templateFileWriter = new TemplateFileWriter(project, nullMonitor);
        templateFileWriter.apply("/templates/README.tmpl", "README.md", classReplacer);
        templateFileWriter.apply("/templates/CHANGELOG.tmpl", "CHANGELOG.md", classReplacer);
        templateFileWriter.apply("/templates/LICENSE_HEADER.txt.tmpl", "LICENSE_HEADER.txt", classReplacer);
        templateFileWriter.apply("/templates/LICENSE.tmpl", "LICENSE.md", new NullReplacer());
        templateFileWriter.apply(LOG4J_PATH, DevkitUtils.MAIN_RESOURCES_FOLDER + "/log4j2.xml", new NullReplacer());

        String uncammelName = mavenModel.getModuleName();
        ImageWriter imageWriter = new ImageWriter(project, nullMonitor);
        imageWriter.apply("/templates/extension-icon-24x16.png", getIcon24FileName(uncammelName));
        imageWriter.apply("/templates/extension-icon-48x32.png", getIcon48FileName(uncammelName));

        if (!mavenModel.getApiType().equals(ApiType.SOAP)) {
            generator.create(project.getFolder(MAIN_JAVA_FOLDER + "/" + mavenModel.getPackage().replaceAll("\\.", "/") + "/" + "strategy"), nullMonitor);
            generateStrategyComponent(mavenModel, classReplacer, templateFileWriter);
        } else if (!mavenModel.getGenerateDefaultBody()) {
            // It is SOAP and we don't want Default body
            generator.create(project.getFolder(MAIN_JAVA_FOLDER + "/" + mavenModel.getPackage().replaceAll("\\.", "/") + "/" + "strategy"), nullMonitor);
            generateStrategyComponent(mavenModel, classReplacer, templateFileWriter);
        }

        if (mavenModel.getDataSenseEnabled()) {
            templateFileWriter.apply("/templates/connector_metadata_category.tmpl", MAIN_JAVA_FOLDER + "/" + mavenModel.getPackage().replaceAll("\\.", "/") + "/"
                    + "DataSenseResolver.java", classReplacer);
        }

        if (mavenModel.getApiType().equals(ApiType.SOAP)) {
            generator.create(project.getFolder("src/main/resources/wsdl/"), nullMonitor);
            templateFileWriter.apply("/templates/binding.xml.tmpl", "src/main/resources/wsdl/binding.xml", classReplacer);
            // Add extra binding file to prevent JABElement generation. WSDL2Connector solves this, so this needs to be added from outside.
            File wsdlFileOrDirectory = new File(mavenModel.getWsdlPath());
            try {
                if (wsdlFileOrDirectory.isDirectory()) {
                    String[] files = wsdlFileOrDirectory.list(new SuffixFileFilter(".wsdl"));
                    for (int i = 0; i < files.length; i++) {
                        File temp = new File(files[i]);
                        wsdlFileName = temp.getName();
                    }
                    org.apache.commons.io.FileUtils.copyDirectory(wsdlFileOrDirectory, project.getFolder("src/main/resources/wsdl/").getRawLocation().toFile());
                } else {
                    wsdlFileName = wsdlFileOrDirectory.getName();
                    if (mavenModel.getWsdlPath().startsWith("http")) {
                        wsdlFileName = mavenModel.getWsdlPath();
                    } else {
                        org.apache.commons.io.FileUtils.copyFileToDirectory(wsdlFileOrDirectory, project.getFolder("src/main/resources/wsdl/").getRawLocation().toFile());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not copy wsdl file to local directory");
            }
        }

        mavenModel.setWsdlPath(wsdlFileName);

        String mainTemplatePath = MAIN_NONE_ABSTRACT_TEMPLATE_PATH;

        if (mavenModel.getApiType().equals(ApiType.REST)) {
            mainTemplatePath = MAIN_TEMPLATE_PATH;
        }

        templateFileWriter.apply(POM_TEMPLATE_PATH, POM_FILENAME, classReplacer);

        create(mavenModel.getConnectorName(), mavenModel.getModuleName(), nullMonitor, mainTemplatePath, getTestResourcePath(),
                DevkitUtils.createConnectorNameFrom(mavenModel.getConnectorName()), mavenModel.getPackage(), project, classReplacer, mavenModel);

        DevkitUtils.configureDevkitAPT(javaProject);

        monitor.worked(20);

        return javaProject;
    }

    private String getConnectionStrategyFileName(ConnectorMavenModel mavenModel) {
        return MAIN_JAVA_FOLDER + "/" + mavenModel.getPackage().replaceAll("\\.", "/") + "/" + "strategy/ConnectorConnectionStrategy.java";
    }

    protected void create(String moduleName, String namespace, IProgressMonitor monitor, String mainTemplatePath, String testResourceTemplatePath, String className,
            String packageName, IProject project, ClassReplacer classReplacer, ConnectorMavenModel mavenModel) throws CoreException {

        TemplateFileWriter fileWriter = new TemplateFileWriter(project, monitor);

        ApiType apiType = mavenModel.getApiType();
        if (!apiType.equals(ApiType.SOAP) || !mavenModel.getGenerateDefaultBody()) {
            fileWriter.apply(mainTemplatePath, buildMainTargetFilePath(packageName, className), classReplacer);
        }
        if (mavenModel.getGenerateDefaultBody()) {
            if (!(apiType.equals(ApiType.REST) || apiType.equals(ApiType.SOAP))) {
                fileWriter.apply(testResourceTemplatePath, getResourceExampleFileName(namespace), classReplacer);
                fileWriter.apply(TEST_TEMPLATE_PATH, buildTestTargetFilePath(packageName, className), classReplacer);
                if (mavenModel.getDataSenseEnabled()) {
                    fileWriter.apply(TEST_DATASENSE_TEMPLATE_PATH, buildDataSenseTestTargetFilePath(packageName, className), classReplacer);
                }
                if (mavenModel.getHasQuery()) {
                    fileWriter.apply(TEST_QUERY_TEMPLATE_PATH, buildQueryTestTargetFilePath(packageName, className), classReplacer);
                }
            }
        }
        fileWriter.apply("/templates/example.tmpl", getExampleFileName(namespace), classReplacer);
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {

    }

    protected String getTestResourcePath() {
        return TEST_RESOURCE_PATH;
    }

    private String getWsdlPath() {

        return page.getWsdlFileOrDirectory();
    }

    private ApiType getApiType() {
        return page.getApiType();
    }

    private void downloadJavadocForAnnotations(IJavaProject javaProject, IProgressMonitor monitor) {
        MavenRunBuilder
                .newMavenRunBuilder()
                .withProject(javaProject)
                .withArgs(
                        new String[] { "dependency:resolve", "-Dclassifier=javadoc", "-DexcludeTransitive=false", "-DincludeGroupIds=org.mule.tools.devkit",
                                "-DincludeArtifactIds=mule-devkit-annotations" }).withTaskName("Downloading annotations sources...").build().run(monitor);
    }

    protected boolean canParseWSDL(IProgressMonitor monitor, String wsdlLocation) {
        try {
            File wsdlFileOrDirectory = new File(wsdlLocation);
            File wsdlFile = wsdlFileOrDirectory;

            if (wsdlFileOrDirectory.isDirectory()) {
                String[] files = wsdlFileOrDirectory.list(new SuffixFileFilter(".wsdl"));
                for (int i = 0; i < files.length; i++) {
                    File temp = new File(files[i]);
                    wsdlFile = new File(wsdlFileOrDirectory, temp.getName());
                }
            }
            if (wsdlFile.exists()) {
                wsdlLocation = wsdlFile.getAbsolutePath();
            }
            monitor.beginTask("Parsing WSDL", 100);
            monitor.worked(5);
            WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
            monitor.worked(15);
            wsdlReader.readWSDL(wsdlLocation);
            monitor.worked(80);
            monitor.done();
            return true;
        } catch (WSDLException e) {
            DevkitUIPlugin.getDefault().logError("Error Parsing WSDL", e);
        }
        return false;
    }

    private ConnectorMavenModel getPopulatedModel() {
        final ConnectorMavenModel mavenModel = new ConnectorMavenModel(advancePage.getVersion(), advancePage.getGroupId(), advancePage.getArtifactId(), page.getCategory(),
                advancePage.getPackage());
        if (!page.usesDefaultValues()) {
            mavenModel.setProjectLocation(page.getLocation());
        }
        mavenModel.setAddGitInformation(advancePage.getAddGitHubInfo());
        mavenModel.setGitConnection(advancePage.getConnection());
        mavenModel.setGitDevConnection(advancePage.getDevConnection());
        mavenModel.setGitUrl(advancePage.getUrl());
        mavenModel.setConnectorClassName(DevkitUtils.createConnectorNameFrom(page.getName()));
        mavenModel.setStrategyClassName("ConnectorConnectionStrategy");
        mavenModel.setDevkitVersion(page.getDevkitVersion());
        mavenModel.setPackage(advancePage.getPackage());
        mavenModel.setConnectorName(page.getName());
        mavenModel.setDataSenseEnabled(page.isMetadaEnabled());
        mavenModel.setHasQuery(page.hasQuery() && mavenModel.getDataSenseEnabled());
        mavenModel.setWsdlPath(getWsdlPath());
        mavenModel.setApiType(getApiType());
        mavenModel.setAuthenticationType(getAuthenticationType());
        mavenModel.setModuleName(page.getConnectorNamespace());
        mavenModel.setProjectName(page.getProjectName());
        mavenModel.setGenerateDefaultBody(page.generateDefaultBody());
        return mavenModel;
    }

    private void openConnectorClass(final ConnectorMavenModel mavenModel, final IProject project) {

        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                try {
                    IFile connectorFile = project.getFile(buildMainTargetFilePath(mavenModel.getPackage(), DevkitUtils.createConnectorNameFrom(mavenModel.getConnectorName())));
                    if (connectorFile.exists()) {
                        IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), connectorFile);
                    } else {
                        // Inform severe error
                    }
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private IProject createProject(String artifactId, IPath path, IProgressMonitor monitor, IWorkspaceRoot root) throws CoreException {

        IProjectDescription projectDescription = getProjectDescription(root, artifactId, path);

        return getProjectWithDescription(artifactId, monitor, root, projectDescription);
    }

    private IProjectDescription getProjectDescription(IWorkspaceRoot root, String artifactId, IPath path) throws CoreException {
        IProjectDescription projectDescription = root.getWorkspace().newProjectDescription(artifactId);
        projectDescription.setNatureIds(new String[] { JavaCore.NATURE_ID, DevkitNature.NATURE_ID });
        if (path != null) {
            projectDescription.setLocationURI(path.toFile().toURI());
        }
        ICommand[] commands = projectDescription.getBuildSpec();

        ICommand[] newCommands = new ICommand[commands.length + 1];
        System.arraycopy(commands, 0, newCommands, 0, commands.length);

        ICommand command = projectDescription.newCommand();
        command.setBuilderName(DevkitBuilder.BUILDER_ID);
        newCommands[newCommands.length - 1] = command;

        projectDescription.setBuildSpec(newCommands);
        return projectDescription;
    }

    /**
     * Generates the Strategy Component.
     * 
     * @param mavenModel
     * @param classReplacer
     * @param templateFileWriter
     * @throws CoreException
     */
    private void generateStrategyComponent(ConnectorMavenModel mavenModel, ClassReplacer classReplacer, TemplateFileWriter templateFileWriter) throws CoreException {
        if (!mavenModel.getApiType().equals(ApiType.SOAP) || !mavenModel.getGenerateDefaultBody()) {
            switch (mavenModel.getAuthenticationType()) {
            case CONNECTION_MANAGEMENT:
                templateFileWriter.apply("/templates/connector_connection_management.tmpl", getConnectionStrategyFileName(mavenModel), classReplacer);
                return;
            case HTTP_BASIC:
                templateFileWriter.apply("/templates/connector_basic_http_auth.tmpl", getConnectionStrategyFileName(mavenModel), classReplacer);
                return;
            case NONE:
                templateFileWriter.apply("/templates/connector_basic.tmpl", getConnectionStrategyFileName(mavenModel), classReplacer);
                return;
            case OAUTH_V2:
                templateFileWriter.apply("/templates/connector_oauth.tmpl", getConnectionStrategyFileName(mavenModel), classReplacer);
                return;
            default:
                break;

            }
            throw new RuntimeException("Unssuported AuthenticationType");
        }
    }
}