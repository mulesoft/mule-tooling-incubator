package org.mule.tooling.devkit.common;

public class ConnectorMavenModel {

    private String version;
    private String groupId;
    private String artifactId;
    private String category;
    private String connectorName = "Hello";
    private boolean oAuthEnabled;
    private AuthenticationType authenticationType;
    private boolean addGitInformation;
    private String gitConnection;
    private String gitDevConnection;
    private String gitUrl;
    private String packageName;
    private String devkitVersion;
    private boolean isOAuth;
    private boolean isMetaDataEnabled;
    private boolean hasQuery;
    private boolean isSoapWithCXF;
    private String wsdlPath;
    private ApiType apiType;

    public boolean isoAuthEnabled() {
        return oAuthEnabled;
    }

    public void setoAuthEnabled(boolean oAuthEnabled) {
        this.oAuthEnabled = oAuthEnabled;
    }

    public String getDevkitVersion() {
        return devkitVersion;
    }

    public void setDevkitVersion(String devkitVersion) {
        this.devkitVersion = devkitVersion;
    }

    public boolean isOAuth() {
        return isOAuth;
    }

    public void setOAuth(boolean isOAuth) {
        this.isOAuth = isOAuth;
    }

    public boolean isMetaDataEnabled() {
        return isMetaDataEnabled;
    }

    public void setMetaDataEnabled(boolean isMetaDataEnabled) {
        this.isMetaDataEnabled = isMetaDataEnabled;
    }

    public boolean isHasQuery() {
        return hasQuery;
    }

    public void setHasQuery(boolean hasQuery) {
        this.hasQuery = hasQuery;
    }

    public boolean isSoapWithCXF() {
        return isSoapWithCXF;
    }

    public void setSoapWithCXF(boolean isSoapWithCXF) {
        this.isSoapWithCXF = isSoapWithCXF;
    }

    public String getWsdlPath() {
        return wsdlPath;
    }

    public void setWsdlPath(String wsdlPath) {
        this.wsdlPath = wsdlPath;
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

    public boolean isOAuthEnabled() {
        return oAuthEnabled;
    }

    public void setOAuthEnabled(boolean oAuthEnabled) {
        this.oAuthEnabled = oAuthEnabled;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public boolean isAddGitInformation() {
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

}
