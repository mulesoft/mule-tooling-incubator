package org.mule.modules.cloud.config;

import org.mule.api.annotations.ws.WsdlTransportRetriever;
import org.mule.devkit.api.ws.transport.WsdlTransport;
import org.mule.devkit.api.ws.transport.HttpBasicWsdlTransport;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.components.WsdlProvider;
import org.mule.api.annotations.ws.WsdlServiceEndpoint;
import org.mule.api.annotations.ws.WsdlServiceRetriever;
import org.mule.devkit.api.ws.definition.DefaultServiceDefinition;
import org.mule.devkit.api.ws.definition.ServiceDefinition;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;

@WsdlProvider(configElementName = "config-type", friendlyName = "Config")
public class ConnectorConfig {

    @Configurable
    @Placement(order = 1)
    private String username;

    @Configurable
    @Placement(order = 2)
    @Password
    @Optional
    private String password;

    @Configurable
    @Default("https://login.salesforce.com/services/Soap/c/22.0")
    @Placement(order = 3)
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

    @WsdlTransportRetriever
    public WsdlTransport resolveTransport(ServiceDefinition serviceDefinition) {
        return new HttpBasicWsdlTransport(getUsername(), getPassword());
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}