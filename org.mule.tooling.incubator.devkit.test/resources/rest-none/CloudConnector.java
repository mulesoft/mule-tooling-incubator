/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.cloud;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.Processor;

import java.io.IOException;
import org.mule.api.annotations.ReconnectOn;
import org.mule.api.annotations.rest.HttpMethod;
import org.mule.api.annotations.rest.RestCall;
import org.mule.api.annotations.rest.RestUriParam;

import org.mule.modules.cloud.config.ConnectorConfig;

/**
 * Anypoint Connector
 *
 * @author MuleSoft, Inc.
 */
@Connector(name="cloud", friendlyName="Cloud")
public abstract class CloudConnector {

    /**
     * Greeting message
     */
    @Configurable
    @Default("Hello")
    private String greeting;

    @Config
    ConnectorConfig config;   

    /**
     * Custom processor
     *
     * {@sample.xml ../../../doc/cloud-connector.xml.sample cloud:greet}
     *
     * @param friend Name of a friend we want to greet
     * @return The greeting and reply to the selected friend.
     * @throws IOException Comment for Exception
     */
    @Processor
    @ReconnectOn(exceptions = { Exception.class })
    @RestCall(uri="https://myapiurl/{friend}", method=HttpMethod.GET)
    public abstract void greet(@RestUriParam("friend") String friend) throws IOException;  

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

    /**
     * Set greeting message
     *
     * @param greeting the message
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
}