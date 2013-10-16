package org.mule.tooling.devkit.template.replacer;

import org.apache.velocity.VelocityContext;

public class ConnectorClassReplacer extends ClassReplacer {

	private boolean oauthEnabled;

	public ConnectorClassReplacer(String packageName, String moduleName,
			String className, boolean metadataEnabled, boolean oauthEnabled) {
		super(packageName, moduleName, className, metadataEnabled);
		this.oauthEnabled = oauthEnabled;
	}

	@Override
	protected void doReplace(VelocityContext context) {
		super.doReplace(context);
		context.put("OAuthEnaled", oauthEnabled);

	}
}
