package org.mule.tooling.devkit.template.replacer;

import org.apache.velocity.VelocityContext;

public class ConnectorClassReplacer extends ClassReplacer {

	private boolean oauthEnabled;
	private String minMuleVersion;
	private boolean hasQuery;
	private String category;
	private String gitUrl;
	public ConnectorClassReplacer(String packageName, String moduleName,
			String className, String runtimeId, boolean metadataEnabled,
			boolean oauthEnabled, String minMuleVersion, boolean hasQuery,
			String category,
			String gitUrl) {
		super(packageName, moduleName, className, runtimeId, metadataEnabled);
		this.oauthEnabled = oauthEnabled;
		this.minMuleVersion = minMuleVersion;
		this.hasQuery = hasQuery;
		this.category = category;
        this.gitUrl = gitUrl;

	}

	@Override
	protected void doReplace(VelocityContext context) {
		super.doReplace(context);
		context.put("OAuthEnaled", oauthEnabled);
		context.put("minMuleVersion", minMuleVersion);
		context.put("hasQuery", hasQuery);
		context.put("category", category);
		context.put("gitUrl", gitUrl);
	}
}
