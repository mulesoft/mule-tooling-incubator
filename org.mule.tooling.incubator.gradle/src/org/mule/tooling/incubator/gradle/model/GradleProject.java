package org.mule.tooling.incubator.gradle.model;

public class GradleProject {

    final private String groupId;
    final private String runtimeVersion;
    final private String version;
    final private boolean muleEnterprise;
    final private String repoUser;
    final private String repoPassword;
    final private String pluginVersion;
    
    public GradleProject(String groupId, String runtimeVersion, String version, boolean muleEnterprise, String repoUser, String repoPassword, String pluginVersion) {
        super();
        this.groupId = groupId;
        this.runtimeVersion = runtimeVersion;
        this.version = version;
        this.muleEnterprise = muleEnterprise;
        this.repoUser = repoUser;
        this.repoPassword = repoPassword;
        this.pluginVersion = pluginVersion;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getRuntimeVersion() {
        return runtimeVersion;
    }

    public String getVersion() {
        return version;
    }

    public boolean isMuleEnterprise() {
        return muleEnterprise;
    }

    public String getRepoUser() {
        return repoUser;
    }

    public String getRepoPassword() {
        return repoPassword;
    }

	public String getPluginVersion() {
		return pluginVersion;
	}

}
