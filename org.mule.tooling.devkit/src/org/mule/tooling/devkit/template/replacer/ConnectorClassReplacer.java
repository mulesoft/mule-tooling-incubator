package org.mule.tooling.devkit.template.replacer;

import org.apache.velocity.VelocityContext;
import org.mule.tooling.devkit.common.AuthenticationType;
import org.mule.tooling.devkit.common.ConnectorMavenModel;

public class ConnectorClassReplacer extends ClassReplacer {

    private boolean oauthEnabled;
    private boolean hasQuery;
    private boolean isSoapWithCXF;
    private String category;
    private String gitUrl;
    private AuthenticationType type;
    private String strategyName;
    public ConnectorClassReplacer(String packageName, String moduleName, String className, String runtimeId, boolean metadataEnabled, boolean oauthEnabled, boolean hasQuery,
            String category, String gitUrl, boolean isSoapWithCXF, AuthenticationType type,String strategyName,ConnectorMavenModel model) {
        super(packageName, moduleName, className, runtimeId, metadataEnabled,model);
        this.oauthEnabled = oauthEnabled;
        this.hasQuery = hasQuery;
        this.category = category;
        this.gitUrl = gitUrl;
        this.isSoapWithCXF = isSoapWithCXF;
        this.type = type;
        this.strategyName = strategyName;

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
        context.put("StrategyClassName", strategyName);
    }
}
