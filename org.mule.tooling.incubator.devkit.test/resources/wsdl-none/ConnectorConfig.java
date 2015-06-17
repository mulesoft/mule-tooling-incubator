/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.cloud.config;

import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.components.WsdlProvider;
import org.mule.api.annotations.ws.WsdlServiceEndpoint;
import org.mule.api.annotations.ws.WsdlServiceRetriever;
import org.mule.devkit.api.ws.definition.DefaultServiceDefinition;
import org.mule.devkit.api.ws.definition.ServiceDefinition;
import org.mule.api.annotations.param.Default;

@WsdlProvider(configElementName = "config-type", friendlyName = "Config")
public class ConnectorConfig {

    @Configurable
    @Default("https://login.salesforce.com/services/Soap/c/22.0")
    private String address;

    @WsdlServiceRetriever
    public ServiceDefinition getServiceDefinition() {
           return new DefaultServiceDefinition(
                "SforceService_Soap",
                "Salesforce API",
                "wsdl/enterprise.wsdl",
                "SforceService",
                "Soap");
    }

    @WsdlServiceEndpoint
    public String getServiceAddress(ServiceDefinition definition) {
         return address;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}