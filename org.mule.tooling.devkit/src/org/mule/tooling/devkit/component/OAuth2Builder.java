package org.mule.tooling.devkit.component;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;

public class OAuth2Builder implements IComponentBuilder {

    @Override
    public String getAnnotation(ICompilationUnit compilationUnit) {
        return new StringBuilder("@OAuth2( configElementName = \"oauth2-type\", friendlyName=\"OAuth2 Configuration\",")
                .append("accessTokenUrl = \"https://foo.com/api/oauth.access\",").append("authorizationUrl = \"https://foo.com/oauth/authorize\",")
                .append("accessTokenRegex = \"access_token\\\":\\\"([^&]+?)\",").append("expirationRegex = \"expires_in\\\":\\\"([^&]+?)\",")
                .append("refreshTokenRegex = \"refresh_token\\\":\\\"([^&]+?)\" )").toString();
    }

    @Override
    public void createTypeMembers(Map<String, Object> options, IType newType, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
        imports.addImport("org.mule.api.annotations.oauth.*");
        imports.addImport("org.mule.api.annotations.Configurable");

        newType.createField("@OAuthAccessToken private String accessToken;", null, true, monitor);
           
        newType.createField("@Configurable @OAuthConsumerKey private String consumerKey;", null, true, monitor);
        newType.createField("@Configurable @OAuthConsumerSecret private String consumerSecret;", null, true, monitor);

        newType.createMethod("@OAuthPostAuthorization public void postAuthorize() { \n// TODO\n }", null, true, monitor);

        newType.createMethod("public void setAccessToken(String accessToken) { this.accessToken = accessToken; }", null, true, monitor);
        newType.createMethod("public String getAccessToken() { return this.accessToken; }", null, true, monitor);

        newType.createMethod("public void setConsumerKey(String consumerKey) { this.consumerKey = consumerKey; }", null, true, monitor);
        newType.createMethod("public String getConsumerKey() { return this.consumerKey; }", null, true, monitor);

        newType.createMethod("public void setConsumerSecret(String consumerSecret) { this.consumerSecret = consumerSecret; }", null, true, monitor);
        newType.createMethod("public String getConsumerSecret() { return this.consumerSecret; }", null, true, monitor);
    }

}
