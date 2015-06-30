package org.mule.modules.cloud;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;

import org.mule.modules.cloud.config.ConnectorConfig;

@Connector(name="cloud", friendlyName="Cloud")
public class CloudConnector {

    @Config
    ConnectorConfig config;

    /**
     * Custom processor
     *
     * {@sample.xml ../../../doc/cloud-connector.xml.sample cloud:greet}
     *
     * @param friend Name to be used to generate a greeting message.
     * @return A greeting message
     */
    @Processor
    public String greet(String friend) {
        /*
         * MESSAGE PROCESSOR CODE GOES HERE
         */
        return config.getGreeting() + " " + friend + ". " + config.getReply();
    }


    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

}