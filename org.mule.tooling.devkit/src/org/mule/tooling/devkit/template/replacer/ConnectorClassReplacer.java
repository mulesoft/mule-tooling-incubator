package org.mule.tooling.devkit.template.replacer;

import org.apache.velocity.VelocityContext;
import org.mule.tooling.devkit.common.AuthenticationType;

public class ConnectorClassReplacer extends ClassReplacer {

    private boolean oauthEnabled;
    private boolean hasQuery;
    private boolean isSoapWithCXF;
    private String category;
    private String gitUrl;
    private AuthenticationType type;

    public ConnectorClassReplacer(String packageName, String moduleName, String className, String runtimeId, boolean metadataEnabled, boolean oauthEnabled, boolean hasQuery,
            String category, String gitUrl, boolean isSoapWithCXF, AuthenticationType type) {
        super(packageName, moduleName, className, runtimeId, metadataEnabled);
        this.oauthEnabled = oauthEnabled;
        this.hasQuery = hasQuery;
        this.category = category;
        this.gitUrl = gitUrl;
        this.isSoapWithCXF = isSoapWithCXF;
        this.type = type;

    }

    @Override
    protected void doReplace(VelocityContext context) {
        super.doReplace(context);
        context.put("OAuthEnaled", oauthEnabled);
        context.put("hasQuery", hasQuery);
        context.put("category", category);
        context.put("gitUrl", gitUrl);
        context.put("isSoapWithCXF", isSoapWithCXF);
        context.put("authenticationType", type);
    }
}
