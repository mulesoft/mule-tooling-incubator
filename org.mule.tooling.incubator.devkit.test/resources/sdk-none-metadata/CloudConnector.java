/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.cloud;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Configurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.mule.api.annotations.param.MetaDataKeyParam;
import org.mule.api.annotations.MetaDataScope;

import org.mule.api.annotations.Processor;

import org.mule.api.annotations.Query;
import org.mule.api.annotations.param.Default;

import org.mule.modules.cloud.config.ConnectorConfig;

@Connector(name="cloud", friendlyName="Cloud")
@MetaDataScope( DataSenseResolver.class )
public class CloudConnector {
    
    /**
     * Configurable
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
     * @param friend Content to be processed
     * @return Some string
     */
    @Processor
    public String greet(String friend) {
        /*
         * MESSAGE PROCESSOR CODE GOES HERE
         */
        return greeting + " " + friend + ". " + config.getReply();
    }

    
    /**
     * Description for query
     *
     * {@sample.xml ../../../doc/cloud-connector.xml.sample cloud:query-processor}
     *
     *  @param query The dsql query
     *  @return List of elements that match the criteria
     */
    @Processor
    public List<Object> queryProcessor(@Query String query) {
        //TODO
        /*
         * MESSAGE PROCESSOR CODE GOES HERE
         */
        return new ArrayList<Object>();
    }

    /**
     * DataSense processor
     *
     * {@sample.xml ../../../doc/cloud-connector.xml.sample cloud:add-entity}

     * @param key Key to be used to populate the entity
     * @param entity Map that represents the entity
     * @return Some string
     */
    @Processor
    public Map<String,Object> addEntity( @MetaDataKeyParam String key, @Default("#[payload]") Map<String,Object> entity) {
        /*
         * USE THE KEY AND THE MAP TO DO SOMETHING
         */
        return entity;
    }

    /**
     * Set property
     *
     * @param greeting My property
     */
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    /**
     * Get property
     */
    public String getGreeting() {
        return this.greeting;
    }

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

}