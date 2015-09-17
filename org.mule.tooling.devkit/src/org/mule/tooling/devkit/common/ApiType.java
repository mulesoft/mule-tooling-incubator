package org.mule.tooling.devkit.common;

public enum ApiType {
    GENERIC("Java SDK","JAVA_SDK"), SOAP("SOAP","SOAP_SDK"), REST("REST","REST_SDK"), WSDL("WSDL","SOAP_CONNECT");

    ApiType(String label,String metricValue) {
        this.label = label;
        this.metricValue = metricValue;
    }

    public String label() {
        return label;
    }

    public String getMetricValue() {
        return metricValue;
    }

    private String label;

    //TODO Rename values instead of having a field
    private String metricValue;

    public static ApiType fromLabel(String text) {
        for (ApiType item : values()) {
            if (item.label().equals(text)) {
                return item;
            }
        }
        return null;
    }
}
