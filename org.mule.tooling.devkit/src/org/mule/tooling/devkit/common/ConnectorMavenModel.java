package org.mule.tooling.devkit.common;

public class ConnectorMavenModel {

    private String version;
    private String groupId;
    private String artifactId;
    private String category;
    private String connectorName = "Hello";
    private boolean	oAuthEnabled;
    private boolean	metadataEnabled;
    private boolean addGitInformation;
    private String gitConnection;
    private String gitDevConnection;
    private String gitUrl;
    
	public ConnectorMavenModel(String version, String groupId, String artifactId,String category) {
        this.version = version;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.category = category;
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

	public boolean isMetadataEnabled() {
		return metadataEnabled;
	}

	public void setMetadataEnabled(boolean metadataEnabled) {
		this.metadataEnabled = metadataEnabled;
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
	
}
