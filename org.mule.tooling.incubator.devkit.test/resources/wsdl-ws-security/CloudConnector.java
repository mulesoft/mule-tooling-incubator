package org.mule.modules.cloud;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.modules.cloud.config.ConnectorConfig;

@Connector(name="cloud", friendlyName="Cloud", minMuleVersion = "3.7")
public class CloudConnector {

    @Config
    ConnectorConfig config;

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

}