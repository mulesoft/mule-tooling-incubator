package org.mule.modules.cloud.config;

import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.oauth.OAuth2;
import org.mule.api.annotations.oauth.OAuthAccessToken;
import org.mule.api.annotations.oauth.OAuthConsumerKey;
import org.mule.api.annotations.oauth.OAuthConsumerSecret;
import org.mule.api.annotations.oauth.OAuthPostAuthorization;
import org.mule.api.annotations.param.Default;

@OAuth2( configElementName = "config-type", friendlyName="OAuth2 Configuration", authorizationUrl = "https://api.myconnector.com/uas/oauth/authorize", 
accessTokenUrl = "https://api.myconnector.com/uas/oauth/accessToken", 
accessTokenRegex = "\"access_token\":\"([^&]+?)\"",
expirationRegex = "\"expires_in\":([^&]+?),", 
refreshTokenRegex = "\"refresh_token\":\"([^&]+?)\"" )
public class ConnectorConfig {
    
    /**
     * Greeting message
     */
    @Configurable
    @Default("Hello")
    private String greeting;

    /**
     * Configurable
     */
    @Configurable
    @Default("How are you?")
    private String reply;

    /**
     * The OAuth access token
     */
    @OAuthAccessToken
    private String accessToken;
    
    /**
     * The OAuth consumer key
     */
    @Configurable
    @OAuthConsumerKey
    private String consumerKey;

    /**
     * The OAuth consumer secret
     */
    @Configurable
    @OAuthConsumerSecret
    private String consumerSecret;

    @OAuthPostAuthorization
    public void postAuthorize() {
        //This method is called after authorization finishes. Remove if not required 
    }
    
    /**
     * Set greeting message
     *
     * @param greeting the greeting message
     */
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    /**
     * Get greeting message
     */
    public String getGreeting() {
        return this.greeting;
    }

    /**
     * Set config property
     *
     * @param reply my config property
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * Get property
     */
    public String getReply() {
        return this.reply;
    }

    /**
     * Set accessToken
     *
     * @param accessToken
     *            The accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Get accessToken
     */
    public String getAccessToken() {
        return this.accessToken;
    }

    /**
     * Set consumerKey
     *
     * @param consumerKey The consumerKey
     */
    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    /**
     * Get consumerKey
     */
    public String getConsumerKey() {
        return this.consumerKey;
    }

    /**
     * Set consumerSecret
     *
     * @param consumerSecret The consumerSecret
     */
    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    /**
     * Get consumerSecret
     */
    public String getConsumerSecret() {
        return this.consumerSecret;
    }

}