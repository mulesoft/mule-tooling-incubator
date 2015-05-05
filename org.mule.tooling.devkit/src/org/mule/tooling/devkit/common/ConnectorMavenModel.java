package org.mule.tooling.devkit.common;

public class ConnectorMavenModel {

    private ApiType apiType;
    private AuthenticationType authenticationType = AuthenticationType.NONE;
    private String version;
    private String groupId;
    private String artifactId;
    private String category;
    private String connectorName = "Hello";
    private boolean addGitInformation;
    private String gitConnection;
    private String gitDevConnection;
    private String gitUrl;
    private String packageName;
    private String devkitVersion;
    private boolean dataSenseEnabled;
    private boolean hasQuery;
    private String wsdlPath;
    private String moduleName;
    private String connectorClassName;
    private String configClassName;
    private String projectName;
    private String projectLocation;
    private boolean generateDefaultBody;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getConnectorClassName() {
        return connectorClassName;
    }

    public void setConnectorClassName(String connectorClassName) {
        this.connectorClassName = connectorClassName;
    }

    public String getDevkitVersion() {
        return devkitVersion;
    }

    public void setDevkitVersion(String devkitVersion) {
        this.devkitVersion = devkitVersion;
    }

    public boolean getHasQuery() {
        return hasQuery;
    }

    public void setHasQuery(boolean hasQuery) {
        this.hasQuery = hasQuery;
    }

    public ApiType getApiType() {
        return apiType;
    }

    public void setApiType(ApiType apiType) {
        this.apiType = apiType;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ConnectorMavenModel(String version, String groupId, String artifactId, String category, String packageName) {
        this.version = version;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.category = category;
        this.packageName = packageName;
    }

    public ConnectorMavenModel() {

    }

    public String getVersion() {
        return version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getCategory() {
        return category;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public boolean getAddGitInformation() {
        return addGitInformation;
    }

    public void setAddGitInformation(boolean addGitInformation) {
        this.addGitInformation = addGitInformation;
    }

    public String getGitConnection() {
        return gitConnection;
    }

    public void setGitConnection(String gitConnection) {
        this.gitConnection = gitConnection;
    }

    public String getGitDevConnection() {
        return gitDevConnection;
    }

    public void setGitDevConnection(String gitDevConnection) {
        this.gitDevConnection = gitDevConnection;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getPackage() {
        return packageName;
    }

    public void setPackage(String packageName) {
        this.packageName = packageName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public boolean getDataSenseEnabled() {
        return dataSenseEnabled;
    }

    public void setDataSenseEnabled(boolean dataSenseEnabled) {
        this.dataSenseEnabled = dataSenseEnabled;
    }

    public String getWsdlPath() {
        return wsdlPath;
    }

    public void setWsdlPath(String wsdlPath) {
        this.wsdlPath = wsdlPath;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public boolean getGenerateDefaultBody() {
        return generateDefaultBody;
    }

    public void setGenerateDefaultBody(boolean generateDefaultBody) {
        this.generateDefaultBody = generateDefaultBody;
    }

    public String getConfigClassName() {
        return configClassName;
    }

    public void setConfigClassName(String configClassName) {
        this.configClassName = configClassName;
    }

}
