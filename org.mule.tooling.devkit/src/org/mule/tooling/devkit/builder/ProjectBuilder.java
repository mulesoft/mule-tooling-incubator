package org.mule.tooling.devkit.builder;

import static org.mule.tooling.devkit.common.DevkitUtils.DEMO_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.DOCS_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.GENERATED_SOURCES_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.ICONS_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_JAVA_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.MAIN_RESOURCES_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.POM_FILENAME;
import static org.mule.tooling.devkit.common.DevkitUtils.POM_TEMPLATE_PATH;
import static org.mule.tooling.devkit.common.DevkitUtils.TEST_JAVA_FOLDER;
import static org.mule.tooling.devkit.common.DevkitUtils.TEST_RESOURCES_FOLDER;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.common.ApiType;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.maven.MavenRunBuilder;
import org.mule.tooling.devkit.template.ImageWriter;
import org.mule.tooling.devkit.template.TemplateFileWriter;
import org.mule.tooling.devkit.template.replacer.ComponentReplacer;
import org.mule.tooling.devkit.template.replacer.NullReplacer;

public class ProjectBuilder {

    private ApiType apiType = ApiType.GENERIC;
    private AuthenticationType authenticationType = AuthenticationType.NONE;
    private String version;
    private String groupId;
    private String artifactId;
    private String category;
    private String connectorName;
    private boolean addGitInformation = false;
    private String gitConnection;
    private String gitDevConnection;
    private String gitUrl;
    private String packageName;
    private String devkitVersion;
    private boolean dataSenseEnabled = false;
    private boolean hasQuery = false;
    private String wsdlPath;
    private String moduleName;
    private String connectorClassName;
    private String configClassName;
    private String projectName;
    private String projectLocation;
    private boolean generateDefaultBody = true;
    private String bigIcon = "";
    private String smallIcon = "";

    private Map<String, String> wsdlFiles = new HashMap<String, String>();

    public ProjectBuilder withApiType(ApiType apiType) {
        this.apiType = apiType;
        return this;
    }

    public ProjectBuilder withAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
        return this;
    }

    public ProjectBuilder withVersion(String version) {
        this.version = version;
        return this;
    }

    public ProjectBuilder withGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public ProjectBuilder withArtifactId(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public ProjectBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public ProjectBuilder withConnectorName(String connectorName) {
        this.connectorName = connectorName;
        return this;
    }

    public ProjectBuilder withAddGitInformation(boolean addGitInformation) {
        this.addGitInformation = addGitInformation;
        return this;
    }

    public ProjectBuilder withGitConnection(String gitConnection) {
        this.gitConnection = gitConnection;
        return this;
    }

    public ProjectBuilder withGitDevConnection(String gitDevConnection) {
        this.gitDevConnection = gitDevConnection;
        return this;
    }

    public ProjectBuilder withGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
        return this;
    }

    public ProjectBuilder withPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public ProjectBuilder withDevkitVersion(String devkitVersion) {
        this.devkitVersion = devkitVersion;
        return this;
    }

    public ProjectBuilder withDataSenseEnabled(boolean dataSenseEnabled) {
        this.dataSenseEnabled = dataSenseEnabled;
        return this;
    }

    public ProjectBuilder withHasQuery(boolean hasQuery) {
        this.hasQuery = hasQuery;
        return this;
    }

    public ProjectBuilder withWsdlPath(String wsdlPath) {
        this.wsdlPath = wsdlPath;
        return this;
    }

    public ProjectBuilder withModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public ProjectBuilder withConnectorClassName(String connectorClassName) {
        this.connectorClassName = connectorClassName;
        return this;
    }

    public ProjectBuilder withConfigClassName(String configClassName) {
        this.configClassName = configClassName;
        return this;
    }

    public ProjectBuilder withProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public ProjectBuilder withProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
        return this;
    }

    public ProjectBuilder withGenerateDefaultBody(boolean generateDefaultBody) {
        this.generateDefaultBody = generateDefaultBody;
        return this;
    }

    public ProjectBuilder withWsdlFiles(Map<String, String> wsdlFiles) {
        this.wsdlFiles = wsdlFiles;
        return this;
    }

    public ProjectBuilder withBigIcon(String bigIcon) {
        this.bigIcon = bigIcon;
        return this;
    }

    public ProjectBuilder withSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
        return this;
    }

    protected ProjectBuilder() {
    }

    public void build(IProject project, IProgressMonitor monitor) throws CoreException {
        if (getApiType().equals(ApiType.SOAP)) {
            if (!canParseWSDL(monitor, wsdlPath)) {
                throw new IllegalArgumentException("Cannot process WSDL");
            }
        }
        monitor.subTask("Creating project folders.");
        createProjectFolders(project, monitor);
    }

    public IProject build(IProgressMonitor monitor) throws CoreException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        if (StringUtils.isEmpty(projectName)) {
            throw new IllegalArgumentException("Project name was not provided");
        }
        if (StringUtils.isEmpty(packageName)) {
            throw new IllegalArgumentException("Package name was not provided");
        }
        monitor.subTask("Creating project");

        IPath path = StringUtils.isBlank(projectLocation) ? null : Path.fromOSString(projectLocation);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        IProject project = createProject(projectName, path, monitor, root);
        createProjectFolders(project, monitor);

        List<IClasspathEntry> entries = generateProjectEntries(project, monitor);
        if (ApiType.SOAP.equals(apiType)) {
            entries.add(createEntry(project.getFolder(DevkitUtils.CXF_GENERATED_SOURCES_FOLDER), monitor));

        }

        IJavaProject javaProject = JavaCore.create(root.getProject(projectName));
        javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[] {}), monitor);

        createProjectFiles(monitor, project);

        DevkitUtils.configureDevkitAPT(javaProject);

        if (getApiType().equals(ApiType.SOAP)) {
            MavenRunBuilder.newMavenRunBuilder().withProject(javaProject).withArg("clean").withArg("compile").withArg("-Pconnector-generator")
                    .withTaskName("Generating connector from WSDL...").build().run(monitor);

            MavenRunBuilder.newMavenRunBuilder().withProject(javaProject).withArg("license:format").withTaskName("Adding headers...").build().run(monitor);

            javaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
        }

        return project;
    }

    private void createProjectFiles(IProgressMonitor monitor, IProject project) throws CoreException {
        Map<String, Object> projectMap = new HashMap<String, Object>();
        ComponentReplacer replacer = new ComponentReplacer(projectMap);

        populateModel(projectMap, monitor);

        TemplateFileWriter templateFileWriter = new TemplateFileWriter(project, monitor);

        createGeneralFiles(project, replacer, templateFileWriter, monitor);

        createJavaProjectFiles(replacer, templateFileWriter);

        createTestFiles(replacer, templateFileWriter);
    }

    private void createJavaProjectFiles(ComponentReplacer replacer, TemplateFileWriter templateFileWriter) throws CoreException {

        if (!apiType.equals(ApiType.SOAP) || !this.generateDefaultBody) {
            templateFileWriter.apply(getConnectorTemplate(), getMainTargetFilePath(), replacer);
        }

        templateFileWriter.apply(getConfigTemplate(), getConfigFileName(), replacer);

        if (dataSenseEnabled) {
            templateFileWriter.apply("/templates/connector_metadata_category.tmpl", MAIN_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + "DataSenseResolver.java",
                    replacer);
        }
    }

    private void createTestFiles(ComponentReplacer replacer, TemplateFileWriter templateFileWriter) throws CoreException {
        if (generateDefaultBody) {
            if (!(apiType.equals(ApiType.REST) || apiType.equals(ApiType.SOAP))) {

                templateFileWriter.apply("/templates/connector-test-parent.tmpl", buildTestParentFilePath(packageName, "AbstractTestCase"), replacer);
                if (!apiType.equals(ApiType.SOAP)) {
                    templateFileWriter.apply("/templates/connector-test-regression-suite.tmpl", buildRegressionTestsFilePath(packageName, connectorClassName), replacer);
                    templateFileWriter.apply("/templates/connector-test.tmpl", buildTestTargetFilePath(packageName, connectorClassName), replacer);
                    if (dataSenseEnabled) {
                        templateFileWriter.apply("/templates/connector-test-datasense.tmpl", buildDataSenseTestTargetFilePath(packageName, connectorClassName), replacer);
                    }
                    if (hasQuery) {
                        templateFileWriter.apply("/templates/connector-query-test.tmpl", buildQueryTestTargetFilePath(packageName, connectorClassName), replacer);
                    }
                }

            }
        }
    }

    private String getConfigTemplate() {
        String mainTemplatePath = "/templates/connector_none_abstract_main.tmpl";
        if (apiType.equals(ApiType.WSDL)) {
            mainTemplatePath = "/templates/connector_wsdl.tmpl";
        } else {
            switch (authenticationType) {
            case CONNECTION_MANAGEMENT:
                mainTemplatePath = "/templates/connector_connection_management.tmpl";
                break;
            case HTTP_BASIC:
                mainTemplatePath = "/templates/connector_basic_http_auth.tmpl";
                break;
            case NONE:
                mainTemplatePath = "/templates/connector_basic.tmpl";
                break;
            case OAUTH_V2:
                mainTemplatePath = "/templates/connector_oauth.tmpl";
                break;
            default:
                break;
            }

        }
        return mainTemplatePath;
    }

    private String getConnectorTemplate() {
        String mainTemplatePath = "/templates/connector_none_abstract_main.tmpl";
        if (apiType.equals(ApiType.REST)) {
            mainTemplatePath = "/templates/connector_main.tmpl";
        } else if (apiType.equals(ApiType.WSDL)) {
            mainTemplatePath = "/templates/connector_wsdl_base.tmpl";
        }
        return mainTemplatePath;
    }

    private void createGeneralFiles(IProject project, ComponentReplacer replacer, TemplateFileWriter templateFileWriter, IProgressMonitor monitor) throws CoreException {
        templateFileWriter.apply("/templates/README.tmpl", "README.md", replacer);
        templateFileWriter.apply("/templates/CHANGELOG.tmpl", "CHANGELOG.md", replacer);
        templateFileWriter.apply("/templates/LICENSE_HEADER.txt.tmpl", "LICENSE_HEADER.txt", replacer);
        templateFileWriter.apply("/templates/LICENSE.tmpl", "LICENSE.md", new NullReplacer());
        templateFileWriter.apply(DevkitUtils.LOG4J_PATH, MAIN_RESOURCES_FOLDER + "/log4j2.xml", new NullReplacer());

        templateFileWriter.apply("/templates/example.tmpl", getExampleFileName(), replacer);
        templateFileWriter.apply("/templates/connector-test-automation-credentials.properties.tmpl", "automation-credentials.properties", replacer);

        createIcons(project, monitor);

        if (apiType.equals(ApiType.SOAP)) {
            String wsdlFileName = wsdlPath;
            final String original = wsdlFileName;
            templateFileWriter.apply("/templates/binding.xml.tmpl", "src/main/resources/wsdl/binding.xml", replacer);
            File wsdlFileOrDirectory = new File(wsdlFileName);
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
                    if (original.startsWith("http")) {
                        wsdlFileName = original;
                    } else {
                        org.apache.commons.io.FileUtils.copyFileToDirectory(wsdlFileOrDirectory, project.getFolder("src/main/resources/wsdl/").getRawLocation().toFile());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not copy wsdl file to local directory");
            }
            replacer.update("wsdlPath", wsdlFileName);
        } else if (apiType.equals(ApiType.WSDL)) {
            for (Entry<String, String> keyValuePair : wsdlFiles.entrySet()) {
                try {
                    if (!keyValuePair.getKey().startsWith("http")) {
                        org.apache.commons.io.FileUtils.copyFileToDirectory(new File(keyValuePair.getKey()), project.getFolder("src/main/resources/wsdl/").getRawLocation()
                                .toFile());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        templateFileWriter.apply(POM_TEMPLATE_PATH, POM_FILENAME, replacer);
    }

    private void createIcons(IProject project, IProgressMonitor monitor) {
        try {
            ImageWriter imageWriter = new ImageWriter(project, monitor);
            if (StringUtils.isEmpty(smallIcon)) {
                imageWriter.apply("/templates/extension-icon-24x16.png", getIcon24FileName());
            } else {
                org.apache.commons.io.FileUtils.moveFile(new File(smallIcon), new File(project.getProject().getFile(getIcon24FileName()).getLocationURI()));
            }
            if (StringUtils.isEmpty(bigIcon)) {
                imageWriter.apply("/templates/extension-icon-48x32.png", getIcon48FileName());
            } else {
                org.apache.commons.io.FileUtils.moveFile(new File(bigIcon), new File(project.getProject().getFile(getIcon48FileName()).getLocationURI()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateModel(Map<String, Object> projectMap, IProgressMonitor monitor) {
        projectMap.put("apiType", apiType);
        projectMap.put("authenticationType", authenticationType);
        projectMap.put("version", version);
        projectMap.put("groupId", groupId);
        projectMap.put("artifactId", artifactId);
        projectMap.put("category", category);
        projectMap.put("connectorName", connectorName);
        projectMap.put("addGitInformation", addGitInformation);
        projectMap.put("gitConnection", gitConnection);
        projectMap.put("gitDevConnection", gitDevConnection);
        projectMap.put("gitUrl", gitUrl);
        projectMap.put("package", packageName);
        projectMap.put("devkitVersion", devkitVersion);
        projectMap.put("dataSenseEnabled", dataSenseEnabled);
        projectMap.put("hasQuery", hasQuery);
        projectMap.put("wsdlPath", wsdlPath);
        projectMap.put("moduleName", moduleName);
        projectMap.put("connectorClassName", connectorClassName);
        projectMap.put("configClassName", configClassName);
        projectMap.put("projectName", projectName);
        projectMap.put("projectLocation", projectLocation);
        projectMap.put("generateDefaultBody", generateDefaultBody);
        try {
            projectMap.put("serviceDefinitions", this.getServiceDefinitions(monitor));
        } catch (WSDLException e) {
            e.printStackTrace();
        }
    }

    public List<IClasspathEntry> generateProjectEntries(IProject project, IProgressMonitor monitor) throws CoreException {
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
        entries.add(createEntry(project.getFolder(MAIN_JAVA_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(MAIN_RESOURCES_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(TEST_RESOURCES_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(TEST_JAVA_FOLDER), monitor));
        entries.add(createEntry(project.getFolder(GENERATED_SOURCES_FOLDER), monitor));
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
        create(project.getFolder(MAIN_RESOURCES_FOLDER), monitor);
        create(project.getFolder(TEST_RESOURCES_FOLDER), monitor);
        create(project.getFolder(MAIN_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/")), monitor);
        create(project.getFolder(MAIN_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/config"), monitor);
        create(project.getFolder(TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/")), monitor);
        create(project.getFolder(TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation"), monitor);
        create(project.getFolder(TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation/testrunners"), monitor);
        create(project.getFolder(TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation/testcases"), monitor);
        if (apiType.equals(ApiType.SOAP)) {
            create(project.getFolder("src/main/resources/wsdl/"), monitor);
        }

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
            ((IFolder) resource).create(IResource.FORCE, true, monitor);
            break;
        case IResource.PROJECT:
            break;
        }
    }

    private IProject createProject(String projectName, IPath path, IProgressMonitor monitor, IWorkspaceRoot root) throws CoreException {

        IProjectDescription projectDescription = getProjectDescription(root, projectName, path);

        IProject project = root.getProject(projectName);
        if (!project.exists()) {
            project.create(projectDescription, monitor);
            project.open(monitor);
            project.setDescription(projectDescription, monitor);
        }
        return project;
    }

    private IProjectDescription getProjectDescription(IWorkspaceRoot root, String projectName, IPath path) throws CoreException {
        IProjectDescription projectDescription = root.getWorkspace().newProjectDescription(projectName);
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

    protected String getResourceExampleFileName() {
        return TEST_RESOURCES_FOLDER + "/" + moduleName + "-config.xml";
    }

    protected String getIcon48FileName() {
        return "icons/" + moduleName + "-connector-48x32.png";
    }

    protected String getIcon24FileName() {
        return "icons/" + moduleName + "-connector-24x16.png";
    }

    protected String getExampleFileName() {
        return "doc" + "/" + moduleName + "-connector.xml.sample";
    }

    protected String getMainTargetFilePath() {
        return MAIN_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + connectorClassName + ".java";
    }

    protected String getTestTargetFilePath() {
        return TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + connectorClassName + "Test.java";
    }

    protected String getQueryTestTargetFilePath() {
        return TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + connectorClassName + "QueryTest.java";
    }

    protected String getDataSenseTestTargetFilePath() {
        return TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + connectorClassName + "AddEntityTest.java";
    }

    protected String getConfigFileName() {
        return MAIN_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/" + "config/" + configClassName + ".java";
    }

    protected String buildTestParentFilePath(final String packageName, String className) {
        return TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation/" + className + ".java";
    }

    protected String buildRegressionTestsFilePath(final String packageName, String className) {
        return TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation/testrunners/RegressionTestSuite.java";
    }

    protected String buildTestTargetFilePath(final String packageName, String className) {
        return TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation/testcases/GreetTestCases.java";
    }

    protected String buildQueryTestTargetFilePath(final String packageName, String className) {
        return TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation/testcases/QueryProcessorTestCases.java";
    }

    protected String buildDataSenseTestTargetFilePath(final String packageName, String className) {
        return TEST_JAVA_FOLDER + "/" + packageName.replaceAll("\\.", "/") + "/automation/testcases/AddEntityTestCases.java";
    }

    @SuppressWarnings("rawtypes")
    private List<ServiceDefinition> getServiceDefinitions(IProgressMonitor monitor) throws WSDLException {
        WSDLFactory factory;
        List<ServiceDefinition> deff = new ArrayList<ServiceDefinition>();
        factory = WSDLFactory.newInstance();

        ExtensionRegistry registry = factory.newPopulatedExtensionRegistry();
        javax.wsdl.xml.WSDLReader wsdlReader = factory.newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose", false);
        wsdlReader.setFeature("javax.wsdl.importDocuments", true);
        wsdlReader.setExtensionRegistry(registry);
        for (Entry<String, String> wsdlFile : wsdlFiles.entrySet()) {
            try {
                monitor.setTaskName("Parsing: " + wsdlFile.getKey());
                javax.wsdl.Definition definition = wsdlReader.readWSDL(wsdlFile.getKey());
                Map services = definition.getAllServices();
                for (Object serviceDef : services.values()) {
                    javax.wsdl.Service serviceItem = (javax.wsdl.Service) serviceDef;
                    String name = serviceItem.getQName().getLocalPart();
                    Map portsMap = serviceItem.getPorts();
                    boolean hasMultiplePorts = portsMap.values().size() > 1;
                    for (Object portDef : portsMap.values()) {
                        javax.wsdl.Port portItem = (javax.wsdl.Port) portDef;
                        String portName = portItem.getName();
                        String addressValue = getPortAddress(portItem);
                        ServiceDefinition serviceDefinition = new ServiceDefinition();
                        serviceDefinition.setId(name + "_" + portName);
                        serviceDefinition.setAddress(addressValue);
                        serviceDefinition.setServiceName(name);
                        serviceDefinition.setServicePort(portName);
                        serviceDefinition.setDisplay(hasMultiplePorts ? wsdlFile.getValue() + " (" + portName + ")" : wsdlFile.getValue());
                        if (wsdlFile.getKey().startsWith("http")) {
                            serviceDefinition.setLocation(wsdlFile.getKey());
                        } else {
                            serviceDefinition.setLocation("wsdl/" + Path.fromOSString(wsdlFile.getKey()).lastSegment());
                        }
                        deff.add(serviceDefinition);
                    }
                }
            } catch (Exception ex) {

            } finally {
                monitor.worked(1);
            }
        }
        return deff;
    }

    private String getPortAddress(javax.wsdl.Port portItem) {
        String addressValue = "";
        @SuppressWarnings("rawtypes")
        List extElements = portItem.getExtensibilityElements();
        for (Object element : extElements) {
            if (element instanceof SOAPAddress) {
                SOAPAddress address = (SOAPAddress) element;
                addressValue = address.getLocationURI();

            } else if (element instanceof SOAP12Address) {
                SOAP12Address address = (SOAP12Address) element;
                addressValue = address.getLocationURI();
            } else {
                throw new RuntimeException("Typo raro:" + element.getClass());
            }
        }
        return addressValue;
    }

    private boolean canParseWSDL(IProgressMonitor monitor, String wsdlLocation) {
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

    public String getConnectorClassName() {
        return connectorClassName;
    }

    public ApiType getApiType() {
        return apiType;
    }

    public String getProjectName() {
        return projectName;
    }
}
